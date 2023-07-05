ALTER TABLE food__food_group
    ADD CONSTRAINT uc_food__food_group_name UNIQUE (name);

ALTER TABLE recommend__food_group_recommend
    ADD CONSTRAINT uc_recommend__food_group_recommend_days UNIQUE (days);

ALTER TABLE user__my_spot
    DROP FOREIGN KEY FK5kjug67gx8id4ngv75yr62i7s;

ALTER TABLE review__like
    DROP FOREIGN KEY FK6snyk383ca3kwecmo3mccvf59;

ALTER TABLE food_recommend_food_recommend_types
    DROP FOREIGN KEY FK7dhdc4rh5nrodrbrnmom9r2y;

ALTER TABLE reviews__image_origin
    DROP FOREIGN KEY FK7w7xcvf652l2u3jc5gn0kasxs;

ALTER TABLE paycheck__corporation_paycheck__paycheck_categories
    DROP FOREIGN KEY FKchbv0fi00gcjdf5kjj7xhm2yo;

ALTER TABLE application_form__apartment_meal_info
    DROP FOREIGN KEY FKcui3h2vo1av2i52q1r2qy1wv6;

ALTER TABLE user__my_spot
    DROP FOREIGN KEY FKfftb2rqnftrcw2mwgrfjh8vul;

ALTER TABLE user__push_condition
    DROP FOREIGN KEY FKh28l3awaar8pvv3euaaf8lg2e;

ALTER TABLE client__apartment
    DROP FOREIGN KEY FKian8sapxjpekh933mt8si4k22;

ALTER TABLE board__alarm
    DROP FOREIGN KEY FKim28hdg4f8lr3bfehlthd8lg9;

ALTER TABLE review__like
    DROP FOREIGN KEY FKoig1y4d84k7r8yvssnrmn46py;

ALTER TABLE makers_pickup_times
    DROP FOREIGN KEY FKq5nvo7wbcnbxplwtnqg4w105s;

ALTER TABLE user__my_spot
    DROP FOREIGN KEY FKs80segjuv3bqr3btgw3kaanu;

ALTER TABLE BATCH_JOB_EXECUTION_CONTEXT
    DROP FOREIGN KEY JOB_EXEC_CTX_FK;

ALTER TABLE BATCH_JOB_EXECUTION_PARAMS
    DROP FOREIGN KEY JOB_EXEC_PARAMS_FK;

ALTER TABLE BATCH_STEP_EXECUTION
    DROP FOREIGN KEY JOB_EXEC_STEP_FK;

ALTER TABLE BATCH_JOB_EXECUTION
    DROP FOREIGN KEY JOB_INST_EXEC_FK;

ALTER TABLE QRTZ_BLOB_TRIGGERS
    DROP FOREIGN KEY QRTZ_BLOB_TRIGGERS_ibfk_1;

ALTER TABLE QRTZ_CRON_TRIGGERS
    DROP FOREIGN KEY QRTZ_CRON_TRIGGERS_ibfk_1;

ALTER TABLE QRTZ_SIMPLE_TRIGGERS
    DROP FOREIGN KEY QRTZ_SIMPLE_TRIGGERS_ibfk_1;

ALTER TABLE QRTZ_SIMPROP_TRIGGERS
    DROP FOREIGN KEY QRTZ_SIMPROP_TRIGGERS_ibfk_1;

ALTER TABLE QRTZ_TRIGGERS
    DROP FOREIGN KEY QRTZ_TRIGGERS_ibfk_1;

ALTER TABLE BATCH_STEP_EXECUTION_CONTEXT
    DROP FOREIGN KEY STEP_EXEC_CTX_FK;

DROP TABLE application_form__apartement;

DROP TABLE application_form__apartment_meal_info;

DROP TABLE board__alarm;

DROP TABLE client__apartment;

DROP TABLE client__requested_my_spot_zones;

DROP TABLE food_recommend_food_recommend_types;

DROP TABLE makers_pickup_times;

DROP TABLE paycheck__corporation_paycheck__paycheck_categories;

DROP TABLE recommend__food_recommend_type;

DROP TABLE review__like;

