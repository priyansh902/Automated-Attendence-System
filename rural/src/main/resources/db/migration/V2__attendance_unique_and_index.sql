ALTER TABLE attendance
ADD CONSTRAINT uk_student_date UNIQUE (student_id, date);

CREATE INDEX idx_attendence_date ON attendence (date);