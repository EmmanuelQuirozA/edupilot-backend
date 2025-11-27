package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Module;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByKey(String key);
}
