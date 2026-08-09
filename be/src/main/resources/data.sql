-- =========================================================================
-- 1. ROLES & FEATURES SETUP
-- =========================================================================

INSERT IGNORE INTO roles (id, created_at, created_by, updated_at, updated_by, name, is_default) VALUES
    (1, NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'ROLE_ROOT', false),
    (2, NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'ROLE_ADMIN', false),
    (3, NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'ROLE_LIBRARIAN', false),
    (4, NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'ROLE_USER', true);

INSERT IGNORE INTO features (id, name) VALUES
    (1, 'CREATE_BOOK'),
    (2, 'CREATE_USER'),
    (3, 'CREATE_BORROW'),
    (4, 'CREATE_ROLE'),
    (5, 'CREATE_POST'),
    (6, 'CREATE_COMMENT'),
    (7, 'CREATE_GENRE'),
    (8, 'GET_BOOK'),
    (9, 'GET_USER'),
    (10, 'GET_BORROW'),
    (11, 'GET_BORROW_MULTI'),
    (12, 'GET_ROLE'),
    (13, 'GET_POST'),
    (14, 'GET_COMMENT'),
    (15, 'GET_GENRE'),
    (16, 'GET_POLICY'),
    (17, 'UPDATE_BOOK'),
    (18, 'UPDATE_USER'),
    (19, 'UPDATE_USER_ROLE'),
    (20, 'UPDATE_BORROW'),
    (21, 'UPDATE_ROLE'),
    (22, 'UPDATE_POST'),
    (23, 'UPDATE_GENRE'),
    (24, 'UPDATE_POLICY'),
    (25, 'DELETE_BOOK'),
    (26, 'DELETE_USER'),
    (27, 'DELETE_USER_MULTI'),
    (28, 'DELETE_BORROW'),
    (29, 'DELETE_ROLE'),
    (30, 'DELETE_POST'),
    (31, 'DELETE_COMMENT'),
    (32, 'DELETE_GENRE'),
    (33, 'ASSIGN_FEATURE'),
    (34, 'UNASSIGN_FEATURE'),
    (35, 'EXPORT_USER'),
    (36, 'EXPORT_BOOK'),
    (37, 'EXPORT_BORROW');

INSERT IGNORE INTO features_roles (role_id, feature_id) VALUES
    (4, 5),  (4, 6),  (4, 8),  (4, 9),  (4, 10),
    (4, 11),  (4, 12), (4, 13), (4, 14), (4, 19),
    (4, 20), (4, 26), (4, 27),

    (3, 1),  (3, 3),  (3, 5),  (3, 6),  (3, 7),
    (3, 8),  (3, 9),  (3, 10), (3, 11), (3, 12),
    (3, 13), (3, 14), (3, 15), (3, 17), (3, 19),
    (3, 20), (3, 21), (3, 22), (3, 24), (3, 26),
    (3, 27), (3, 28), (3, 31), (3, 32), (3,33);

-- =========================================================================
-- 2. USERS (Tyler as ROOT, everyone else as USER)
-- =========================================================================

INSERT IGNORE INTO users (id, full_name, username, address, email, phone_number, password, role_id, created_at, is_deleted, is_active) VALUES
    (1, 'Tyler Dao', 'tyler', '32 Noecker Street', 'baonamfpt@gmail.com', '5483843681', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '1', NOW(), false, true),
    (2, 'Alex Mercer', 'alex_m', '123 University Ave', 'baonamfpt@gmail.com', '5195550143', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '4', NOW(), false, true),
    (3, 'Chloe Laurent', 'chloe_l', '88 Columbia St W', 'baonamfpt@gmail.com', '5195550177', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '3', NOW(), false, true),
    (4, 'Marcus Vance', 'marcus_v', '12 King St N', 'baonamfpt@gmail.com', '5195550198', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '2', NOW(), false, true),
    (5, 'Sarah Jenkins', 'sarah_j', '45 Phillip St', 'baonamfpt@gmail.com', '5195550211', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '4', NOW(), false, true);

-- =========================================================================
-- 3. GENRES & BOOKS SETUP
-- =========================================================================

