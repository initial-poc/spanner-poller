package com.infogain.gcp.poc.poller.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;
import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutBoxStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxStatusService {
	private static final int NEW_STATUS=0;
	private static final int PROCESSING_STATUS=1;
	private static final int COMPLETE_STATUS=2;
	
	private final SpannerOutBoxStatusRepository outBoxStatusRepository;

	@Value(value = "${application.name}")
	private String applicationName;
	
	public OutboxStatusEntity processRecord(OutboxEntity outboxEntity) {

		OutboxStatusEntity outboxStatusEntity = OutboxStatusEntity.builder().version(outboxEntity.getVersion()).locator(outboxEntity.getLocator()).status(PROCESSING_STATUS).destination("TTY").instance(applicationName).created(Timestamp.now()).updated(Timestamp.now()).build();
		log.info("Going to process record {}",outboxStatusEntity);
		outBoxStatusRepository.save(outboxStatusEntity);
		return outboxStatusEntity;
	}
	
	
	public void completeRecord(OutboxStatusEntity processingRecord) {
		
		processingRecord.setStatus(COMPLETE_STATUS);
		processingRecord.setUpdated(Timestamp.now());
		log.info("Completed record {} by application-> {} ",processingRecord,applicationName);
		outBoxStatusRepository.save(processingRecord);
	}
}
