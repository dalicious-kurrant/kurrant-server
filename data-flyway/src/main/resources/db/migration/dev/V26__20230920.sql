create table application_form__recommend_makers
(
    id                bigint unsigned auto_increment
        primary key,
    created_date_time timestamp(6) default CURRENT_TIMESTAMP(6) not null comment '생성일',
    updated_date_time timestamp    default CURRENT_TIMESTAMP    null comment '수정일',
    address_depth_1                              varchar(255)    null comment '기본주소',
    address_depth_2                              varchar(255)    null comment '상세주소',
    address_depth_3                              varchar(255)    null comment '지번주소',
    address_location                             geometry        null comment '위치',
    zip_code                                     varchar(5)      null comment '우편번호, 다섯자리',
    name                                         varchar(32)     not null comment '메이커스 이름',
    user_Id                                      varchar(255)    not null comment '유저 Id',
    phone                                        varchar(255)    not null comment '매장 번호',
    e_status                                     tinyint(1)      null comment '진행 상황',
    group_id                                     bigint unsigned null comment '고객사 Id'
)