INSERT IGNORE INTO genres (id, name) VALUES
    (1, 'Sci-fi'),
    (2, 'Romance'),
    (3, 'Manga'),
    (4, 'Historical Fiction'),
    (5, 'War Story'),
    (6, 'Fantasy'),
    (7, 'Mystery'),
    (8, 'Thriller'),
    (9, 'Dystopian'),
    (10, 'Self-Help');

INSERT IGNORE INTO books (id, title, author, copies, created_at, cover_url) VALUES
    (1, 'Project Hail Mary', 'Andy Weir', 10, DATE_SUB(NOW(), INTERVAL 6 DAY), '1.jpg'),
    (2, 'We Are Legion (We Are Bob)', 'Dennis E. Taylor', 5, DATE_SUB(NOW(), INTERVAL 6 DAY), '2.jpg'),
    (3, 'The Song of Achilles', 'Madeline Miller', 1, DATE_SUB(NOW(), INTERVAL 18 DAY), '3.jpg'),
    (4, 'The Martian', 'Andy Weir', 0, DATE_SUB(NOW(), INTERVAL 10 DAY), '4.jpg'),
    (5, 'Dune', 'Frank Herbert', 0, DATE_SUB(NOW(), INTERVAL 19 DAY), '5.jpg'),
    (6, 'Neuromancer', 'William Gibson', 0, DATE_SUB(NOW(), INTERVAL 5 DAY), '6.jpg'),
    (7, 'Pride and Prejudice', 'Jane Austen', 0, DATE_SUB(NOW(), INTERVAL 1 DAY), '7.jpg'),
    (8, 'The Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', 0, DATE_SUB(NOW(), INTERVAL 17 DAY), '8.jpg'),
    (9, 'Spy x Family, Vol. 1', 'Tatsuya Endo', 0, DATE_SUB(NOW(), INTERVAL 1 DAY), '9.jpg'),
    (10, 'Demon Slayer: Kimetsu no Yaiba, Vol. 1', 'Koyoharu Gotouge', 0, DATE_SUB(NOW(), INTERVAL 2 DAY), '10.jpg'),
    (11, 'Chainsaw Man, Vol. 1', 'Tatsuki Fujimoto', 11, DATE_SUB(NOW(), INTERVAL 9 DAY), '11.jpg'),
    (12, 'The Book Thief', 'Markus Zusak', 5, DATE_SUB(NOW(), INTERVAL 2 DAY), '12.jpg'),
    (13, 'All the Light We Cannot See', 'Anthony Doerr', 0, DATE_SUB(NOW(), INTERVAL 18 DAY), '13.jpg'),
    (14, 'The Nightingale', 'Kristin Hannah', 6, DATE_SUB(NOW(), INTERVAL 20 DAY), '14.jpg'),
    (15, 'Circe', 'Madeline Miller', 4, DATE_SUB(NOW(), INTERVAL 9 DAY), '15.jpg'),
    (16, 'Snow Crash', 'Neal Stephenson', 3, DATE_SUB(NOW(), INTERVAL 0 DAY), '16.jpg'),
    (17, 'A Court of Thorns and Roses', 'Sarah J. Maas', 8, DATE_SUB(NOW(), INTERVAL 1 DAY), '17.jpg'),
    (18, 'Kaguya-sama: Love Is War, Vol. 1', 'Aka Akasaka', 0, DATE_SUB(NOW(), INTERVAL 6 DAY), '18.jpg'),
    (19, 'The Hobbit', 'J.R.R. Tolkien', 12, DATE_SUB(NOW(), INTERVAL 5 DAY), '19.jpg'),
    (20, 'The Fellowship of the Ring', 'J.R.R. Tolkien', 8, DATE_SUB(NOW(), INTERVAL 4 DAY), '20.jpg'),
    (21, 'Gone Girl', 'Gillian Flynn', 6, DATE_SUB(NOW(), INTERVAL 3 DAY), '21.jpg'),
    (22, 'The Girl with the Dragon Tattoo', 'Stieg Larsson', 0, DATE_SUB(NOW(), INTERVAL 9 DAY), '22.jpg'),
    (23, '1984', 'George Orwell', 15, DATE_SUB(NOW(), INTERVAL 7 DAY), '23.jpg'),
    (24, 'Atomic Habits', 'James Clear', 0, DATE_SUB(NOW(), INTERVAL 8 DAY), '24.jpg');

