ALTER TABLE food__food_discount_policy
    MODIFY discount_rate INT NOT NULL;

ALTER TABLE client__meal_info
    MODIFY dtype VARCHAR(31) NULL;

ALTER TABLE client__spot
    MODIFY dtype VARCHAR(31) NULL;

ALTER TABLE review__comment
    MODIFY dtype VARCHAR(31) NULL;

ALTER TABLE paycheck__corporation_paycheck
    MODIFY excel_file_location VARCHAR(2048);

ALTER TABLE paycheck__corporation_paycheck
    MODIFY excel_filename VARCHAR(1024);

ALTER TABLE paycheck__corporation_paycheck
    MODIFY excel_s3_key VARCHAR(1024);

ALTER TABLE makers__makers
    MODIFY fee VARCHAR(255);

ALTER TABLE cms__advertisement
    MODIFY id BIGINT UNSIGNED COMMENT '배너 PK';

ALTER TABLE cms__banner
    MODIFY id BIGINT UNSIGNED COMMENT '배너 PK';

ALTER TABLE client__group
    ALTER is_active SET DEFAULT 1;

ALTER TABLE makers__makers
    ALTER is_active SET DEFAULT 1;

ALTER TABLE client__spot
    ALTER is_alarm SET DEFAULT 0;

ALTER TABLE client__spot
    ALTER is_delete SET DEFAULT 0;

ALTER TABLE client__corporation
    ALTER is_garbage SET DEFAULT 0;

ALTER TABLE client__corporation
    ALTER is_hot_storage SET DEFAULT 0;

ALTER TABLE user__user
    ALTER is_membership SET DEFAULT 0;

ALTER TABLE client__corporation
    ALTER is_membership_support SET DEFAULT 0;

ALTER TABLE client__corporation
    ALTER is_salad_required SET DEFAULT 0;

ALTER TABLE client__corporation
    ALTER is_setting SET DEFAULT 0;

ALTER TABLE client__meal_info
    MODIFY last_order_time VARCHAR(255);

ALTER TABLE client__meal_info
    MODIFY last_order_time VARCHAR(255) NULL;

ALTER TABLE user__user
    ALTER marketing_agreed SET DEFAULT 0;

ALTER TABLE user__user
    ALTER marketing_alarm SET DEFAULT 0;

ALTER TABLE client__meal_info
    MODIFY membership_benefit_time VARCHAR(255);

ALTER TABLE paycheck__makers_paycheck__paycheck_add
    MODIFY memo VARCHAR(255);

ALTER TABLE paycheck__corporation_paycheck__paycheck_memo
    MODIFY memo_created_date_time timestamp(6) NULL;

ALTER TABLE paycheck__makers_paycheck__paycheck_memo
    MODIFY memo_created_date_time timestamp(6) NULL;

ALTER TABLE test_entity
    MODIFY name VARCHAR(255) NULL;

ALTER TABLE user__user
    ALTER order_alarm SET DEFAULT 0;

ALTER TABLE paycheck__corporation_paycheck
    MODIFY pdf_file_location VARCHAR(2048);

ALTER TABLE paycheck__makers_paycheck
    MODIFY pdf_file_location VARCHAR(2048);

ALTER TABLE paycheck__corporation_paycheck
    MODIFY pdf_filename VARCHAR(1024);

ALTER TABLE paycheck__corporation_paycheck
    MODIFY pdf_s3_key VARCHAR(1024);

ALTER TABLE order__order_item_dailyfood
    MODIFY price DECIMAL(15, 2);

ALTER TABLE order__order_item_dailyfood
    ALTER price SET DEFAULT 0;

ALTER TABLE paycheck__corporation_paycheck_paycheck_adds
    MODIFY price DECIMAL(15, 2);

ALTER TABLE paycheck__corporation_paycheck_paycheck_adds
    ALTER price SET DEFAULT 0;

ALTER TABLE paycheck__makers_paycheck__paycheck_add
    ALTER price SET DEFAULT 0;

ALTER TABLE user__point_policy
    MODIFY reward_point DECIMAL(15, 2);

ALTER TABLE user__point_policy
    ALTER reward_point SET DEFAULT 0;

ALTER TABLE cms__banner
    MODIFY section VARCHAR(16) COMMENT '배너 구역';

ALTER TABLE user__creditcard_info
    MODIFY status INT NOT NULL;

ALTER TABLE paycheck__makers_paycheck__paycheck_daily_foods
    MODIFY supply_price DECIMAL(15, 2);

ALTER TABLE paycheck__makers_paycheck__paycheck_daily_foods
    ALTER supply_price SET DEFAULT 0;

ALTER TABLE cms__banner
    MODIFY type VARCHAR(16) COMMENT '배너유형';

ALTER TABLE board__notice
    MODIFY updated_date_time timestamp(6) NULL;

ALTER TABLE makers__makers
    MODIFY updated_datetime TIMESTAMP(6) NOT NULL;

ALTER TABLE user__daily_report
    MODIFY updated_datetime timestamp(6) NULL;

ALTER TABLE order__membership_support_price
    MODIFY using_support_price DECIMAL(15, 2);

ALTER TABLE user__support_price_history
    MODIFY using_support_price DECIMAL(15, 2);

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