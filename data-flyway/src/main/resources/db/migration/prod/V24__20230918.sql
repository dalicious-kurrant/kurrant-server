ALTER TABLE food__daily_food
    ADD COLUMN is_eat_in BIT(1) DEFAULT 0 NULL;

ALTER TABLE client__spot
    ADD COLUMN makers_id DECIMAL NULL COMMENT '메이커스 FK';

