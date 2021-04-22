# Google cloud PNR Order POC

* Create maven project using below command
```
mvn archetype:generate -DgroupId=com.infogain.gcp.poc -DartifactId=pnr-order-poc -Dversion=1.0.0 -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

# Spanner DDL scripts
* OUTBOX table
```
CREATE TABLE OUTBOX (
	locator STRING(6) NOT NULL,
	version INT64 NOT NULL,
	parent_locator STRING(6),
	created TIMESTAMP NOT NULL,
	data STRING(MAX) NOT NULL,
) PRIMARY KEY (locator, version)
```

* OUTBOX_STATUS table
```
CREATE TABLE OUTBOX_STATUS (
	locator STRING(6) NOT NULL,
	version INT64 NOT NULL,
	destination STRING(20) NOT NULL,
	status INT64 NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	instance STRING(64) NOT NULL,
	FOREIGN KEY (locator, version) REFERENCES OUTBOX(locator, version),
) PRIMARY KEY (locator, version, destination),
INTERLEAVE IN PARENT OUTBOX ON DELETE CASCADE
```



* processing_locators index
```
CREATE INDEX processing_locators 
ON OUTBOX_STATUS (
	status,
	locator
)
```
 