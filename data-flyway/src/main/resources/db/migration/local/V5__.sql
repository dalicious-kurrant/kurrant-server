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