CREATE TABLE garden_activities(
                                  id BIGSERIAL PRIMARY KEY ,
                                  garden_id BIGINT NOT NULL,
                                  plant_id BIGINT,
                                  plant_name VARCHAR(255),
                                  type VARCHAR(100) NOT NULL,
                                  message VARCHAR(500),
                                  created_at TIMESTAMP NOT NULL
);


CREATE INDEX idx_garden_activities_garden_id
    ON garden_activities(garden_id);

CREATE INDEX idx_garden_activities_created_at
    ON garden_activities(created_at);