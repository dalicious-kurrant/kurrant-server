create table `application_form__requested_my_spot`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '신청한 마이 스팟 PK',
    `address_depth_1` VARCHAR(255) COMMENT '기본주소',
    `address_depth_2` VARCHAR(255) COMMENT '상세주소',
    `address_depth_3` VARCHAR(255) COMMENT '지번주소',
    `address_location` GEOMETRY comment '위치',
    `zip_code` VARCHAR(5) COMMENT '우편번호, 다섯자리',
    `memo`             varchar(255) comment '메모',
    `name`             varchar(32) not null comment '스팟 이름',
    `user_id`          BIGINT UNSIGNED comment '유저 Id',
    `application_form__requested_my_spot_zones_id` BIGINT UNSIGNED not null comment '신청 마이스팟 존 ID',
    primary key (`id`)
) engine = InnoDB;
create table `application_form__requested_my_spot_zones`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '신청한 마이 스팟존 PK',
    `created_date_time` TIMESTAMP(6) DEFAULT NOW(6) not null comment '생성일',
    `memo`                varchar(255) comment '메모',
    `push_alarm_user_ids` varchar(255) comment '푸시 알림 신청 유저 ID 리스트',
    `updated_date_time` TIMESTAMP(6) DEFAULT NOW(6) not null comment '수정일',
    `waiting_user_count`  integer comment '신청 유저 수',
    `region_fk` BIGINT UNSIGNED,
    primary key (`id`)
) engine = InnoDB;
create table `application_form__requested_share_spot`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '신청한 공유 스팟 PK',
    `address_depth_1` VARCHAR(255) COMMENT '기본주소',
    `address_depth_2` VARCHAR(255) COMMENT '상세주소',
    `address_depth_3` VARCHAR(255) COMMENT '지번주소',
    `address_location`        GEOMETRY comment '위치',
    `zip_code` VARCHAR(5) COMMENT '우편번호, 다섯자리',
    `created_date_time` TIMESTAMP(6) DEFAULT NOW(6) not null comment '생성일',
    `delivery_time`           time comment '배송 시간',
    `entrance_option`         bit comment '외부인 출입 가능 여부',
    `group_id` BIGINT UNSIGNED comment '그룹 id',
    `memo`                    varchar(255) comment '메모',
    `share_spot_request_type` integer comment '공유 스팟 신청 타입 1.개설 2.추가 3.시간추가',
    `updated_date_time` TIMESTAMP(6) DEFAULT NOW(6) not null comment '수정일',
    `user_id` BIGINT UNSIGNED comment '신청 유저 id',
    primary key (`id`)
) engine = InnoDB;
create table `client__department`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '부서 PK',
    `name` VARCHAR(32) comment '부서 이름',
    `group_id` BIGINT UNSIGNED not null comment '그룹',
    primary key (`id`)
) engine = InnoDB;
create table `client__region`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '지역 PK',
    `city`                    varchar(255) comment '시/도',
    `county`                  varchar(255) comment '시/군/구',
    `village`                 varchar(255) comment '동/읍/리',
    `zipcodes`                varchar(255) comment '우편 번호',
    `client__my_spot_zone_fk` BIGINT UNSIGNED comment '마이스팟 존',
    primary key (`id`)
) engine = InnoDB;
create table `delivery__daily_food_delivery`
(
    `id` BIGINT UNSIGNED not null auto_increment,
    `delivery_instance_id` BIGINT UNSIGNED not null,
    `order_item_daily_food_id` BIGINT UNSIGNED not null,
    primary key (`id`)
) engine = InnoDB;
create table `delivery__delivery_instance`
(
    `id` BIGINT UNSIGNED not null auto_increment,
    `delivery_time` time,
    `dining_type`   integer,
    `order_number`  integer,
    `pick_up_time`  time,
    `service_date`  date,
    `makers_id` BIGINT UNSIGNED not null,
    `spot_id` BIGINT UNSIGNED not null,
    primary key (`id`)
) engine = InnoDB;
create table `food__delivery_schedule`
(
    `daily_food_group_id` BIGINT UNSIGNED not null,
    `delivery_time` time comment '배송 시간',
    `pickup_time`   time comment '픽업 시간'
) engine = InnoDB;
create table `makers__preset_delivery_schedule`
(
    `preset_group_daily_food_id` BIGINT UNSIGNED not null,
    `delivery_time` time comment '배송 시간',
    `pickup_time`   time comment '픽업 시간'
) engine = InnoDB;
alter table `user__daily_report`
    add column `image_location` VARCHAR(255) comment '이미지';

