INSERT  INTO users(id, username, email, role, password) VALUES (10, 'testuser', 'testuser@gmail.com', 'USER', 'password'), (11, 'testadmin', 'testadmin@gmail.com', 'ADMIN', 'password');

INSERT INTO courses (id,title, description, completed) VALUES (10,'Math', 'Mathematics course', true);

INSERT INTO course_author (user_id, course_id) VALUES (10,10);

