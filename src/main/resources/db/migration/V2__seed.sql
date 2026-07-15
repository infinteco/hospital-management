-- Demo seed data. All users share the password "Password123!" (bcrypt below).
-- Change these before any real deployment.

INSERT INTO patients (id, full_name, email, phone, date_of_birth, gender, created_at) VALUES
    (1, 'Alice Patient', 'alice@example.com', '+91-9000000001', '1992-04-12', 'Female', NOW(6)),
    (2, 'Bob Patient',   'bob@example.com',   '+91-9000000002', '1988-11-03', 'Male',   NOW(6));

INSERT INTO doctors (id, full_name, specialization, email, phone, created_at) VALUES
    (1, 'Dr. Sara Smith', 'Cardiology',  'smith@example.com', '+91-9000000101', NOW(6)),
    (2, 'Dr. John Jones', 'Dermatology', 'jones@example.com', '+91-9000000102', NOW(6));

-- bcrypt("Password123!") — same hash reused for all demo accounts.
INSERT INTO users (username, password, role, enabled, patient_id, doctor_id, created_at) VALUES
    ('admin',    '$2y$10$ZxwlcuPHSYdcgrD/DKq15Ocl89RyTgp36Mq4MPhZSbWVPnWsp5b0S', 'ADMIN',   1, NULL, NULL, NOW(6)),
    ('dr.smith', '$2y$10$ZxwlcuPHSYdcgrD/DKq15Ocl89RyTgp36Mq4MPhZSbWVPnWsp5b0S', 'DOCTOR',  1, NULL, 1,    NOW(6)),
    ('dr.jones', '$2y$10$ZxwlcuPHSYdcgrD/DKq15Ocl89RyTgp36Mq4MPhZSbWVPnWsp5b0S', 'DOCTOR',  1, NULL, 2,    NOW(6)),
    ('alice',    '$2y$10$ZxwlcuPHSYdcgrD/DKq15Ocl89RyTgp36Mq4MPhZSbWVPnWsp5b0S', 'PATIENT', 1, 1,    NULL, NOW(6)),
    ('bob',      '$2y$10$ZxwlcuPHSYdcgrD/DKq15Ocl89RyTgp36Mq4MPhZSbWVPnWsp5b0S', 'PATIENT', 1, 2,    NULL, NOW(6));

INSERT INTO appointments (patient_id, doctor_id, start_time, status, reason, created_at) VALUES
    (1, 1, '2030-01-15 10:00:00', 'SCHEDULED', 'Routine cardiac check-up', NOW(6));

INSERT INTO medical_records (patient_id, doctor_id, diagnosis, treatment, notes, created_at) VALUES
    (1, 1, 'Mild hypertension', 'Lifestyle changes; recheck in 3 months', 'BP 140/90', NOW(6));
