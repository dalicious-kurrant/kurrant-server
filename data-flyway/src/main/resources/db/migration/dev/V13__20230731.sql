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
