-- users
INSERT INTO users (id, external_id, name, email, last_sync_at, is_deleted)
VALUES ('11111111-1111-1111-1111-111111111111', 'ext-user-1', 'Test User', 'test@example.com', NOW(), false);

-- chats
INSERT INTO chats (id, name, last_sync_at, is_deleted)
VALUES ('22222222-2222-2222-2222-222222222222', 'Test Chat', NOW(), false);

-- members
INSERT INTO chat_members (chat_id, user_id, created_at)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', NOW());

-- messages
INSERT INTO messages (id, text, author_id, chat_id, created_at)
VALUES
    ('33333333-3333-3333-3333-333333333333', 'Message 1', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '5 minutes'),
    ('44444444-4444-4444-4444-444444444444', 'Message 2', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '3 minutes');
