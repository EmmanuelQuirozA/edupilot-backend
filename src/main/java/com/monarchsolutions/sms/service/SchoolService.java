package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.repository.SchoolRepository;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class SchoolService {
	

	@Value("${app.schools_logos-dir}")
	private String schools_logosDir;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
  	private ObjectMapper objectMapper;

	@Transactional(readOnly = true)
	public PageResult<Map<String,Object>> getSchools(
		Long   p_token_user_id,
		Long   p_school_id,
		String lang,
		Integer p_status_filter,
		String school_name,
		Integer p_offset,
		Integer p_limit,
		boolean p_export_all,
		String p_order_by,
		String p_order_dir
	) throws Exception {
		return schoolRepository.getSchools(
			p_token_user_id,
			p_school_id,
			lang,
			p_status_filter,
			school_name,
			p_offset,
			p_limit,
			p_export_all,
			p_order_by,
			p_order_dir
		);
	}

	public List<SchoolsList> getSchoolsList(Long token_user_id, Long school_id, String lang, int statusFilter) {
			return schoolRepository.getSchoolsList(token_user_id, school_id, lang, statusFilter);
	}

        public List<SchoolsList> getRelatedSchoolList(Long user_school_id, Long school_id, String lang) {
                return schoolRepository.getRelatedSchoolList(user_school_id, school_id, lang);
        }

        public Map<String, Object> getSchoolDetails(Long tokenUserId, Long schoolId, String lang) throws Exception {
                return schoolRepository.getSchoolDetails(tokenUserId, schoolId, lang);
        }

	public String createSchool(CreateSchoolRequest request) throws Exception {
		// Call the repository method that converts the request to JSON and executes the stored procedure
		return schoolRepository.createSchool(request);
	}

	public String updateSchool(Long p_token_user_id, Long schoolId, UpdateSchoolRequest request, String lang, MultipartFile newImage) throws Exception {
			
		// 1) If there's a new image, store it to disk and set its filename on the DTO
		Path logoDir = Paths.get(schools_logosDir);
		if (newImage != null && !newImage.isEmpty()) {
				Files.createDirectories(logoDir);

				String original   = StringUtils.cleanPath(newImage.getOriginalFilename());
				String timestamp  = LocalDateTime.now()
															.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
				String storedName = timestamp + "_" + original;

				try (InputStream in = newImage.getInputStream()) {
						Files.copy(in, logoDir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
				}

				request.setImage(storedName);
		}

		// 2) Call the SP via your repository; expect a JSON string with "oldImage" if it changed
		String jsonResponse = schoolRepository.updateSchool(
				p_token_user_id,
				schoolId,
				request,
				lang
		);

		// 3) Parse the JSON, check for oldImage, and delete the old file if present
		JsonNode root = objectMapper.readTree(jsonResponse);
		boolean success = root.path("success").asBoolean(false);

		if (success && root.has("oldImage")) {
				String oldImage = root.path("oldImage").asText(null);
				if (oldImage != null && !oldImage.isBlank()) {
						Path oldPath = logoDir.resolve(oldImage);
						try {
								Files.deleteIfExists(oldPath);
						} catch (IOException e) {
								// Log and swallow; we don't want a delete-failure to break the update
								LoggerFactory.getLogger(getClass())
											.warn("Failed to delete old school logo: {}", oldPath, e);
						}
				}
		}

		// 4) Return the original SP response to the controller
		return jsonResponse;
	}

	public String changeSchoolStatus(Long tokenSchoolId, Long school_id, String lang) {
		return schoolRepository.changeSchoolStatus(tokenSchoolId, school_id, lang);
	}

	public String getSchoolImage(Long token_user_id, Long school_id) {
		return schoolRepository.getSchoolImage(token_user_id, school_id);
	}

	public String getUserSchool(Long token_user_id) {
		return schoolRepository.getUserSchool(token_user_id);
	}

}
