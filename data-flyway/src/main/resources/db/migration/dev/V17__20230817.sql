alter table board__notice
    add column active_date date NULL COMMENT '상태변경 일';

alter table user__preference
    add column is_vegan int NULL comment '비건여부';

alter table user__preference
    add column vegan_level int NULL comment '비건정도';