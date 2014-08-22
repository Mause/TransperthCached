CREATE TABLE IF NOT EXISTS visit
(
    stop_num INTEGER,
    visit_day_type INTEGER,
    route_num INTEGER,
    hour INTEGER,
    minute INTEGER,

    FOREIGN KEY (stop_num) REFERENCES stops(stop_num)
);

CREATE TABLE IF NOT EXISTS stops
(
    dataset TEXT,
    stop_num INTEGER PRIMARY KEY,
    stop_uid TEXT,
    description TEXT,
    latitude REAL,
    longitude REAL,
    zone INTEGER,
    modes TEXT,
    routes TEXT
);

CREATE TABLE IF NOT EXISTS routes
(
    route_uid text,
    route_num text,
    route_name text,
    service_provider_uid text,
    mode text,
    info_url text,
    route_group text
);

CREATE INDEX visit_stop_num_idx ON visit (stop_num);

CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT "en_US");
INSERT INTO "android_metadata" VALUES ("en_US");
