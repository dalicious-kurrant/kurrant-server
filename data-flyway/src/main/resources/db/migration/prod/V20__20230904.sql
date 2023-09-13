create table application_form__requested_corporation
(
    id   BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    created_date_time timestamp(6) default CURRENT_TIMESTAMP(6) not null comment '생성일',
    updated_date_time timestamp    default CURRENT_TIMESTAMP    null comment '수정일',
    user_name           varchar(255)                                  null comment '신청한 유저 이름',
    address             varchar(255)                              null comment '스팟 주소',
    user_phone         varchar(255)                              null comment '유저 핸드폰 번호',
    memo         varchar(255)                              null comment '메모',
    e_status tinyint(1) null comment '진행 상황',
    CONSTRAINT pk_application_form__requested_corporation PRIMARY KEY (id)
);

create table application_form__requested_makers
(
    id   BIGINT UNSIGNED AUTO_INCREMENT NOT NULL,
    created_date_time timestamp(6) default CURRENT_TIMESTAMP(6) not null comment '생성일',
    updated_date_time timestamp    default CURRENT_TIMESTAMP    null comment '수정일',
    user_name           varchar(255)                                  null comment '신청한 유저 이름',
    makers_name           varchar(255)                                  null comment '신청한 메이커스 이름',
    address             varchar(255)                              null comment '메이커스 주소',
    phone         varchar(255)                              null comment '번호',
    memo         varchar(255)                              null comment '메모',
    main_product         varchar(255)                              null comment '메인 상품',
    e_status tinyint(1) null comment '진행 상황',
    CONSTRAINT pk_application_form__requested_makers PRIMARY KEY (id)
);
