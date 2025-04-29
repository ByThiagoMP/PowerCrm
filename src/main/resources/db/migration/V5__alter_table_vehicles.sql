ALTER TABLE vehicles
ADD COLUMN brand_id BIGINT,
ADD COLUMN model_id BIGINT,
ADD COLUMN fipe_price DECIMAL(10, 2);

ALTER TABLE vehicles
ADD CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brands (id),
ADD CONSTRAINT fk_model FOREIGN KEY (model_id) REFERENCES models (id);