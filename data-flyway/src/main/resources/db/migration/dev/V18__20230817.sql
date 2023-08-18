
alter table user__preference
    add column is_vegan boolean NULL comment '비건여부';

alter table user__preference
    add column vegan_level int NULL comment '비건정도';
