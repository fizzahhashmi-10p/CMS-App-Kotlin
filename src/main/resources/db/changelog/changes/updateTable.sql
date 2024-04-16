ALTER TABLE course
ADD CONSTRAINT U_course_per_author UNIQUE (author,title);