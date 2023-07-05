ALTER TABLE cms__banner
    MODIFY section VARCHAR(16) COMMENT '배너 구역' NOT NULL;

ALTER TABLE cms__banner
    MODIFY type VARCHAR(16) COMMENT '배너유형' NOT NULL;

ALTER TABLE application_form__requested_my_spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE application_form__corporation
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE application_form__requested_share_spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE application_form__spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE client__group
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE client__spot
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE makers__makers
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE order__order
    MODIFY zip_code VARCHAR(5) COMMENT '우편번호, 다섯자리' NULL;

ALTER TABLE application_form__corporation
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE application_form__requested_my_spot
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE application_form__requested_share_spot
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE application_form__spot
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE client__group
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE client__spot
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE makers__makers
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE order__order
    MODIFY address_depth_1 VARCHAR(255) COMMENT '기본주소';

ALTER TABLE application_form__corporation
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE application_form__requested_my_spot
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE application_form__requested_share_spot
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE application_form__spot
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE client__group
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE client__spot
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE makers__makers
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE order__order
    MODIFY address_depth_2 VARCHAR(255) COMMENT '상세주소';

ALTER TABLE application_form__corporation
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE application_form__requested_my_spot
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE application_form__requested_share_spot
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE application_form__spot
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE client__group
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE client__spot
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE makers__makers
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE order__order
    MODIFY address_depth_3 VARCHAR(255) COMMENT '지번주소';

ALTER TABLE user__membership
    ALTER auto_payment SET DEFAULT 0;

ALTER TABLE client__meal_info
    MODIFY delivery_time VARCHAR(255) NULL;

ALTER TABLE order__order_item_membership
    MODIFY discount_price DECIMAL(15, 2);

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

ALTER TABLE user__daily_report
    MODIFY image_location varchar(255);

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

ALTER TABLE user__point_history
    MODIFY left_point DECIMAL;

ALTER TABLE user__user
    ALTER marketing_agreed SET DEFAULT 0;

ALTER TABLE user__user
    ALTER marketing_alarm SET DEFAULT 0;

ALTER TABLE client__corporation
    MODIFY maximum_spend DECIMAL;

ALTER TABLE client__meal_info
    MODIFY membership_benefit_time VARCHAR(255);

ALTER TABLE paycheck__makers_paycheck__paycheck_add
    MODIFY memo VARCHAR(255);

ALTER TABLE paycheck__corporation_paycheck__paycheck_memo
    MODIFY memo_created_date_time timestamp(6) NULL;

ALTER TABLE paycheck__makers_paycheck__paycheck_memo
    MODIFY memo_created_date_time timestamp(6) NULL;

ALTER TABLE client__corporation
    MODIFY minimum_spend DECIMAL;

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

ALTER TABLE order__order_item_membership
    MODIFY period_discounted_rate DECIMAL(15, 2);

ALTER TABLE user__point_history
    MODIFY point DECIMAL;
ALTER TABLE order__order_item_membership
    MODIFY price DECIMAL(15, 2);

ALTER TABLE user__creditcard_info
    MODIFY status INT NOT NULL;

ALTER TABLE board__notice
    MODIFY updated_date_time timestamp(6) NULL;

ALTER TABLE makers__makers
    MODIFY updated_datetime TIMESTAMP(6) NOT NULL;

ALTER TABLE user__daily_report
    MODIFY updated_datetime timestamp(6) NULL;