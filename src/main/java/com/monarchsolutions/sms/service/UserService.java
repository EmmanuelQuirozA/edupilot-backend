package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.dto.user.UserBalanceDTO;
import com.monarchsolutions.sms.dto.user.UserDetails;
import com.monarchsolutions.sms.dto.user.UserDetailsCache;
import com.monarchsolutions.sms.dto.user.UsersBalanceDTO;
import com.monarchsolutions.sms.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getUsersList(
        Long token_user_id,
        Long user_id,
        Long school_id,
        Long role_id,
        String full_name,
        Boolean enabled,
        String lang,
        int page,
        int size,
        Boolean exportAll,
        String order_by,
        String order_dir) throws Exception {
        // If tokenSchoolId is not null, the SP will filter users by school.
        return userRepository.getUsersList(
            token_user_id,
            user_id,
            school_id,
            role_id,
            full_name,
            enabled,
            lang,
            page,
            size,
            exportAll,
            order_by,
            order_dir
        );
    }

    public String createUser(Long userSchoolId, String lang, Long responsible_user_id, CreateUserRequest request) throws Exception {
        // Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        String jsonResponse = userRepository.createUser(request, userSchoolId, lang, responsible_user_id);

        // Call the repository method that converts the request to JSON and executes the stored procedure
        return jsonResponse;
    }

    public String updateUser(Long userSchoolId, String lang, Long user_id, Long responsible_user_id, UpdateUserRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return userRepository.updateUser(request, userSchoolId, user_id, lang, responsible_user_id);
    }

    public String changeUserStatus(Integer userId, String lang, Long tokenSchoolId, Long responsible_user_id) throws Exception {
        // First, perform the change (this should throw an exception if the action fails)
        String jsonResponse = userRepository.changeUserStatus(userId, lang, tokenSchoolId, responsible_user_id);

        return jsonResponse;
    }

    public UserDetails getUser(Long token_user_id, Long userId, String lang) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        List<UserDetails> rows = userRepository.getUser(token_user_id, userId, lang);
        return rows.stream().findFirst().orElse(null);
    }

    public UserDetailsCache getUserBasic(Long token_user_id, Long userId, String lang) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        List<UserDetailsCache> rows = userRepository.getUserBasic(token_user_id, userId, lang);
        return rows.stream().findFirst().orElse(null);
    }

    public List<UserBalanceDTO> getActiveUserBalances(Long schoolId, String search_criteria) {
        return userRepository.findActiveUserBalancesBySchoolId(schoolId, search_criteria);
    }

    public List<UsersBalanceDTO> getUsersBalance(Long token_user_id, String full_name, String lang) {
        return userRepository.getUsersBalance(token_user_id, full_name, lang);
    }
}
