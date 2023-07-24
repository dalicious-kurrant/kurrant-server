ALTER TABLE delivery__driver
    ADD CONSTRAINT uc_delivery__driver_code UNIQUE (code);

ALTER TABLE user__user
    ADD COLUMN nickname VARCHAR(32) NULL COMMENT '사용자 닉네임';