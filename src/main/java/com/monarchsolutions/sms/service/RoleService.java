package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.roles.RolesListResponse;
import com.monarchsolutions.sms.entity.Role;
import com.monarchsolutions.sms.repository.RoleEntityRepository;
import com.monarchsolutions.sms.repository.RoleProcedureRepository;
import com.monarchsolutions.sms.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    @Autowired
    private RoleProcedureRepository roleProcedureRepository;

    public List<RolesListResponse> getRoles(Long token_user_id, Long school_id, String lang, int status_filter){
        return roleRepository.getRoles(token_user_id, school_id, lang, status_filter);
    }

    public List<Role> getRolesForUser(Long tokenUserId, String searchTerm, boolean onlyActive, String lang) {
        String sanitizedSearch = (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm;
        return roleEntityRepository.findRolesForUserSchools(tokenUserId, sanitizedSearch, onlyActive, lang);
    }

    public Map<String, Object> createRole(Long tokenUserId, Object payload, String lang) throws Exception {
        return roleProcedureRepository.createRole(tokenUserId, payload, lang);
    }

    public Map<String, Object> updateRole(Long tokenUserId, Long roleId, Object payload, String lang) throws Exception {
        return roleProcedureRepository.updateRole(tokenUserId, roleId, payload, lang);
    }
}
