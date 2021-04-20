package com.infogain.gcp.poc.poller.service;

import java.net.InetAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class PnrService {

	private final OutboxStatusService outboxStatusService;
	private final SpannerOutboxRepository spannerOutboxRepository;
	private final String ip ;
	
@Autowired
@SneakyThrows
	public PnrService(OutboxStatusService outboxStatusService, SpannerOutboxRepository spannerOutboxRepository) {
		this.outboxStatusService = outboxStatusService;
		this.spannerOutboxRepository = spannerOutboxRepository;
		ip= InetAddress.getLocalHost().getHostAddress();
	}



	@Value(value="${delay}")
	private int processDelay;
	public void processRecords() {

		log.info("Getting record to process by application->  {}", ip);
		List<OutboxEntity> recordToProcess = spannerOutboxRepository.getRecordToProcess();
		if (recordToProcess.isEmpty()) {
			log.info("No Record to process");
			return;
		}
		log.info("total record - {} to process by application->  {}", recordToProcess.size(), ip);
		log.info("RECORD {}", recordToProcess);

		try {
			Thread.sleep(processDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		process(recordToProcess);
	}

	@Transactional(rollbackFor = Exception.class)
	private void process(List<OutboxEntity> outboxEntities) {
		outboxEntities.stream().forEach(outboxEntity -> {
			OutboxStatusEntity processingRecord = outboxStatusService.processRecord(outboxEntity);
			outboxStatusService.completeRecord(processingRecord);

		});
	}

}
