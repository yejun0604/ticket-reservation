-- Local development seed data
-- 50 deterministic test users
-- Safe to run repeatedly because conflicts are ignored.

INSERT INTO users (id, name, email)
VALUES
    (30001, 'Alex Kim', 'seed.user01@example.com'),
    (30002, 'Jordan Lee', 'seed.user02@example.com'),
    (30003, 'Taylor Park', 'seed.user03@example.com'),
    (30004, 'Morgan Choi', 'seed.user04@example.com'),
    (30005, 'Casey Smith', 'seed.user05@example.com'),
    (30006, 'Riley Brown', 'seed.user06@example.com'),
    (30007, 'Jamie Wilson', 'seed.user07@example.com'),
    (30008, 'Avery Taylor', 'seed.user08@example.com'),
    (30009, 'Cameron Martin', 'seed.user09@example.com'),
    (30010, 'Dylan Anderson', 'seed.user10@example.com'),
    (30011, 'Ethan Thomas', 'seed.user11@example.com'),
    (30012, 'Noah Moore', 'seed.user12@example.com'),
    (30013, 'Liam Jackson', 'seed.user13@example.com'),
    (30014, 'Mason White', 'seed.user14@example.com'),
    (30015, 'Lucas Harris', 'seed.user15@example.com'),
    (30016, 'Mia Clark', 'seed.user16@example.com'),
    (30017, 'Emma Lewis', 'seed.user17@example.com'),
    (30018, 'Olivia Walker', 'seed.user18@example.com'),
    (30019, 'Sophie Hall', 'seed.user19@example.com'),
    (30020, 'Chloe Young', 'seed.user20@example.com'),
    (30021, 'Daniel King', 'seed.user21@example.com'),
    (30022, 'Ryan Wright', 'seed.user22@example.com'),
    (30023, 'Nathan Scott', 'seed.user23@example.com'),
    (30024, 'Aaron Green', 'seed.user24@example.com'),
    (30025, 'Leo Baker', 'seed.user25@example.com'),
    (30026, 'Grace Adams', 'seed.user26@example.com'),
    (30027, 'Ella Nelson', 'seed.user27@example.com'),
    (30028, 'Zoe Hill', 'seed.user28@example.com'),
    (30029, 'Hannah Campbell', 'seed.user29@example.com'),
    (30030, 'Ruby Mitchell', 'seed.user30@example.com'),
    (30031, 'Ben Roberts', 'seed.user31@example.com'),
    (30032, 'Jack Carter', 'seed.user32@example.com'),
    (30033, 'Sam Phillips', 'seed.user33@example.com'),
    (30034, 'Luke Evans', 'seed.user34@example.com'),
    (30035, 'Max Turner', 'seed.user35@example.com'),
    (30036, 'Isla Parker', 'seed.user36@example.com'),
    (30037, 'Lucy Collins', 'seed.user37@example.com'),
    (30038, 'Amelia Edwards', 'seed.user38@example.com'),
    (30039, 'Emily Stewart', 'seed.user39@example.com'),
    (30040, 'Sarah Morris', 'seed.user40@example.com'),
    (30041, 'Chris Rogers', 'seed.user41@example.com'),
    (30042, 'Matt Reed', 'seed.user42@example.com'),
    (30043, 'Josh Cook', 'seed.user43@example.com'),
    (30044, 'Tom Morgan', 'seed.user44@example.com'),
    (30045, 'Will Bell', 'seed.user45@example.com'),
    (30046, 'Anna Murphy', 'seed.user46@example.com'),
    (30047, 'Kate Bailey', 'seed.user47@example.com'),
    (30048, 'Nina Rivera', 'seed.user48@example.com'),
    (30049, 'Maya Cooper', 'seed.user49@example.com'),
    (30050, 'Eva Ward', 'seed.user50@example.com')
ON CONFLICT DO NOTHING;

-- Keep Hibernate's generated-id sequence ahead of manually seeded IDs.
SELECT setval(
    to_regclass('users_seq'),
    (SELECT COALESCE(MAX(id), 1) FROM users),
    true
)
WHERE to_regclass('users_seq') IS NOT NULL;
