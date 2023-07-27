alter table board__notice add is_push_alarm BOOLEAN NOT NULL default false;

alter table board__notice change spot_id group_ids VARCHAR(255);

alter table board__notice modify status BOOLEAN;

alter table board__notice add e_type INT null;

alter table board__notice drop type;