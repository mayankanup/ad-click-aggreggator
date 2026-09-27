CREATE TABLE IF NOT EXISTS click_aggregates (
  granularity VARCHAR(16) NOT NULL,
  dimension_type VARCHAR(8) NOT NULL,
  dimension_id VARCHAR(128) NOT NULL,
  org_id VARCHAR(128),
  window_start TIMESTAMPTZ NOT NULL,
  window_end TIMESTAMPTZ NOT NULL,
  click_count BIGINT NOT NULL,
  PRIMARY KEY (granularity, dimension_type, dimension_id, window_start)
);
CREATE INDEX IF NOT EXISTS idx_agg_window ON click_aggregates (granularity, window_start);
