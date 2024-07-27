CREATE TABLE IF NOT EXISTS contacts (
   id BIGSERIAL PRIMARY KEY,
   first_name VARCHAR(255) NOT NULL,
   last_name VARCHAR(255) NOT NULL,
   phone VARCHAR(255) NOT NULL,
   country_code VARCHAR(2) NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_contacts_first_name ON contacts (first_name);
CREATE INDEX IF NOT EXISTS idx_contacts_last_name ON contacts (last_name);
CREATE INDEX IF NOT EXISTS idx_contacts_phone ON contacts (phone);