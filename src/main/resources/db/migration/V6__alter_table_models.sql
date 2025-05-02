ALTER TABLE models
ADD COLUMN brand_id BIGINT;

ALTER TABLE models
ADD CONSTRAINT fk_model_brand FOREIGN KEY (brand_id) REFERENCES brands (id);