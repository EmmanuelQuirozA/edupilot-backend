package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.groups.CreateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GroupsListResponse;
import com.monarchsolutions.sms.dto.groups.UpdateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GetClassesCatalog;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Get Groups List
    public List<GroupsListResponse> getGroupsList(Long tokenSchoolId, Long school_id, String lang, Integer status_filter) {
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getGroupsList");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("status_filter", Integer.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("school_id", school_id);
        query.setParameter("lang", lang);
        query.setParameter("status_filter", status_filter);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<GroupsListResponse> groups = new ArrayList<>();

        for (Object[] data : results) {
            groups.add(mapGroup(data));
        }
        return groups;
    }

    private GroupsListResponse mapGroup(Object[] data) {
        GroupsListResponse group = new GroupsListResponse();

        group.setGroup_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        group.setSchool_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        group.setScholar_level_id(data[2] != null ? ((Number) data[2]).longValue() : null);
        group.setName(data[3] != null ? (String) data[3] : null);
        group.setGeneration(data[4] != null ? (String) data[4] : null);
        group.setGrade_group(data[5] != null ? (String) data[5] : null);
        group.setGrade(data[6] != null ? (String) data[6] : null);
        group.setGroup(data[7] != null ? (String) data[7] : null);
        group.setScholar_level_name(data[8] != null ? (String) data[8] : null);
        group.setEnabled(data[9] != null ? (Boolean) data[9] : null);
        group.setGroup_status(data[10] != null ? (String) data[10] : null);
        group.setSchool_description(data[11] != null ? (String) data[11] : null);
        
        return group;
    }

    // Create Group
    public String createGroup(Long userSchoolId, String lang, CreateGroupRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String groupDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createGroup");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("group_data", String.class, ParameterMode.IN);

        // Set the parameters. If user_school_id is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("lang", lang);
        query.setParameter("group_data", groupDataJson);

        // Execute the stored procedure
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    public String updateGroup(Long userSchoolId, Long group_id, String lang, UpdateGroupRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String groupDataJson = objectMapper.writeValueAsString(request);
        
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateGroup");
        
        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("group_data", String.class, ParameterMode.IN);
        
        // Set the parameters. If userSchoolId is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("p_group_id", group_id);
        query.setParameter("lang", lang);
        query.setParameter("group_data", groupDataJson);

        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    public String changeGroupStatus(Long tokenSchoolId, Long group_id, String lang) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("changeGroupStatus");
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("p_group_id", group_id);
        query.setParameter("lang", lang);

        // If the group does not exist, the stored procedure will signal an error.
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

	public List<GetClassesCatalog>  getClassesCatalog(Long token_user_id, Long school_id, String lang) {
		String sql = """
			SELECT
                g.group_id,
                g.generation,
				CASE WHEN :lang = 'en' THEN sl.name_en ELSE sl.name_es END AS scholar_level_name,
                CONCAT(g.grade,'-',g.`group`) AS grade_group
			FROM `groups` g

				/* 1) bring in the caller so we know their school */
				JOIN users uc
					ON uc.user_id = :token_user_id
                    
				LEFT JOIN schools        sc ON g.school_id = sc.school_id
				LEFT JOIN scholar_levels sl ON g.scholar_level_id = sl.scholar_level_id

			WHERE g.enabled = 1
                AND g.school_id = :school_id

				/* 2) only users in the same or a related school as the caller */
				OR (
						sc.school_id         = uc.school_id
					OR sc.related_school_id = uc.school_id
				);
		""";

		Query q = entityManager.createNativeQuery(sql);
		// bind parameters _without_ the leading colon:
		q.setParameter("token_user_id",	token_user_id);
		q.setParameter("lang",      			lang);
		q.setParameter("school_id",      			school_id);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<GetClassesCatalog> list = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      GetClassesCatalog dto = new GetClassesCatalog();
      dto.setGroup_id( ((Number) r[0]).longValue());
      dto.setGeneration((String) r[1]);
      dto.setScholar_level_name((String) r[2]);
      dto.setGrade_group((String) r[3]);
      list.add(dto);
    }
    return list;
	}

}
