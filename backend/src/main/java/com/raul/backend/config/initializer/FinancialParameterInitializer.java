package com.raul.backend.config.initializer;

import com.raul.backend.entity.FinancialParameter;
import com.raul.backend.entity.User;
import com.raul.backend.enums.FinancialParameterCategory;
import com.raul.backend.enums.FinancialParameterType;
import com.raul.backend.repository.FinancialParameterRepository;
import com.raul.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class FinancialParameterInitializer implements CommandLineRunner {

    private final FinancialParameterRepository repository;
    private final UserRepository userRepository;

    @Override
    @Order(2)
    public void run(String... args) {

        if (repository.count() > 0) return; // evita duplicar

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User padrão não encontrado"));

        save("DAILY_INTEREST", "0.02", "DECIMAL", "PERCENTAGE", "Juros diário por atraso", user);
        save("LATE_FEE", "2.5", "DECIMAL", "PERCENTAGE", "Multa por atraso", user);
        save("GRACE_PERIOD_DAYS", "5", "INTEGER", "DAYS", "Dias antes de considerar atraso", user);
        save("DEFAULT_THRESHOLD_DAYS", "30", "INTEGER", "DAYS", "Dias para inadimplência", user);
        save("AUTO_CHARGE_ENABLED", "1", "BOOLEAN", "FLAG", "Ativa cobrança automática", user);
        save("MINIMUM_CHARGE_AMOUNT", "10", "DECIMAL", "MONETARY", "Valor mínimo", user);
    }

    private void save(String name, String value, String type, String category, String desc, User user) {

        FinancialParameter p = new FinancialParameter();

        p.setName(name);
        p.setValue(new BigDecimal(value));
        p.setType(FinancialParameterType.valueOf(type));
        p.setCategory(FinancialParameterCategory.valueOf(category));
        p.setDescription(desc);
        p.setActive(true);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        repository.save(p);
    }
}