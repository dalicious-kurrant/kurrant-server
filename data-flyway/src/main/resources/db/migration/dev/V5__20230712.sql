CREATE TABLE delivery__driver
(
    id   BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)                   NULL COMMENT '배송 기사 이름',
    code VARCHAR(255)                   NULL COMMENT '배송 기사 코드',
    CONSTRAINT pk_delivery__driver PRIMARY KEY (id)
);