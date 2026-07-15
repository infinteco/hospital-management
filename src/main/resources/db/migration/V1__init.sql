-- Core schema. Column types match Hibernate 6 / MySQL dialect expectations so
-- `spring.jpa.hibernate.ddl-auto=validate` passes (datetime(6), bit, bigint).

CREATE TABLE patients (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    full_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) UNIQUE,
    phone        VARCHAR(255),
    date_of_birth DATE,
    gender       VARCHAR(20),
    created_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE doctors (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    full_name      VARCHAR(255) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    email          VARCHAR(255) UNIQUE,
    phone          VARCHAR(255),
    created_at     DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    enabled    BIT          NOT NULL,
    patient_id BIGINT,
    doctor_id  BIGINT,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_user_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE = InnoDB;

CREATE TABLE appointments (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    patient_id    BIGINT       NOT NULL,
    doctor_id     BIGINT       NOT NULL,
    start_time    DATETIME(6)  NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    reason        VARCHAR(500),
    created_at    DATETIME(6)  NOT NULL,
    cancelled_at  DATETIME(6),
    cancel_reason VARCHAR(500),
    -- A doctor cannot hold two *non-cancelled* appointments at the same time.
    -- active_slot is NULL for cancelled rows (MySQL allows duplicate NULLs in a
    -- unique index), so cancelled slots can be rebooked while active ones cannot.
    active_slot   DATETIME(6) GENERATED ALWAYS AS
        (CASE WHEN status <> 'CANCELLED' THEN start_time ELSE NULL END) STORED,
    PRIMARY KEY (id),
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT uq_doctor_active_slot UNIQUE (doctor_id, active_slot)
) ENGINE = InnoDB;

CREATE INDEX idx_appt_patient ON appointments (patient_id);
CREATE INDEX idx_appt_doctor ON appointments (doctor_id);

CREATE TABLE medical_records (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    patient_id BIGINT        NOT NULL,
    doctor_id  BIGINT        NOT NULL,
    diagnosis  VARCHAR(255)  NOT NULL,
    treatment  VARCHAR(2000),
    notes      VARCHAR(2000),
    created_at DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_record_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE = InnoDB;

CREATE INDEX idx_record_patient ON medical_records (patient_id);
