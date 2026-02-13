package com.selection_point.mapper;

import com.selection_point.entity.AdminSettings;
import com.selection_point.repository.AdminSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSettingsLoader implements CommandLineRunner {

    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Override
    public void run(String... args) {

        if (adminSettingsRepository.findById(1L).isEmpty()) {

            AdminSettings s = new AdminSettings();
            s.setId(1L);
            s.setUpiId("9754525446@ybl");   // 🔴 apna UPI
            s.setInstituteName("Selection Point");

            adminSettingsRepository.save(s);

            System.out.println("✅ AdminSettings initialized");
        }
    }
}