INSERT IGNORE INTO books_genres (book_id, genre_id) VALUES
    (1, 1),
    (2, 1),
    (3, 2), (3, 4), (3, 5),
    (4, 1),
    (5, 1),
    (6, 1),
    (7, 2), (7, 4),
    (8, 2), (8, 4),
    (9, 3),
    (10, 3),
    (11, 3),
    (12, 4), (12, 5),
    (13, 4), (13, 5),
    (14, 4), (14, 5),
    (15, 4),
    (16, 1),
    (17, 2),
    (18, 2), (18, 3),
    (19, 6),
    (20, 6),
    (21, 7), (21, 8),
    (22, 7), (22, 8),
    (23, 1), (23, 9),
    (24, 10),
    (25, 4);

-- =========================================================================
-- 4. SOCIAL ENGINE
-- =========================================================================

INSERT IGNORE INTO posts (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, content, subject, book_id) VALUES
    -- Posts from 14 days ago
    (2, DATE_SUB(NOW(), INTERVAL 13 DAY), 'tyler', true, false, null, null, 'This novel is written by Andy Weir, who is the author of Martian.', 'Review of my favorite novel recently!!', 1),

    -- Posts from 12 days ago
    (3, DATE_SUB(NOW(), INTERVAL 12 DAY), 'alex_m', true, false, null, null, 'An absolute classic. Middle-earth feels so alive.', 'Classic Fantasy Masterpiece!', 19),
    (4, DATE_SUB(NOW(), INTERVAL 11 DAY), 'chloe_l', true, false, null, null, 'The psychological twists kept me up until 3 AM.', 'Could not put this down!', 21),

    -- Posts from 10 days ago
    (5, DATE_SUB(NOW(), INTERVAL 10 DAY), 'marcus_v', true, false, null, null, 'Extremely relevant even today. Scariest book I have ever read.', 'Chilling dystopian warning', 23),
    (6, DATE_SUB(NOW(), INTERVAL 9 DAY), 'sarah_j', true, false, null, null, 'Completely redefined how I approach my morning routine.', 'Life changing frameworks', 24),

    -- Posts from 8 days ago
    (7, DATE_SUB(NOW(), INTERVAL 8 DAY), 'tyler', true, false, null, null, 'Just finished Dune. The political intrigue and world-building live up to every bit of the hype.', 'Dune lived up to the hype', 5),
    (8, DATE_SUB(NOW(), INTERVAL 7 DAY), 'tyler', true, false, null, null, 'Bob Johansson wakes up as an AI in a Von Neumann probe. The humor and science blend perfectly.', 'We Are Legion is endlessly fun', 2),

    -- Posts from 6 days ago
    (9, DATE_SUB(NOW(), INTERVAL 6 DAY), 'tyler', true, false, null, null, 'William Gibson essentially predicted the modern internet framework back in 1984. Mind-blowing prose.', 'Cyberpunk roots: Neuromancer', 6),
    (10, DATE_SUB(NOW(), INTERVAL 5 DAY), 'tyler', true, false, null, null, 'Currently weeping. Madeline Miller has a way of writing heartbreak that lingers for days.', 'Song of Achilles broke me', 3),

    -- Posts from 4 days ago
    (11, DATE_SUB(NOW(), INTERVAL 4 DAY), 'alex_m', true, false, null, null, 'If you loved Project Hail Mary, you need to read this one next. Hard science fiction at its best.', 'Another Andy Weir classic', 4),
    (12, DATE_SUB(NOW(), INTERVAL '4 4' DAY_HOUR), 'alex_m', true, false, null, null, 'The tension builds so flawlessly in this book. Stieg Larsson created an unforgettable duo.', 'Lisbeth Salander is an amazing character', 22),
    (13, DATE_SUB(NOW(), INTERVAL 3 DAY), 'alex_m', true, false, null, null, 'A brilliant breakdown of how small changes compound into massive results over time.', 'Practical frameworks for habit building', 24),
    (14, DATE_SUB(NOW(), INTERVAL '3 6' DAY_HOUR), 'alex_m', true, false, null, null, 'The historical detail here is immense. A stark, moving depiction of survival during wartime.', 'Devastatingly beautiful historical fiction', 14),

    -- Posts from 2 days ago
    (15, DATE_SUB(NOW(), INTERVAL 2 DAY), 'chloe_l', true, false, null, null, 'A masterpiece of perspective. The mythological elements feel incredibly grounded and human.', 'Circes perspective is everything', 25),
    (16, DATE_SUB(NOW(), INTERVAL '2 2' DAY_HOUR), 'chloe_l', true, false, null, null, 'Classic enemies-to-lovers blueprint. Elizabeth Bennet and Mr. Darcy have my whole heart.', 'Rereading an all-time favorite romance', 7),
    (17, DATE_SUB(NOW(), INTERVAL '2 8' DAY_HOUR), 'chloe_l', true, false, null, null, 'The Forger family dynamic is hilarious yet wholesome. Anya carries the entire narrative!', 'Spy x Family is pure joy', 9),
    (18, DATE_SUB(NOW(), INTERVAL 1 DAY), 'chloe_l', true, false, null, null, 'The prose is lyrical, almost like poetry. A haunting look at lives intertwined during WWII.', 'A beautiful story about the power of words', 13),

    -- Posts from yesterday/today
    (19, DATE_SUB(NOW(), INTERVAL 18 HOUR), 'marcus_v', true, false, null, null, 'Tatsuki Fujimoto defies all traditional Shonen conventions. This ride is absolute chaos.', 'Chainsaw Man is unhinged brilliance', 11),
    (20, DATE_SUB(NOW(), INTERVAL 14 HOUR), 'marcus_v', true, false, null, null, 'The pacing in the first half is slow, but the final acts payout massively. An essential sci-fi epic.', 'The depth of worldbuilding in Dune', 5),
    (21, DATE_SUB(NOW(), INTERVAL 12 HOUR), 'marcus_v', true, false, null, null, 'This book handles the narrative of death in such a unique, beautiful way. Unforgettable.', 'Liesel Meminger, the book thief', 12),
    (22, DATE_SUB(NOW(), INTERVAL 10 HOUR), 'marcus_v', true, false, null, null, 'The world-building is fascinating, but the high-tech corporate satire is what keeps me hooked.', 'Snow Crash holds up surprisingly well', 16),

    -- Recent posts
    (23, DATE_SUB(NOW(), INTERVAL 6 HOUR), 'alex_m', true, false, null, null, 'The psychological manipulation and unreliable narrator tricks used here are top-tier.', 'Unreliable narrators at their finest', 21),
    (24, DATE_SUB(NOW(), INTERVAL 4 HOUR), 'sarah_j', true, false, null, null, 'The transition from lighthearted high school comedy to high-stakes psychological warfare is epic.', 'Mind games and romance mixed perfectly', 18),
    (25, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'marcus_v', true, false, null, null, 'The emotional bond between the main characters makes the inevitable tragedy sting so much worse.', 'A modern classic mythology retelling', 3),
    (26, DATE_SUB(NOW(), INTERVAL 15 MINUTE), 'sarah_j', true, false, null, null, 'The action choreography paneling is some of the cleanest I have ever seen in modern manga.', 'Tanjiros journey begins here', 10),
    (27, DATE_SUB(NOW(), INTERVAL 17 MINUTE), 'chloe_l', true, false, null, null, 'Dennis Taylor writes Bob with such humor and heart. A must-read for sci-fi fans.', 'We Are Legion is a hidden gem', 2);

