CREATE TABLE delivery__driver
(
    id   BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)                   NULL COMMENT '배송 기사 이름',
    code VARCHAR(255)                   NULL COMMENT '배송 기사 코드',
    CONSTRAINT pk_delivery__driver PRIMARY KEY (id)
);

ALTER TABLE delivery__driver
    ADD CONSTRAINT uc_delivery__driver_code UNIQUE (code);

CREATE TABLE delivery__driver_schedule
(
    id            BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    delivery_date date                           NULL COMMENT '배송 날짜',
    e_dining_type INT                            NOT NULL COMMENT '식사 타입',
    delivery_time time                           NULL COMMENT '배송 시간',
    driver_id     BIGINT UNSIGNED                NOT NULL,
    CONSTRAINT pk_delivery__driver_schedule PRIMARY KEY (id)
);
CREATE TABLE delivery__driver_route
(
    id                 BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    e_delivery_status  INT                            NULL,
    spot_id            BIGINT UNSIGNED                NOT NULL,
    makers_id          BIGINT UNSIGNED                NOT NULL,
    driver_schedule_id BIGINT UNSIGNED                NOT NULL,
    CONSTRAINT pk_delivery__driver_route PRIMARY KEY (id)
);
ALTER TABLE delivery__delivery_instance
    ADD COLUMN driver_schedule_id BIGINT UNSIGNED NULL;

ALTER TABLE delivery__delivery_instance
    ADD CONSTRAINT FK_DELIVERY__DELIVERY_INSTANCE_ON_DRIVERSCHEDULE FOREIGN KEY (driver_schedule_id) REFERENCES delivery__driver_schedule (id);

ALTER TABLE delivery__driver_route
    ADD CONSTRAINT FK_DELIVERY__DRIVER_ROUTE_ON_DRIVERSCHEDULE FOREIGN KEY (driver_schedule_id) REFERENCES delivery__driver_schedule (id);

ALTER TABLE delivery__driver_route
    ADD CONSTRAINT FK_DELIVERY__DRIVER_ROUTE_ON_MAKERS FOREIGN KEY (makers_id) REFERENCES makers__makers (id);

ALTER TABLE delivery__driver_route
    ADD CONSTRAINT FK_DELIVERY__DRIVER_ROUTE_ON_SPOT FOREIGN KEY (spot_id) REFERENCES client__spot (id);

ALTER TABLE delivery__driver_schedule
    ADD CONSTRAINT FK_DELIVERY__DRIVER_SCHEDULE_ON_DRIVER FOREIGN KEY (driver_id) REFERENCES delivery__driver (id);