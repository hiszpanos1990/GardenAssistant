CREATE TABLE plant_profiles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    watering_frequency_days INTEGER NOT NULL,
    min_temperature DOUBLE PRECISION,
    max_temperature DOUBLE PRECISION,
    likes_rain BOOLEAN NOT NULL
);

ALTER TABLE plants
    ADD COLUMN plant_profile_id BIGINT;

ALTER TABLE plants
    ADD CONSTRAINT fk_plants_plant_profile
        FOREIGN KEY (plant_profile_id)
            REFERENCES plant_profiles(id);


INSERT INTO plant_profiles
(name, watering_frequency_days, min_temperature, max_temperature, likes_rain)
VALUES
    ('Tomato', 2, 10, 35, true),
    ('Cactus', 14, 5, 45, false),
    ('Rose', 3, 5, 30, true),
    ('Orchid', 7, 15, 28, false);