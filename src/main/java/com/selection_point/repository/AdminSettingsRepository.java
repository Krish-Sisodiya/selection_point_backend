package com.selection_point.repository;

import com.selection_point.entity.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSettingsRepository
        extends JpaRepository<AdminSettings, Long> {
}

