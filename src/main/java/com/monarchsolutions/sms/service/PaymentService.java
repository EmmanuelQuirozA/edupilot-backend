 package com.monarchsolutions.sms.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.payments.ByMonthPaymentsDTO;
import com.monarchsolutions.sms.dto.payments.ByYearPaymentsDTO;
import com.monarchsolutions.sms.dto.payments.CreatePayment;
import com.monarchsolutions.sms.dto.payments.StudentPaymentsDTO;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;
import com.monarchsolutions.sms.repository.PaymentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {
  
  // ← make this a String
  @Value("${app.upload-dir}")
  private String uploadDir;
  private final PaymentRepository paymentRepository;
  private final ObjectMapper objectMapper;

  public PaymentService(PaymentRepository paymentRepository,
                        ObjectMapper objectMapper) {
    this.paymentRepository = paymentRepository;
    this.objectMapper     = objectMapper;
  }


	@Autowired
	private PaymentRepository repo;

	public String createPayment(Long tokenUserId, CreatePayment req, String lang) {
			return repo.createPayment(tokenUserId, req, lang);
	}

	/** Fetch just one payment’s detail by its ID (and language). */
  public Map<String,Object> getPaymentById(Long paymentId, String lang) throws SQLException {
    // schoolId, studentId, paymentRequestId all null, filters null, page=0,size=1
    PageResult<Map<String,Object>> page = paymentRepository.getPayments(
      null,null, null, paymentId, null,
      null, null, null, null, null, null,
      null, null,
      lang,
      0, 1,
      false,
      null, null
    );
    if (page.getContent().isEmpty()) {
      throw new NoSuchElementException("Payment not found: " + paymentId);
    }
    return page.getContent().get(0);
  }

  public String updatePayment(
      Long responsibleUserId,
      Long paymentId,
      UpdatePaymentDTO dto,
      boolean removeReceipt,
      MultipartFile newReceipt,
      String lang
  ) throws Exception {
    // 1) If removeReceipt, fetch paymentData record & delete its file
    if (removeReceipt) {
      Map<String,Object> paymentData = getPaymentById(paymentId, lang);
      String oldPath = (String) paymentData.get("receipt_path");
      if (StringUtils.hasText(oldPath)) {
        Files.deleteIfExists(Paths.get(uploadDir).resolve(oldPath));
      }
      dto.setReceipt_path(null);
      dto.setReceipt_file_name(null);
    }

    // 2) If there's a new file, save it exactly like in createPayment()
    if (newReceipt != null && !newReceipt.isEmpty()) {
      Map<String,Object> paymentData = getPaymentById(paymentId, lang);
      String original  = StringUtils.cleanPath(newReceipt.getOriginalFilename());
      String timestamp = LocalDateTime.now()
                             .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
      String storedName= timestamp + "-" + paymentData.get("student_id") + "_" + original;

      Path dir = Paths.get(uploadDir);
      Files.createDirectories(dir);
      try (InputStream in = newReceipt.getInputStream()) {
        Files.copy(in, dir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
      }
      dto.setReceipt_file_name(original);
      dto.setReceipt_path(storedName);
    }

    // 3) Delegate to your repository — pass the DTO, not a JSON string
    return paymentRepository.updatePayment(
      responsibleUserId,
      paymentId,
      dto,
			removeReceipt,
      lang
    );
  }

  public List<ByYearPaymentsDTO> getGroupedPayments(
    Long tokenUserId,
    Integer paymentId,
    Integer paymentRequestId,
    String  ptName,
    LocalDate paymentMonth,
    LocalDate paymentCreatedAt,
    Boolean tuitions,
    String  lang
  ) {
    List<StudentPaymentsDTO> flat = repo.getGroupedPayments(
      tokenUserId, paymentId, paymentRequestId,
      ptName, paymentMonth, paymentCreatedAt,
      tuitions, lang
    );

    // 2) drop any rows with null paymentMonth
    List<StudentPaymentsDTO> withDates = flat.stream()
      .filter(p -> p.getPaymentMonth() != null)
      .collect(Collectors.toList());

    // 3) group by year → month
    Map<Integer, Map<Integer, List<StudentPaymentsDTO>>> grouped;
    // If tuition. group by year → payment_month
    if (tuitions) {
      grouped = withDates.stream()
        .collect(Collectors.groupingBy(
          p -> p.getPaymentMonth().getYear(),
          LinkedHashMap::new,
          Collectors.groupingBy(
            p -> p.getPaymentMonth().getMonthValue(),
            LinkedHashMap::new,
            Collectors.toList()
          )
        ));
    // Else. group by year → payment.created_at
    } else {
      grouped = flat.stream()
        .collect(Collectors.groupingBy(
          p -> p.getPaymentCreatedAt().getYear(),
          LinkedHashMap::new,
          Collectors.groupingBy(
            p -> p.getPaymentCreatedAt().getMonthValue(),
            LinkedHashMap::new,
            Collectors.toList()
          )
        ));
    }

    // 4) build the DTOs
    List<ByYearPaymentsDTO> result = new ArrayList<>();
    for (var yearEntry : grouped.entrySet()) {
      int year = yearEntry.getKey();
      List<ByMonthPaymentsDTO> months = new ArrayList<>();

      for (var monthEntry : yearEntry.getValue().entrySet()) {
        int month = monthEntry.getKey();
        List<StudentPaymentsDTO> items = monthEntry.getValue();

        BigDecimal total = items.stream()
          .map(StudentPaymentsDTO::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

        months.add(new ByMonthPaymentsDTO(month, total, items));
      }

      result.add(new ByYearPaymentsDTO(year, months));
    }

    return result;
  }

}
