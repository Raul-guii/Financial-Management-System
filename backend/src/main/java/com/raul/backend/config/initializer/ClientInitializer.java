package com.raul.backend.config.initializer;

import com.raul.backend.entity.Client;
import com.raul.backend.entity.User;
import com.raul.backend.enums.ClientType;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientInitializer implements CommandLineRunner {

    private final ClientRepository repository;
    private final UserRepository userRepository;

    @Override
    @Order(3)
    public void run(String... args) {

        if (repository.count() > 0) return;

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuário padrão não encontrado"));

        // CPF
        save(
                "João Silva",
                ClientType.PERSON,
                false,
                "52998224725",
                "joao.silva@email.com",
                "61999990001",
                "Rua das Flores",
                "120",
                "Centro",
                "Formosa",
                "GO",
                "73800000",
                "Brasil",
                user
        );

        save(
                "Maria Oliveira",
                ClientType.PERSON,
                false,
                "12345678909",
                "maria.oliveira@email.com",
                "61999990002",
                "Avenida Brasil",
                "450",
                "Bosque",
                "Brasília",
                "DF",
                "70000000",
                "Brasil",
                user
        );

        save(
                "Carlos Pereira",
                ClientType.PERSON,
                false,
                "11144477735",
                "carlos.pereira@email.com",
                "61999990003",
                "Rua Goiás",
                "89",
                "Jardim América",
                "Goiânia",
                "GO",
                "74000000",
                "Brasil",
                user
        );

        // CNPJ
        save(
                "Tech Solutions LTDA",
                ClientType.COMPANY,
                false,
                "11222333000181",
                "contato@techsolutions.com",
                "1133334444",
                "Rua da Tecnologia",
                "1000",
                "Industrial",
                "São Paulo",
                "SP",
                "01000000",
                "Brasil",
                user
        );

        save(
                "Mercado Central ME",
                ClientType.COMPANY,
                false,
                "11444777000161",
                "financeiro@mercadocentral.com",
                "6233335555",
                "Avenida Central",
                "250",
                "Setor Oeste",
                "Anápolis",
                "GO",
                "75000000",
                "Brasil",
                user
        );

        save(
                "Alpha Sistemas SA",
                ClientType.COMPANY,
                false,
                "12345678000195",
                "suporte@alphasistemas.com",
                "2133336666",
                "Rua Empresarial",
                "700",
                "Centro",
                "Rio de Janeiro",
                "RJ",
                "20000000",
                "Brasil",
                user
        );
    }

    private void save(
            String name,
            ClientType type,
            Boolean defaulter,
            String document,
            String email,
            String phone,
            String street,
            String number,
            String neighborhood,
            String city,
            String state,
            String postalCode,
            String country,
            User user
    ) {

        Client client = new Client();

        client.setName(name);
        client.setType(type);
        client.setDefaulter(defaulter);
        client.setDocument(document);
        client.setEmail(email);
        client.setPhone(phone);

        client.setAddressStreet(street);
        client.setAddressNumber(number);
        client.setAddressNeighborhood(neighborhood);
        client.setAddressCity(city);
        client.setAddressState(state);
        client.setAddressPostalCode(postalCode);
        client.setAddressCountry(country);

        client.setActive(true);

        client.setCreatedBy(user);

        repository.save(client);
    }
}