package com.infogain.gcp.poc.poller.repository;

import java.util.List;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;
import org.springframework.cloud.gcp.data.spanner.repository.query.Query;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;

public interface SpannerOutboxRepository extends SpannerRepository<OutboxEntity	, String> {
	@Query(value = "SELECT o.* FROM OUTBOX o LEFT JOIN OUTBOX_STATUS os USING(locator,version) WHERE (os.status is NULL OR os.status = 0) and o.locator not IN ( select locator from OUTBOX where locator in (select distinct locator from OUTBOX_STATUS where status=1) or parent_locator in (select distinct locator from OUTBOX_STATUS where status=1) UNION ALL select parent_locator from OUTBOX where parent_locator is not null AND locator in (select distinct locator from OUTBOX_STATUS where status=1) or parent_locator in (select distinct locator from OUTBOX_STATUS where status=1)) ORDER BY o.created LIMIT 10 ")
	public List<OutboxEntity> getRecordToProcess();
}
