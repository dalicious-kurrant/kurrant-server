ALTER TABLE paycheck__makers_paycheck
    ADD COLUMN fee DECIMAL(15, 2) DEFAULT 0 NULL COMMENT '물류 수수료';