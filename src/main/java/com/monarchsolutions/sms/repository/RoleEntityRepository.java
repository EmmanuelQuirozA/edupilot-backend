package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends JpaRepository<Role, Long> {
}
