package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.catalogs.ModuleAccessResponse;
import com.monarchsolutions.sms.repository.ModuleAccessProjection;
import com.monarchsolutions.sms.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<ModuleAccessResponse> getAccessibleModules(
            Long tokenUserId,
            String searchTerm,
            boolean onlyActive,
            String lang,
            String moduleKey
    ) {
        String sanitizedSearch = (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm;
        String sanitizedModuleKey = (moduleKey == null || moduleKey.isBlank()) ? null : moduleKey;

        List<ModuleAccessProjection> projections = moduleRepository.findModulesForUserSchools(
                tokenUserId,
                sanitizedSearch,
                onlyActive,
                lang,
                sanitizedModuleKey
        );

        return projections.stream()
                .map(projection -> {
                    ModuleAccessResponse response = new ModuleAccessResponse();
                    response.setModuleId(projection.getModuleId());
                    response.setModuleName(projection.getModuleName());
                    response.setModuleKey(projection.getModuleKey());
                    response.setModuleAccessControlId(projection.getModuleAccessControlId());
                    response.setSchoolId(projection.getSchoolId());
                    response.setEnabled(projection.getEnabled());
                    response.setSortOrder(projection.getSortOrder());
                    return response;
                })
                .toList();
    }
}
