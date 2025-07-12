package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.groups.CreateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GetClassesCatalog;
import com.monarchsolutions.sms.dto.groups.GroupsListResponse;
import com.monarchsolutions.sms.dto.groups.UpdateGroupRequest;
import com.monarchsolutions.sms.repository.GroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;

    public List<GroupsListResponse> getGroupsList(Long tokenSchoolId, Long school_id, String lang, Integer status_filter) {
        // If tokenSchoolId is not null, the SP will filter users by school.
        return groupRepository.getGroupsList(tokenSchoolId, school_id, lang, status_filter);
    }

    public String createGroup(Long userSchoolId, String lang, CreateGroupRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return groupRepository.createGroup(userSchoolId, lang, request);
    }

    public String updateGroup(Long userSchoolId, Long group_id, String lang, UpdateGroupRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return groupRepository.updateGroup(userSchoolId, group_id, lang, request);
    }

    public String changeGroupStatus(Long tokenSchoolId, Long userId, String lang) {
        // Optionally, you could validate that the token's school allows changing the status.
        return groupRepository.changeGroupStatus(tokenSchoolId, userId, lang);
    }

    public List<GetClassesCatalog> getClassesCatalog(Long token_user_id, Long school_id, String lang) {
        // If tokenSchoolId is not null, the SP will filter users by school.
        return groupRepository.getClassesCatalog(token_user_id, school_id, lang);
    }
}
