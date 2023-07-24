ALTER TABLE paycheck__corporation_paycheck__paycheck_memo
    MODIFY COLUMN memo_created_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE paycheck__makers_paycheck__paycheck_memo
    MODIFY COLUMN memo_created_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE client__corporation
    MODIFY minimum_spend DECIMAL(15, 2);

ALTER TABLE user__user
    ALTER order_alarm SET DEFAULT 0;

ALTER TABLE order__order_item_membership
    MODIFY period_discounted_rate DECIMAL(15, 2);

ALTER TABLE user__point_history
    MODIFY point DECIMAL(15, 2);

ALTER TABLE order__order_item_dailyfood
    MODIFY price DECIMAL(15, 2);

ALTER TABLE order__order_item_membership
    MODIFY price DECIMAL(15, 2);

ALTER TABLE paycheck__corporation_paycheck_paycheck_adds
    MODIFY price DECIMAL(15, 2);

ALTER TABLE user__point_policy
    MODIFY reward_point DECIMAL(15, 2);

ALTER TABLE cms__banner
    MODIFY section VARCHAR(16) COMMENT '배너 구역';

ALTER TABLE paycheck__makers_paycheck__paycheck_daily_foods
    MODIFY supply_price DECIMAL(15, 2);

ALTER TABLE cms__banner
    MODIFY type VARCHAR(16) COMMENT '배너유형';

ALTER TABLE board__notice
MODIFY COLUMN updated_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE makers__makers
    MODIFY COLUMN updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE user__daily_report
    MODIFY COLUMN updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE order__membership_support_price
    MODIFY using_support_price DECIMAL(15, 2);

ALTER TABLE order__membership_support_price
    ALTER using_support_price SET DEFAULT 0;

ALTER TABLE user__support_price_history
    MODIFY using_support_price DECIMAL(15, 2);

ALTER TABLE user__support_price_history
    ALTER using_support_price SET DEFAULT 0;

ALTER TABLE application_form__corporation
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE application_form__requested_my_spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE application_form__requested_share_spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE application_form__spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE client__group
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE client__spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE makers__makers
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';

ALTER TABLE order__order
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리';