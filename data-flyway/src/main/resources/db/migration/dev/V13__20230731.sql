create table board__back_office_notice
(
    dtype             varchar(31)                               null,
    id   BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    created_date_time timestamp(6) default CURRENT_TIMESTAMP(6) not null comment '생성일',
    updated_date_time timestamp    default CURRENT_TIMESTAMP    null,
    content           longtext                                  null comment '공지 내용',
    title             varchar(255)                              null comment '공지 제목',
    group_ids         varchar(255)                              null comment '그룹ID List',
    makers_id         BIGINT                              null comment '메이커스 ID',
    status            tinyint(1)                                null comment '상태 0:비활성 / 1:활성',
    e_type            int                                       null,
    is_alarm_talk     tinyint(1)   default 0                    not null comment '상태 0:비활성 / 1:활성',
    CONSTRAINT pk_board__back_office_notice PRIMARY KEY (id)
);

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