INSERT IGNORE INTO post_likes (id, post_id, user_id) VALUES
    (1, 2, 1),
    (2, 1, 2), (3, 1, 3),
    (4, 2, 3), (5, 2, 4), (6, 2, 5),
    (7, 3, 1), (8, 3, 4),
    (9, 4, 1), (10, 4, 5),
    (11, 5, 2), (12, 5, 3), (13, 5, 4);

INSERT IGNORE INTO comments (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, content, post_id) VALUES
    (1, NOW(), 'tyler', true, false, null, null, 'Great post!', 1),
    (2, NOW(), 'alex_m', true, false, null, null, 'Agreed, Andy Weir is brilliant!', 2),
    (3, NOW(), 'tyler', true, false, null, null, 'I love Tolkien! Have you read the Silmarillion?', 3),
    (4, NOW(), 'marcus_v', true, false, null, null, 'The ending of this book completely broke my brain.', 4),
    (5, NOW(), 'sarah_j', true, false, null, null, 'Big Brother is watching.', 5),
    (6, NOW(), 'chloe_l', true, false, null, null, 'Started implementing the 2-minute rule because of this!', 6),
    (7, DATE_SUB(NOW(), INTERVAL '13 2' DAY_HOUR), 'chloe_l', true, false, null, null, 'I read this in one sitting! It is absolutely precious.', 1),
    (8, DATE_SUB(NOW(), INTERVAL '12 5' DAY_HOUR), 'sarah_j', true, false, null, null, 'The art style gives such nostalgic 80s/90s manga vibes. Love it.', 1),

    -- Discussion on Post 3 (The Hobbit)
    (9, DATE_SUB(NOW(), INTERVAL '11 4' DAY_HOUR), 'marcus_v', true, false, null, null, 'The chapter "Riddles in the Dark" is pure perfection.', 3),
    (10, DATE_SUB(NOW(), INTERVAL '10 1' DAY_HOUR), 'alex_m', true, false, null, null, 'Agreed. It is a much lighter read than Lord of the Rings but just as magical.', 3),

    -- Discussion on Post 4 (Gone Girl)
    (11, DATE_SUB(NOW(), INTERVAL '10 3' DAY_HOUR), 'sarah_j', true, false, null, null, 'Amy Dunne is one of the most terrifyingly well-written characters ever.', 4),
    (12, DATE_SUB(NOW(), INTERVAL '9 8' DAY_HOUR), 'tyler', true, false, null, null, 'That mid-book twist completely changed how I look at psychological thrillers.', 4),

    -- Discussion on Post 5 (1984)
    (13, DATE_SUB(NOW(), INTERVAL '9 2' DAY_HOUR), 'alex_m', true, false, null, null, 'The concept of Newspeak and Doublethink still blows my mind.', 5),
    (14, DATE_SUB(NOW(), INTERVAL '8 12' DAY_HOUR), 'chloe_l', true, false, null, null, 'It is crazy how a book written in 1949 feels so prophetic.', 5),

    -- Discussion on Post 6 (Atomic Habits)
    (15, DATE_SUB(NOW(), INTERVAL '8 2' DAY_HOUR), 'marcus_v', true, false, null, null, 'The environment design chapter helped me stop checking my phone during work.', 6),
    (16, DATE_SUB(NOW(), INTERVAL '7 5' DAY_HOUR), 'tyler', true, false, null, null, 'Consistency beats intensity. That lesson stuck with me.', 6),

    -- Discussion on Post 7 (Dune)
    (17, DATE_SUB(NOW(), INTERVAL '7 1' DAY_HOUR), 'alex_m', true, false, null, null, 'The spice must flow! Are you planning to read the sequels?', 7),
    (18, DATE_SUB(NOW(), INTERVAL '6 18' DAY_HOUR), 'marcus_v', true, false, null, null, 'The worldbuilding is unmatched, but the political maneuvering is the best part.', 7),

    -- Discussion on Post 11 (The Martian)
    (19, DATE_SUB(NOW(), INTERVAL '3 22' DAY_HOUR), 'tyler', true, false, null, null, '"I am going to have to science the s*** out of this." Best line ever.', 11),
    (20, DATE_SUB(NOW(), INTERVAL '3 10' DAY_HOUR), 'sarah_j', true, false, null, null, 'Mark Watney’s optimism makes a brutal situation so entertaining.', 11),

    -- Discussion on Post 12 (The Girl with the Dragon Tattoo)
    (21, DATE_SUB(NOW(), INTERVAL '3 18' DAY_HOUR), 'chloe_l', true, false, null, null, 'Lisbeth Salander is an icon. Such a dark but incredible mystery.', 12),

    -- Discussion on Post 15 (Circe)
    (22, DATE_SUB(NOW(), INTERVAL '1 20' DAY_HOUR), 'tyler', true, false, null, null, 'Madeline Miller does it again. The way she builds Circe’s isolation is beautiful.', 15),
    (23, DATE_SUB(NOW(), INTERVAL '1 15' DAY_HOUR), 'sarah_j', true, false, null, null, 'Turning those sailors into pigs was a top-tier moment.', 15),

    -- Discussion on Post 16 (Pride and Prejudice)
    (24, DATE_SUB(NOW(), INTERVAL '1 18' DAY_HOUR), 'alex_m', true, false, null, null, 'Mr. Darcy’s first proposal is the ultimate definition of fumbling the bag.', 16),
    (25, DATE_SUB(NOW(), INTERVAL '1 4' DAY_HOUR), 'marcus_v', true, false, null, null, 'The wit and sarcasm in Jane Austen’s writing holds up so well.', 16),

    -- Discussion on Post 19 (Chainsaw Man)
    (26, DATE_SUB(NOW(), INTERVAL 12 HOUR), 'chloe_l', true, false, null, null, 'It gets even more unhinged as the volumes go on. Enjoy the ride!', 19),
    (27, DATE_SUB(NOW(), INTERVAL 8 HOUR), 'alex_m', true, false, null, null, 'The power system and the devil designs are so refreshing for modern Shonen.', 19),

    -- Discussion on Post 23 (Gone Girl - Unreliable Narrators)
    (28, DATE_SUB(NOW(), INTERVAL 4 HOUR), 'chloe_l', true, false, null, null, 'If you loved this, you should check out "The Silent Patient" next.', 23),

    -- Discussion on Post 24 (Kaguya-sama)
    (29, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'tyler', true, false, null, null, 'They are both operational geniuses but completely incompetent at romance. It’s hilarious.', 24),

    -- Discussion on Post 26 (Demon Slayer)
    (30, DATE_SUB(NOW(), INTERVAL 5 MINUTE), 'marcus_v', true, false, null, null, 'The bond between Tanjiro and Nezuko is the heart of the whole series.', 26);

