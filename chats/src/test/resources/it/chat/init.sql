-- users
INSERT INTO users (id, external_id, name, email, last_sync_at, is_deleted)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'ext1', 'User One', 'user1@example.com', NOW(), false),
    ('00000000-0000-0000-0000-000000000002', 'ext2', 'User Two', 'user2@example.com', NOW(), false),
    ('00000000-0000-0000-0000-000000000003', 'ext3', 'User Three', 'user3@example.com', NOW(), false);

-- chats
INSERT INTO chats (id, name, owner_id, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'First Chat', '00000000-0000-0000-0000-000000000001', NOW() - INTERVAL '2 days'),
    ('00000000-0000-0000-0000-000000000002', 'Second Chat', '00000000-0000-0000-0000-000000000002', NOW() - INTERVAL '1 day'),
    ('00000000-0000-0000-0000-000000000003', 'Third Chat', '00000000-0000-0000-0000-000000000001', NOW());

-- members
INSERT INTO chat_members (chat_id, user_id, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', NOW()),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', NOW()),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', NOW()),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', NOW());
