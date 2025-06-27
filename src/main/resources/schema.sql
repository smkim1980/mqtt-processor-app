DROP TABLE IF EXISTS LOCATION_HISTORY;
DROP TABLE IF EXISTS VIOLATION_HISTORY;
DROP TABLE IF EXISTS INCIDENT_HISTORY;
DROP TABLE IF EXISTS STOP_EVENT_HISTORY;
DROP TABLE IF EXISTS TRIP_HISTORY;
DROP TABLE IF EXISTS VEHICLE_MASTER;


-- 차량 마스터 정보
CREATE TABLE VEHICLE_MASTER (
                                VEHICLE_ID BIGINT PRIMARY KEY,
                                PLATE_NUMBER VARCHAR(20),
                                CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 운행 이력 정보
CREATE TABLE TRIP_HISTORY (
                              ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                              VEHICLE_ID BIGINT,
                              ROUTE_ID BIGINT,
                              TRIP_ID BIGINT,
                              DRIVER_ID BIGINT,
                              DRIVING_MODE INT,
                              START_AT TIMESTAMP,
                              END_AT TIMESTAMP,
                              FOREIGN KEY (VEHICLE_ID) REFERENCES VEHICLE_MASTER(VEHICLE_ID)
);
CREATE INDEX IDX_TRIP_HISTORY_VEHICLE_ID ON TRIP_HISTORY(VEHICLE_ID);

-- 위치 정보 이력
CREATE TABLE LOCATION_HISTORY (
                                  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  TRIP_ID BIGINT,
                                  OCCUR_AT TIMESTAMP,
                                  LATITUDE DOUBLE,
                                  LONGITUDE DOUBLE,
                                  HEADING FLOAT,
                                  SPEED FLOAT,
                                  FOREIGN KEY (TRIP_ID) REFERENCES TRIP_HISTORY(ID)
);
CREATE INDEX IDX_LOCATION_HISTORY_TRIP_ID_OCCUR_AT ON LOCATION_HISTORY(TRIP_ID, OCCUR_AT);

-- 운행 위반 이력
CREATE TABLE VIOLATION_HISTORY (
                                   ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   TRIP_ID BIGINT,
                                   OCCUR_AT TIMESTAMP,
                                   VIOLATION_CODE INT,
                                   VIOLATION_SPEED FLOAT,
                                   LATITUDE DOUBLE,
                                   LONGITUDE DOUBLE,
                                   FOREIGN KEY (TRIP_ID) REFERENCES TRIP_HISTORY(ID)
);
CREATE INDEX IDX_VIOLATION_HISTORY_TRIP_ID ON VIOLATION_HISTORY(TRIP_ID);

-- 돌발 상황 이력
CREATE TABLE INCIDENT_HISTORY (
                                  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  TRIP_ID BIGINT,
                                  OCCUR_AT TIMESTAMP,
                                  INCIDENT_CODE INT,
                                  LATITUDE DOUBLE,
                                  LONGITUDE DOUBLE,
                                  FOREIGN KEY (TRIP_ID) REFERENCES TRIP_HISTORY(ID)
);

-- 정류장 이벤트 이력
CREATE TABLE STOP_EVENT_HISTORY (
                                    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    TRIP_ID BIGINT,
                                    STOP_ID BIGINT,
                                    EVENT_TYPE VARCHAR(10), -- 'ARRIVAL' or 'DEPARTURE'
                                    EVENT_AT TIMESTAMP,
                                    FOREIGN KEY (TRIP_ID) REFERENCES TRIP_HISTORY(ID)
);