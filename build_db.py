from os.path import join, dirname
import json
import os
import sqlite3


WEEKDAYS = 0x1
SATURDAY = 0x2
SUNDAY = 0x3

HERE = dirname(__file__)


def setup(conn):
    with open('create_tables.sql') as fh:
        conn.executescript(fh.read())


def dump_data(data, conn):
    cursor = conn.cursor()

    print("Dumping visits table")

    visits = (
        (
            str(stop_num),
            day_type_num,
            visit[0],
        ) + tuple(map(int, visit[1].split(':')))

        for stop_num, day_types in data.items()
        for day_type_num, day_type in zip(
            [WEEKDAYS, SATURDAY, SUNDAY], day_types
        )
        for visit in day_type
    )

    cursor.executemany('INSERT INTO visit VALUES (?, ?, ?, ?, ?)', visits)


def valid_key(haystack, needle):
    return haystack.get(needle, "Unknown")


def valid_int(haystack, needle):
    try:
        return int(haystack.get(needle))
    except (ValueError, TypeError):
        return 0


def dump_stop_data(data, conn):
    cursor = conn.cursor()

    print("Dumping data into stops table")

    transit_stops = []
    for item in data["TransitStopReferenceData"]:
        if item.get('Code') == '':
            continue

        lat, long = map(float, item["Position"].split(', '))

        transit_stops.append((
            item["DataSet"],
            valid_int(item, "Code"),
            item["StopUid"],
            item["Description"],
            lat,
            long,
            valid_int(item, "Zone"),
            item.get("SupportedModes", ""),
            item.get("Routes", "")
        ))

    cursor.executemany(
        'INSERT INTO stops VALUES (?,?,?,?,?,?,?,?,?)',
        transit_stops
    )


def dump_route_data(data, conn):
    cursor = conn.cursor()

    print("Dumping data into routes table")

    routes = (
        (
            valid_key(item, "RouteUid"),
            valid_key(item, "Code"),
            valid_key(item, "Name"),
            valid_key(item, "ServiceProviderUid"),
            valid_key(item, "TransportMode"),
            valid_key(item, "Url"),
            valid_key(item, "RouteTimetableGroupId")
        )
        for item in data["RouteReferenceData"]
    )

    cursor.executemany(
        'INSERT INTO routes VALUES (?,?,?,?,?,?,?)',
        routes
    )


def main():
    db = join(HERE, 'assets/transperthcache.db')
    if os.path.exists(db):
        os.unlink(db)

    conn = sqlite3.connect(db)

    setup(conn)

    RAW_ASSETS = join(HERE, 'raw_assets')
    with open(join(RAW_ASSETS, 'transperthcache.json')) as fh:
        dump_data(
            json.load(fh),
            conn
        )

    with open(join(RAW_ASSETS, 'TransitStops.json')) as fh:
        dump_stop_data(
            json.load(fh),
            conn
        )

    with open(join(RAW_ASSETS, 'Routes.json')) as fh:
        dump_route_data(
            json.load(fh),
            conn
        )

    conn.commit()
    conn.close()


if __name__ == '__main__':
    main()
