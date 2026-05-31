CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE gardens (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255),
                         description VARCHAR(255),
                         location VARCHAR(255),
                         max_plants INTEGER NOT NULL,
                         user_id BIGINT,
                         CONSTRAINT fk_gardens_user
                             FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE watering_schedules (
                                    id BIGSERIAL PRIMARY KEY,
                                    watering_interval INTEGER NOT NULL,
                                    next_watering_date TIMESTAMP,
                                    last_watering_date TIMESTAMP,
                                    is_watering BOOLEAN NOT NULL
);

CREATE TABLE plants (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255),
                        type VARCHAR(255),
                        planted_date TIMESTAMP,
                        status VARCHAR(255),
                        garden_id BIGINT,
                        watering_schedule_id BIGINT,
                        CONSTRAINT fk_plants_garden
                            FOREIGN KEY (garden_id) REFERENCES gardens(id),
                        CONSTRAINT fk_plants_watering_schedule
                            FOREIGN KEY (watering_schedule_id) REFERENCES watering_schedules(id)
);