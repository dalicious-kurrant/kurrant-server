create table board__images
(
    back_office_notice_id           bigint unsigned                           not null,
    img_created_datetime timestamp(6) default CURRENT_TIMESTAMP(6) null comment '생성일',
    filename             varchar(1024)                             null comment '파일명, S3최대값',
    s3_key               varchar(1024)                             null comment 'S3 업로드 키',
    file_location        varchar(2048)                             null comment 'S3 접근 위치',
    constraint FK2rhrp9fjsca6gi6agyg5w3r98
        foreign key (back_office_notice_id) references board__back_office_notice (id)
)
    charset = utf8mb4;