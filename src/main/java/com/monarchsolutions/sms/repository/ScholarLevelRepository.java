package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.ScholarLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarLevelRepository extends JpaRepository<ScholarLevel, Long> {
}
