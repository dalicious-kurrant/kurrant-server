ALTER TABLE user__user_group
    ADD CONSTRAINT uc_2e005442bd9ea959fbd6b00ae UNIQUE (group_id, user_id);