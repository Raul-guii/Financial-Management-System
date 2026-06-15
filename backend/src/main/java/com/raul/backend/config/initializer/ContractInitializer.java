package com.raul.backend.config.initializer;

import com.raul.backend.entity.Client;
import com.raul.backend.entity.Contract;
import com.raul.backend.entity.ContractItem;
import com.raul.backend.entity.User;
import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.ContractStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractItemRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//@Component
@Order(4)
@Profile("dev")
@RequiredArgsConstructor
public class ContractInitializer implements CommandLineRunner {

    private final ContractRepository contractRepository;
    private final ContractItemRepository contractItemRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        System.out.println("Criando contratos...");
        if (contractRepository.count() > 0) return;

        User user = userRepository.findByEmail("admin@sgf.com")
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Client> clients = clientRepository.findAll()
                .stream().limit(100).toList();

        BillingPeriod[] periods = BillingPeriod.values();

        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);

            Contract contract = new Contract();
            contract.setClient(client);
            contract.setCreatedBy(user);
            contract.setStatus(ContractStatus.ACTIVE);
            contract.setBillingPeriod(periods[i % periods.length]);
            contract.setStartDate(LocalDate.now().withDayOfMonth(1).minusMonths(2));
            contract.setEndDate(LocalDate.now().withDayOfMonth(1).plusMonths(10));

            contract = contractRepository.save(contract);
            createItems(contract, i);
        }

        System.out.println("100 contratos criados.");
    }

    private void createItems(Contract contract, int i) {

        String[] services = {
                "Hospedagem",
                "Consultoria",
                "Suporte",
                "Licença",
                "Monitoramento"
        };

        for (int j = 1; j <= 3; j++) {

            ContractItem item = new ContractItem();

            item.setContract(contract);

            item.setName(
                    services[(i + j) % services.length]
            );

            item.setDescription(
                    "Serviço de " + item.getName()
            );

            item.setQuantity(BigDecimal.valueOf(j));

            item.setUnitPrice(
                    BigDecimal.valueOf(100 + (i % 500))
            );

            item.setActive(true);

            contractItemRepository.save(item);
        }
    }
}
