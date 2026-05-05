-- Seed data for Trello-like MVP (dev/test).
-- Safe for repeated startup with ON CONFLICT.

insert into users (id, created_at, updated_at, version, username, email, password_hash, full_name, system_role, active)
values (1, now(), now(), 0, 'demo_admin', 'admin@demo.local', '$2a$10$demo.hash.value', 'Demo Admin', 'ADMIN', true)
on conflict (id) do nothing;

insert into users (id, created_at, updated_at, version, username, email, password_hash, full_name, system_role, active)
values (2, now(), now(), 0, 'demo_user', 'user@demo.local', '$2a$10$demo.hash.value', 'Demo User', 'USER', true)
on conflict (id) do nothing;

insert into boards (id, created_at, updated_at, version, archived, name, description, visibility, owner_id)
values (1, now(), now(), 0, false, 'Product Roadmap', 'Demo board for Trello-style workflow', 'PRIVATE', 1)
on conflict (id) do nothing;

insert into board_members (id, created_at, updated_at, version, joined_at, role, board_id, user_id)
values (1, now(), now(), 0, now(), 'ADMIN', 1, 1)
on conflict (board_id, user_id) do nothing;

insert into board_members (id, created_at, updated_at, version, joined_at, role, board_id, user_id)
values (2, now(), now(), 0, now(), 'MEMBER', 1, 2)
on conflict (board_id, user_id) do nothing;

insert into board_lists (id, created_at, updated_at, version, archived, name, position, board_id)
values (1, now(), now(), 0, false, 'To Do', 1000, 1)
on conflict (board_id, name) do nothing;

insert into board_lists (id, created_at, updated_at, version, archived, name, position, board_id)
values (2, now(), now(), 0, false, 'Doing', 2000, 1)
on conflict (board_id, name) do nothing;

insert into board_lists (id, created_at, updated_at, version, archived, name, position, board_id)
values (3, now(), now(), 0, false, 'Done', 3000, 1)
on conflict (board_id, name) do nothing;

insert into labels (id, created_at, updated_at, version, color, name, board_id)
values (1, now(), now(), 0, 'green', 'backend', 1)
on conflict (board_id, name) do nothing;

insert into labels (id, created_at, updated_at, version, color, name, board_id)
values (2, now(), now(), 0, 'blue', 'frontend', 1)
on conflict (board_id, name) do nothing;

insert into cards (id, created_at, updated_at, version, archived, cover_color, description, due_date, position, start_date, title, board_id, created_by, list_id)
values (1, now(), now(), 0, false, 'blue', 'Design board-state API response', current_date + 3, 1000, current_date, 'Implement board-state endpoint', 1, 1, 1)
on conflict (id) do nothing;

insert into cards (id, created_at, updated_at, version, archived, cover_color, description, due_date, position, start_date, title, board_id, created_by, list_id)
values (2, now(), now(), 0, false, 'green', 'Wire board UI to GET /boards/{id}/board-state', current_date + 5, 1000, current_date, 'Connect frontend to board API', 1, 2, 2)
on conflict (id) do nothing;

insert into card_members (id, created_at, updated_at, version, card_id, user_id)
values (1, now(), now(), 0, 1, 1)
on conflict (card_id, user_id) do nothing;

insert into card_members (id, created_at, updated_at, version, card_id, user_id)
values (2, now(), now(), 0, 2, 2)
on conflict (card_id, user_id) do nothing;

insert into card_labels (id, created_at, updated_at, version, card_id, label_id)
values (1, now(), now(), 0, 1, 1)
on conflict (card_id, label_id) do nothing;

insert into card_labels (id, created_at, updated_at, version, card_id, label_id)
values (2, now(), now(), 0, 2, 2)
on conflict (card_id, label_id) do nothing;

insert into card_comments (id, created_at, updated_at, version, content, author_id, card_id)
values (1, now(), now(), 0, 'Initial demo comment on card', 1, 1)
on conflict (id) do nothing;

insert into activities (id, created_at, updated_at, version, payload, type, actor_id, board_id, card_id)
values (1, now(), now(), 0, '{"message":"Board initialized with demo data"}', 'BOARD_CREATED', 1, 1, null)
on conflict (id) do nothing;

insert into activities (id, created_at, updated_at, version, payload, type, actor_id, board_id, card_id)
values (2, now(), now(), 0, '{"message":"Created first card"}', 'CARD_CREATED', 1, 1, 1)
on conflict (id) do nothing;