DROP TABLE reviews__image_origin;

DROP TABLE user__my_spot;

DROP TABLE user__push_condition;

DROP TABLE user__review_deadline_push_alarm_log;

ALTER TABLE user__creditcard_info
    DROP COLUMN billing_key;

ALTER TABLE user__preference
    DROP COLUMN birth_place;

ALTER TABLE user__preference
    DROP COLUMN protein_bar_frequency;

ALTER TABLE user__preference
    DROP COLUMN protein_drink_frequency;

ALTER TABLE user__preference
    DROP COLUMN protein_scoop;

ALTER TABLE client__my_spot_zone
    DROP COLUMN city;

ALTER TABLE client__my_spot_zone
    DROP COLUMN coutries;

ALTER TABLE client__my_spot_zone
    DROP COLUMN villages;

ALTER TABLE client__my_spot_zone
    DROP COLUMN zipcodes;

ALTER TABLE review__review
    DROP COLUMN created_datetime;

ALTER TABLE review__review
    DROP COLUMN file_location;

ALTER TABLE review__review
    DROP COLUMN filename;

ALTER TABLE review__review
    DROP COLUMN image_origin;

ALTER TABLE review__review
    DROP COLUMN img_created_datetime;

ALTER TABLE review__review
    DROP COLUMN `like`;

ALTER TABLE review__review
    DROP COLUMN review_like;

ALTER TABLE review__review
    DROP COLUMN s3_key;

ALTER TABLE review__review
    DROP COLUMN updated_datetime;

ALTER TABLE client__meal_info
    DROP COLUMN daily_support_price;

ALTER TABLE order__order_item_dailyfood_group
    DROP COLUMN delivery_time;

ALTER TABLE user__user_spot
    DROP COLUMN ho;

ALTER TABLE cms__banner
    DROP COLUMN img_created_datetime;

ALTER TABLE cms__banner
    DROP COLUMN move_to;

ALTER TABLE food__images
    DROP COLUMN img_created_datetime;

ALTER TABLE reviews__images
    DROP COLUMN img_created_datetime;

ALTER TABLE user__user
    DROP COLUMN img_created_datetime;

ALTER TABLE client__group
    DROP COLUMN manager_id;

ALTER TABLE food__daily_food_group
    DROP COLUMN pickup_time;

ALTER TABLE makers__preset_group_daily_food
    DROP COLUMN pickup_time;

ALTER TABLE board__notice
    DROP COLUMN type;

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

ALTER TABLE application_form__requested_my_spot
    MODIFY address_location BLOB;

ALTER TABLE application_form__requested_share_spot
    MODIFY address_location BLOB;

ALTER TABLE client__group
    MODIFY address_location BLOB;

ALTER TABLE client__spot
    MODIFY address_location BLOB;

ALTER TABLE makers__makers
    MODIFY address_location BLOB;

ALTER TABLE user__membership
    ALTER auto_payment SET DEFAULT 0;

ALTER TABLE client__meal_info
    MODIFY delivery_time VARCHAR(255) NULL;

ALTER TABLE order__order_item_membership
    CHANGE price discount_price DECIMAL(15, 2);

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

ALTER TABLE user__daily_report
    MODIFY image_location VARCAHR(255);

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

ALTER TABLE order__order_item_membership
    CHANGE discount_price `null` NULL;

ALTER TABLE order__order_item_dailyfood
    MODIFY price DECIMAL(15, 2);

ALTER TABLE order__order_item_dailyfood
    ALTER price SET DEFAULT 0;

ALTER TABLE order__order_item_membership
    ADD price DECIMAL(15, 2) DEFAULT 0 NULL COMMENT '상품 가격';

ALTER TABLE paycheck__corporation_paycheck_paycheck_adds
    MODIFY price DECIMAL(15, 2);

ALTER TABLE paycheck__corporation_paycheck_paycheck_adds
    ALTER price SET DEFAULT 0;

ALTER TABLE paycheck__makers_paycheck__paycheck_add
    CHANGE issue_item `null` NULL;

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

CREATE INDEX i_founders_number ON user__founders (founders_number);