package com.infogain.gcp.poc.poller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PollerPnrService {
	
	private final OutboxStatusService outboxStatusService;
	private final SpannerOutboxRepository spannerOutboxRepository;
	
	@Value(value = "${application.name}")
	private String applicationName;
	public void processRecords(){
		 
		log.info("Getting record to process by application->  {}",applicationName);
		List<OutboxEntity> recordToProcess = spannerOutboxRepository.getRecordToProcess();
		if(recordToProcess.isEmpty()) {
			log.info("No Record to process");
			return ;
		}
		log.info("total record - {} to process by application->  {}",recordToProcess.size(),applicationName);
		 log.info("RECORD {}",recordToProcess);
		 process(recordToProcess);
	}

	private void process(List<OutboxEntity> outboxEntities) {
		outboxEntities. stream().forEach(outboxEntity-> {
			OutboxStatusEntity processingRecord = outboxStatusService.processRecord(outboxEntity);
			outboxStatusService.completeRecord(processingRecord);
			
		});
	}
	 
}
