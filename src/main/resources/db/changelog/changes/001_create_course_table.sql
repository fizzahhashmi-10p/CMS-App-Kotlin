CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    completed BOOLEAN NOT NULL,
    description VARCHAR(255),
    title VARCHAR(255)
);