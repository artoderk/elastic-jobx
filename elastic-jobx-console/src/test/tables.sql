-- Postgres
CREATE TABLE job_trigger_history
(
  id serial NOT NULL,
  namespace character varying(64),
  job_name character varying(128),
  sharding_count character varying(64),
  sharding_item smallint,
  server_ip character varying(15),
  status smallint,
  begin_time timestamp without time zone,
  complete_time timestamp without time zone,
  next_fire_time timestamp without time zone,
  CONSTRAINT job_this_pkey PRIMARY KEY (namespace, job_name, sharding_item, begin_time)
);
-- create index idx_job_this_ns on job_trigger_history(namespace);
-- create index idx_job_this_jn on job_trigger_history(job_name);
-- create index idx_job_this_bt on job_trigger_history(begin_time);

commit;