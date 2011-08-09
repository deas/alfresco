CREATE TABLE version
     (vers VARCHAR(50));

CREATE TABLE alf_namespace (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  version BIGINT NOT NULL,
  uri VARCHAR(100) UNIQUE
);

CREATE TABLE alf_qname (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  version BIGINT NOT NULL,
  ns_id BIGINT NOT NULL,
  local_name VARCHAR(200) NOT NULL,
  CONSTRAINT alf_qname_ibfk_1_UNIQUE UNIQUE (ns_id,local_name),
  CONSTRAINT alf_qname_ibfk_1 FOREIGN KEY (ns_id) REFERENCES alf_namespace (id)
);

CREATE TABLE alf_lock_resource (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  version BIGINT NOT NULL,
  qname_ns_id BIGINT NOT NULL,
  qname_localname VARCHAR(255) NOT NULL,
  CONSTRAINT idx_alf_lockr_key UNIQUE (qname_ns_id,qname_localname),
  CONSTRAINT fk_alf_lockr_ns FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace (id)
);

CREATE TABLE alf_lock (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  version BIGINT NOT NULL,
  shared_resource_id BIGINT NOT NULL,
  excl_resource_id BIGINT NOT NULL,
  lock_token VARCHAR(36) NOT NULL,
  start_time BIGINT NOT NULL,
  expiry_time BIGINT NOT NULL,
  CONSTRAINT idx_alf_lock_key UNIQUE (shared_resource_id,excl_resource_id),
  CONSTRAINT fk_alf_lock_excl FOREIGN KEY (excl_resource_id) REFERENCES alf_lock_resource (id),
  CONSTRAINT fk_alf_lock_shared FOREIGN KEY (shared_resource_id) REFERENCES alf_lock_resource (id)
);

CREATE INDEX idx_fk_alf_lock_excl ON alf_lock ( excl_resource_id );

