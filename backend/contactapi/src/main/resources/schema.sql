-- APP_USER
CREATE TABLE IF NOT EXISTS app_user
(
    id            UUID PRIMARY KEY,
    created_time  TIMESTAMP(6),
    email         VARCHAR(255) NOT NULL UNIQUE,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) UNIQUE
);

CREATE INDEX idx_email ON app_user (email);

-- CONTACTS
CREATE TABLE IF NOT EXISTS contacts
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    title      VARCHAR(255)
);

CREATE INDEX idx_firstname_lastname
    ON contacts (first_name, last_name);

-- APP_USER_CONTACTS
CREATE TABLE IF NOT EXISTS app_user_contacts
(
    user_id     UUID NOT NULL REFERENCES app_user (id) NOCHECK,
    contacts_id UUID NOT NULL REFERENCES contacts (id) NOCHECK,
    UNIQUE (contacts_id)
);

-- CONTACT_EMAIL
CREATE TABLE IF NOT EXISTS contact_email
(
    contact_id  UUID         NOT NULL REFERENCES contacts (id),
    email_value VARCHAR(255) NOT NULL,
    email_type  VARCHAR      NOT NULL,
    PRIMARY KEY (contact_id, email_type)
);

-- CONTACT_PHONE
CREATE TABLE IF NOT EXISTS contact_phone
(
    contact_id  UUID         NOT NULL REFERENCES contacts (id),
    phone_value VARCHAR(255) NOT NULL,
    phone_type  VARCHAR      NOT NULL,
    PRIMARY KEY (contact_id, phone_type)
);
