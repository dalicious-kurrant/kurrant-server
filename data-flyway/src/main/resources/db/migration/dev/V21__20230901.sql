CREATE TABLE makers__intro_images
(
    makers_id     BIGINT UNSIGNED NOT NULL COMMENT 'ID',
    s3_key        VARCHAR(1024)   NULL COMMENT 'S3 업로드 키',
    file_location VARCHAR(2048)   NULL COMMENT 'S3 접근 위치',
    filename      VARCHAR(1024)   NULL COMMENT '파일명, S3최대값'
);

ALTER TABLE makers__intro_images
    ADD CONSTRAINT fk_makers__intro_images_on_makers FOREIGN KEY (makers_id) REFERENCES makers__makers (id);

CREATE TABLE food__intro_images
(
    food_id       BIGINT UNSIGNED NOT NULL COMMENT 'ID',
    s3_key        VARCHAR(1024)   NULL COMMENT 'S3 업로드 키',
    file_location VARCHAR(2048)   NULL COMMENT 'S3 접근 위치',
    filename      VARCHAR(1024)   NULL COMMENT '파일명, S3최대값'
);

ALTER TABLE food__intro_images
    ADD CONSTRAINT fk_food__intro_images_on_food FOREIGN KEY (food_id) REFERENCES food__food (id);