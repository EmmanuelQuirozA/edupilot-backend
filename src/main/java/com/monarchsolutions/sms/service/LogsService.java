package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.ChangeDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.PaymentRequestLogGroupDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.PaymentRequestLogsDto;
import com.monarchsolutions.sms.dto.userLogs.payments.ChangePaymentDto;
import com.monarchsolutions.sms.dto.userLogs.payments.PaymentLogGroupDto;
import com.monarchsolutions.sms.dto.userLogs.payments.PaymentLogsDto;
import com.monarchsolutions.sms.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class LogsService {

	@Autowired
	private LogsRepository logsRepository;

	public List<UserLogsListDto> getUsersActivityLog(Long tokenSchoolId, String lang) {
		// If tokenSchoolId is not null, the SP will filter students by school.
		return logsRepository.getUsersActivityLog(tokenSchoolId, lang);
	}

	public List<PaymentRequestLogGroupDto> getGroupedRequestLogs(
		Long token_user_id,
		Long tokenSchoolId,
		Long paymentRequestId,
		String lang
	) {
		// 1) Fetch the flat list of row‐level logs
		List<PaymentRequestLogsDto> flat = logsRepository.getPaymentRequestLogs(
			token_user_id,tokenSchoolId, paymentRequestId, lang
		);

		// 2) Group by the tuple of (payment_request_id, responsible_user_id, full_name, role, updated_at)
		Map<List<Object>, List<PaymentRequestLogsDto>> byGroup =
			flat.stream()
				.collect(Collectors.groupingBy(log ->
					Arrays.asList(
						log.getPayment_request_id(),
						log.getResponsable_user_id(),
						log.getResponsable_full_name(),
						log.getRole_name(),
						log.getUpdated_at(),
						log.getLog_type_name()
					)
				));

		// 3) Transform each group into a PaymentRequestLogGroupDto
		List<PaymentRequestLogGroupDto> grouped = byGroup.entrySet().stream()
		.map(entry -> {
			List<Object> key = entry.getKey();
			List<PaymentRequestLogsDto> rows = entry.getValue();

			PaymentRequestLogGroupDto group = new PaymentRequestLogGroupDto();
			group.setPayment_request_id((Long) key.get(0));
			group.setResponsable_user_id((Long) key.get(1));
			group.setResponsable_full_name((String) key.get(2));
			group.setRole_name((String) key.get(3));
			group.setUpdated_at((LocalDateTime) key.get(4));
			group.setLog_type_name((String) key.get(5));

			List<ChangeDto> changes = rows.stream()
				.map(r -> new ChangeDto(
					r.getField(),
					r.getFrom(),
					r.getTo(),
					r.getComments()
				))
				.collect(Collectors.toList());

			group.setChanges(changes);
			return group;
		})
		.collect(Collectors.toList());

		// **HERE** re-sort your grouped list by updated_at descending
		grouped.sort(Comparator.comparing(PaymentRequestLogGroupDto::getUpdated_at).reversed());
	
		return grouped;
	}

	// ---------------------------------------
        public List<PaymentLogGroupDto> getGroupedPaymentLogs(
                Long token_user_id,
                Long tokenSchoolId,
                Long paymentId,
                String lang
        ) {
		// 1) Fetch the flat list of row‐level logs
		List<PaymentLogsDto> flat = logsRepository.getPaymentLogs(
			token_user_id,tokenSchoolId, paymentId, lang
		);

		// 2) Group by the tuple of (payment_id, responsible_user_id, full_name, role, updated_at)
		Map<List<Object>, List<PaymentLogsDto>> byGroup =
			flat.stream()
				.collect(Collectors.groupingBy(log ->
					Arrays.asList(
						log.getPayment_id(),
						log.getResponsable_user_id(),
						log.getResponsable_full_name(),
						log.getRole_name(),
						log.getUpdated_at(),
						log.getLog_type_name()
					)
				));

		// 3) Transform each group into a PaymentLogGroupDto
		List<PaymentLogGroupDto> grouped = byGroup.entrySet().stream()
		.map(entry -> {
			List<Object> key = entry.getKey();
			List<PaymentLogsDto> rows = entry.getValue();

			PaymentLogGroupDto group = new PaymentLogGroupDto();
			group.setPayment_id((Long) key.get(0));
			group.setResponsable_user_id((Long) key.get(1));
			group.setResponsable_full_name((String) key.get(2));
			group.setRole_name((String) key.get(3));
			group.setUpdated_at((LocalDateTime) key.get(4));
			group.setLog_type_name((String) key.get(5));

			List<ChangePaymentDto> changes = rows.stream()
				.map(r -> new ChangePaymentDto(
					r.getField(),
					r.getFrom(),
					r.getTo(),
					r.getComments()
				))
				.collect(Collectors.toList());

			group.setChanges(changes);
			return group;
		})
		.collect(Collectors.toList());

		// **HERE** re-sort your grouped list by updated_at descending
		grouped.sort(Comparator.comparing(PaymentLogGroupDto::getUpdated_at).reversed());

                return grouped;
        }

        public List<Map<String, Object>> getScheduledJobLogs(
                Long tokenUserId,
                Long paymentRequestScheduledId,
                String lang
        ) {
                return logsRepository.getScheduledJobLogs(tokenUserId, paymentRequestScheduledId, lang);
        }
}
