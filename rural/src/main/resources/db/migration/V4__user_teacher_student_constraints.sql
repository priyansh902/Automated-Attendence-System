-- 1. Ensure username unique in user
ALTER TABLE user
ADD CONSTRAINT uk_user_username UNIQUE (username);

-- 2. Teacher → User (1:1)
ALTER TABLE teacher
ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
ADD CONSTRAINT uk_teacher_user UNIQUE (user_id);

-- 3. Student → User (1:1)
ALTER TABLE student
ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
ADD CONSTRAINT uk_student_user UNIQUE (user_id);

-- 4. Student roll number unique
ALTER TABLE student
ADD CONSTRAINT uk_student_rollno UNIQUE (rollno);
