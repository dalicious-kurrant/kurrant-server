ALTER TABLE client__corporation
    ADD COLUMN manager_name VARCHAR(255) NULL COMMENT '담당자 이름';
ALTER TABLE client__corporation
    ADD COLUMN manager_phone VARCHAR(255) NULL COMMENT '담당자 전화번호';