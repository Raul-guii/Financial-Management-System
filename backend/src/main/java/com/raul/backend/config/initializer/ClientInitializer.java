package com.raul.backend.config.initializer;

import com.raul.backend.entity.Client;
import com.raul.backend.entity.User;
import com.raul.backend.enums.ClientType;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientInitializer implements CommandLineRunner {

    private final ClientRepository repository;
    private final UserRepository userRepository;

    @Override
    @Profile("dev")
    @Order(3)
    public void run(String... args) {

        System.out.println("[ClientInitializer] Iniciando criação de clientes...");

        if (repository.count() > 0) return;

        User user = userRepository.findByEmail("admin@sgf.com")
                .orElseThrow(() -> new RuntimeException("Usuário padrão não encontrado"));

        String[] cities = {
                "São Paulo", "Rio de Janeiro", "Belo Horizonte",
                "Curitiba", "Goiânia", "Brasília",
                "Salvador", "Fortaleza"
        };

        String[] states = {
                "SP", "RJ", "MG", "PR",
                "GO", "DF", "BA", "CE"
        };

        for (int i = 1; i <= 1000; i++) {

            boolean company = i % 2 == 0;

            save(
                    company
                            ? "Empresa " + i + " LTDA"
                            : "Cliente " + i,

                    company
                            ? ClientType.COMPANY
                            : ClientType.PERSON,

                    false,

                    generateDocument(i, company),

                    "cliente" + i + "@email.com",

                    "6199999" + String.format("%04d", i),

                    "Rua " + i,

                    String.valueOf(i),

                    "Centro",

                    cities[i % cities.length],

                    states[i % states.length],

                    "73800" + String.format("%03d", i % 999),

                    "Brasil",

                    user
            );
        }

        System.out.println("1000 clientes criados com sucesso.");
    }

    private String generateDocument(int i, boolean company) {

        if (company) {
            return String.format("%014d", i);
        }

        return String.format("%011d", i);
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