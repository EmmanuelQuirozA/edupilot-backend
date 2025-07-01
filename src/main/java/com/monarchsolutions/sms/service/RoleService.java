package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.roles.RolesListResponse;
import com.monarchsolutions.sms.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    public List<RolesListResponse> getRoles(String lang, int role_level, int status_filter){
        return roleRepository.getRoles(lang, role_level, status_filter);
    }
}
