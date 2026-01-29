--
-- Name: app_user; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE app_user
(
    created_time timestamp(6) without time zone,
    id           uuid                   NOT NULL PRIMARY KEY,
    email        character varying(255),
    first_name   character varying(255),
    last_name    character varying(255),
    password     character varying(255) NOT NULL,
    phone        character varying(255),
    UNIQUE (phone),
    UNIQUE (email),

    CONSTRAINT email_notnull_or_phone_notnull CHECK (
        (email IS NOT NULL AND phone IS NULL)
            OR
        (email IS NULL AND phone IS NOT NULL)
        )
);

-- Name: idx_email; Type: INDEX; Schema: public; Owner: root
--

CREATE INDEX idx_email ON app_user USING btree (email);

ALTER TABLE app_user
    ADD COLUMN login_count INT GENERATED ALWAYS AS (
    (email IS NOT NULL)::int + (phone IS NOT NULL)::int
) STORED;

ALTER TABLE app_user
    ADD CONSTRAINT chk_one_login_only CHECK (login_count = 1);



-- Name: contacts; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE contacts
(
    id         uuid NOT NULL PRIMARY KEY,
    user_id    uuid NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    first_name character varying(255),
    last_name  character varying(255),
    title      character varying(255)
);

-- Name: idx_firstname_lastname; Type: INDEX; Schema: public; Owner: root
--

CREATE INDEX idx_firstname_lastname ON contacts USING btree (first_name, last_name);


--
-- Name: contact_email; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE contact_email
(
    contact_id  uuid                   NOT NULL REFERENCES contacts (id) ON DELETE CASCADE,
    email_type  character varying      NOT NULL,
    email_value character varying(255) NOT NULL,
    PRIMARY KEY (contact_id, email_type),
    CONSTRAINT contact_email_email_type_check CHECK (((email_type)::text = ANY ((ARRAY['WORK':: character varying, 'PERSONAL':: character varying, 'OTHER':: character varying])::text[])
) )
);


-- Name: contact_phone; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE contact_phone
(
    contact_id  uuid                   NOT NULL REFERENCES contacts (id) ON DELETE CASCADE,
    phone_type  character varying      NOT NULL,
    phone_value character varying(255) NOT NULL,
    PRIMARY KEY (contact_id, phone_type),
    CONSTRAINT contact_phone_phone_type_check CHECK (((phone_type)::text = ANY ((ARRAY['WORK':: character varying, 'HOME':: character varying, 'PERSONAL':: character varying, 'OTHER':: character varying])::text[])
) )
);

-- PostgreSQL database dump complete
--