alter table `user__daily_report`
    add column `title` VARCHAR(64) comment '제목';
create table `user__user_department`
(
    `id` BIGINT UNSIGNED not null auto_increment comment '유저 부서 정보 PK',
    `department_id` BIGINT UNSIGNED not null comment '부서 정보',
    `user_id` BIGINT UNSIGNED not null comment '유저 정보 FK',
    primary key (`id`)
) engine = InnoDB;
alter table `application_form__spot`
    add column `address_depth_3` VARCHAR(255) COMMENT '지번주소';
alter table `application_form__apartement`
    add column `address_depth_3` VARCHAR(255) COMMENT '지번주소';
alter table `application_form__corporation`
    add column `address_depth_3` VARCHAR(255) COMMENT '지번주소';
alter table `application_form__requested_my_spot`
    add constraint `FKq12770n80v4t06rig0he00tdu` foreign key (`application_form__requested_my_spot_zones_id`) references `application_form__requested_my_spot_zones` (`id`);
alter table `application_form__requested_my_spot_zones`
    add constraint `FKir8j66m63ehkev0necwo9aah7` foreign key (`region_fk`) references `client__region` (`id`);
alter table `client__department`
    add constraint `FK5rcjuehwrlijjb1yos3hpwy4u` foreign key (`group_id`) references `client__group` (`id`);
alter table `delivery__daily_food_delivery`
    add constraint `FKnlt514vkb21j8mp8atdg43e0q` foreign key (`delivery_instance_id`) references `delivery__delivery_instance` (`id`);
alter table `delivery__daily_food_delivery`
    add constraint `FKh7jrw2rm9fgvbufd2fapkk6i5` foreign key (`order_item_daily_food_id`) references `order__order_item_dailyfood` (`id`);
alter table `delivery__delivery_instance`
    add constraint `FKhmkqrppuk21ry82orm4psylne` foreign key (`makers_id`) references `makers__makers` (`id`);
alter table `delivery__delivery_instance`
    add constraint `FKtrdd6ijkdcq1p7m86ft50sr2w` foreign key (`spot_id`) references `client__spot` (`id`);
alter table `food__delivery_schedule`
    add constraint `FK5c7oty6hkmrd4p39km7b9xq6s` foreign key (`daily_food_group_id`) references `food__daily_food_group` (`id`);
alter table `makers__preset_delivery_schedule`
    add constraint `FKs3nucbi5m3ua6k2cy3klmkh5` foreign key (`preset_group_daily_food_id`) references `makers__preset_group_daily_food` (`id`);
alter table `user__user_department`
    add constraint `FK8udpf6p77ah2ynd1b19mcr4o` foreign key (`department_id`) references `client__department` (`id`);
alter table `user__user_department`
    add constraint `FK3unec7jlau4f7dh3e7pgy9wh` foreign key (`user_id`) references `user__user` (`id`);
alter table client__meal_info
    modify delivery_time varchar(255) not null comment '배송 시간';
alter table client__spot
    add column is_alarm BIT(1) DEFAULT 0 comment '마이 스팟 푸시알림 여부 - 1: 수신';
alter table application_form__requested_my_spot
    add column user_phone varchar(255) comment '유저 핸드폰 번호';