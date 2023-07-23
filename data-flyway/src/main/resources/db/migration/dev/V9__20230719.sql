ALTER TABLE delivery__delivery_instance
    ADD COLUMN driver_id BIGINT UNSIGNED NULL;

ALTER TABLE delivery__delivery_instance
    ADD CONSTRAINT FK_DELIVERY__DELIVERY_INSTANCE_ON_DRIVER FOREIGN KEY (driver_id) REFERENCES delivery__driver (id);