DELIMITER //
CREATE TRIGGER trg_insert_attendance_after_insert_schedule
    AFTER INSERT ON schedule
    FOR EACH ROW
BEGIN
    INSERT INTO attendance (student_id, class_id, schedule_id, status)
    SELECT
        e.student_id,
        NEW.class_id,
        NEW.schedule_id,
        'PRESENT'
    FROM enrollment e
    WHERE e.class_id = NEW.class_id
	AND NOT EXISTS (
        SELECT 1 FROM attendance a
        WHERE a.student_id = e.student_id
		AND a.schedule_id = NEW.schedule_id
    );
END //
DELIMITER ;

DELIMITER //
create trigger trg_insert_result_after_insert_schedule
after insert on schedule
for each row	
begin
	insert into result (student_id, class_id)
    select
		e.student_id,
        new.class_id
    from enrollment e
    where e.class_id = new.class_id
    and not exists (
		select 1 from result r
        where r.student_id = e.student_id
        and r.class_id = new.class_id
    );
end //
DELIMITER ;

DELIMITER //
create TRIGGER trg_insert_result_after_insert_enrollment
AFTER INSERT ON enrollment
FOR EACH ROW
BEGIN
    INSERT INTO result (student_id, class_id)
    VALUES (NEW.student_id, NEW.class_id);
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_insert_attendance_after_insert_enrollment
    AFTER INSERT ON enrollment
    FOR EACH ROW
BEGIN
    INSERT INTO attendance (student_id, schedule_id, class_id, status)
    SELECT
        NEW.student_id,
        s.schedule_id,
        NEW.class_id,
        'PRESENT'
    FROM schedule s
    WHERE s.class_id = NEW.class_id
      AND NOT EXISTS (
        SELECT 1
        FROM attendance a
        WHERE a.student_id = NEW.student_id
          AND a.schedule_id = s.schedule_id
    );
END //
DELIMITER ;