-- =========================================================================
-- 5. CORE TRANSACTIONS (Borrows with relative date scopes)
-- =========================================================================

INSERT IGNORE INTO borrows (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, due_date, book_id, user_id) VALUES
    (1, NOW(), 'tyler', 0, 0, null, null, '2026-06-10', 1, 1),
    (3, NOW(), 'tyler', 1, 0, null, null, '2026-06-01', 2, 1),
    (4, '2026-04-25 10:00:00', 'tyler', 0, 0, null, null, '2026-05-10', 3, 1),
    -- Overdue: Due 25 days ago
    (5, DATE_SUB(NOW(), INTERVAL 40 DAY), 'alex_m', 1, 0, null, null, DATE_SUB(NOW(), INTERVAL 25 DAY), 5, 2),
    -- Overdue: Due 5 days ago
    (6, DATE_SUB(NOW(), INTERVAL 20 DAY), 'chloe_l', 1, 0, null, null, DATE_SUB(NOW(), INTERVAL 5 DAY), 9, 3),
    -- Active: Due in 12 days
    (7, NOW(), 'marcus_v', 1, 0, null, null, DATE_ADD(NOW(), INTERVAL 12 DAY), 23, 4),
    -- Active: Due in 28 days
    (8, NOW(), 'sarah_j', 1, 0, null, null, DATE_ADD(NOW(), INTERVAL 28 DAY), 24, 5),
    -- Historical/Returned: Borrowed and returned in the past
    (9, DATE_SUB(NOW(), INTERVAL 30 DAY), 'alex_m', 0, 0, NOW(), 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), 21, 2),
    -- Active: Due in 5 days
    (10, NOW(), 'tyler', 1, 0, null, null, DATE_ADD(NOW(), INTERVAL 5 DAY), 19, 1);

INSERT IGNORE INTO policies (policy_key, policy_value) VALUES
    ("borrow_duration", "14"),
    ("late_penalty_per_day", "10000");