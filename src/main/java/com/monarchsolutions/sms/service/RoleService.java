package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.roles.RolesListResponse;
import com.monarchsolutions.sms.entity.Role;
import com.monarchsolutions.sms.repository.RoleEntityRepository;
import com.monarchsolutions.sms.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    public List<RolesListResponse> getRoles(String lang, int status_filter){
        return roleRepository.getRoles(lang, status_filter);
    }

    public List<Role> getRolesForUser(Long tokenUserId, String searchTerm, boolean onlyActive, String lang) {
        String sanitizedSearch = (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm;
        return roleEntityRepository.findRolesForUserSchools(tokenUserId, sanitizedSearch, onlyActive, lang);
    }
}
