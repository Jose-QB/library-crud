GRANT SELECT, INSERT, UPDATE, DELETE
ON TABLE library.app_user
TO library_user;

GRANT USAGE, SELECT
ON SEQUENCE library.app_user_id_seq
TO library_user;