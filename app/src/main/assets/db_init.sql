CREATE TABLE "current_user" (
    "id"                INTEGER PRIMARY KEY,
    "email"             TEXT UNIQUE NOT NULL,
    "token"             TEXT NOT NULL,
    "username"          TEXT NOT NULL,
    "password"          TEXT NOT NULL,
    "last_session_time" INTEGER NOT NULL
);

CREATE TABLE "bookmarks" (
    "has_image" INTEGER,
    "message"   TEXT,
    "image"     TEXT,
    "time"      INTEGER NOT NULL
);