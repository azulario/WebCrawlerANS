
CREATE TABLE IF NOT EXISTS emails (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL
);


INSERT INTO emails (email) VALUES
    ('exemplo1@email.com'),
    ('exemplo2@email.com')
ON CONFLICT (email) DO NOTHING;


SELECT * FROM emails;

