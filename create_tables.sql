CREATE TABLE IF NOT EXISTS visit
(
    stop_num text,
    visit_day_type integer,
    route_num integer,
    hour integer,
    minute integer
);

CREATE TABLE IF NOT EXISTS stops
(
    dataset text,
    stop_num text,
    stop_uid text,
    description text,
    lat_long text,
    zone text,
    modes text,
    routes text
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
