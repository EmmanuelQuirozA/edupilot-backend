package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.repository.ClassRepository;

import java.util.*;

@Service
public class ClassService {

  @Autowired
  private ClassRepository classRepository;

  @Transactional(readOnly = true)
  public PageResult<Map<String,Object>> getClasses(
		Long token_user_id,
    Long group_id,
    Long school_id,
    String generation,
    String grade_group,
    String scholar_level_name,
    Boolean enabled,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
  ) throws Exception {
    return classRepository.getClasses(
      token_user_id,
      group_id,
      school_id,
      generation,
      grade_group,
      scholar_level_name,
      enabled,
      lang,
      page,
      size,
      exportAll,
      order_by,
      order_dir
    );
  }
}
