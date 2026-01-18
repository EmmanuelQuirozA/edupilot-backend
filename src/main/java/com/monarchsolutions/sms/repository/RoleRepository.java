package com.monarchsolutions.sms.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.monarchsolutions.sms.dto.roles.RolesListResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class RoleRepository {
 
    @PersistenceContext
    private EntityManager entityManager;

    // Get Roles List
    public List<RolesListResponse> getRoles(Long tokenUserId, Long school_id, String lang, int statusFilter){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getRoles");

        // Register IN parameters
        query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("status_filter", Integer.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("token_user_id", tokenUserId);
        query.setParameter("school_id", school_id);
        query.setParameter("lang", lang);
        query.setParameter("status_filter", statusFilter);
        
        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<RolesListResponse> roles = new ArrayList<>();

        for (Object[] data : results) {
            roles.add(mapRoles(data));
        }
        return roles;
    }

    private RolesListResponse mapRoles(Object[] data) {
        RolesListResponse roles = new RolesListResponse();

        roles.setRole_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        roles.setRole_name(data[1] != null ? (String) data[1] : null);
        roles.setRole_description(data[2] != null ? (String) data[2] : null);
        roles.setRole_status(data[3] != null ? (String) data[3] : null);
        roles.setEnabled(data[4] != null ? (Boolean) data[4] : null);
        roles.setSchool_id(data[5] != null ? ((Number) data[5]).longValue() : null);
        roles.setIs_super_admin(data[6] != null ? (Boolean) data[6] : null);
        roles.setName_en(data[7] != null ? (String) data[7] : null);
        roles.setName_es(data[8] != null ? (String) data[8] : null);
        roles.setDescription_en(data[9] != null ? (String) data[9] : null);
        roles.setDescription_es(data[10] != null ? (String) data[10] : null);

        return roles;
    }
}
