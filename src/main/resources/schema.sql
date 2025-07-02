DROP TABLE IF EXISTS ENCRYPTED_LOCATION;

CREATE TABLE ENCRYPTED_LOCATION (
                                    encrypted_latitude VARCHAR(255),
                                    encrypted_longitude VARCHAR(255),
                                    heading REAL,
                                    altitude REAL,
                                    speed REAL,
                                    occur_dt VARCHAR(255),
                                    route_dir VARCHAR(255),
                                    last_stop_id VARCHAR(255),
                                    last_stop_seq BIGINT,
                                    last_stop_dist BIGINT,
                                    origin_accum_dist BIGINT
);