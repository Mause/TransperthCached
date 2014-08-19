import json
import os
import sqlite3


WEEKDAYS = 0x1
SATURDAY = 0x2
SUNDAY = 0x3


def setup(conn):
    cursor = conn.cursor()

    print("Creating visit table")
    cursor.execute(
        '''
        CREATE TABLE IF NOT EXISTS visit
        (
            stop_num text,
            visit_day_type integer,
            route_num integer,
            hour integer,
            minute integer
        )
        '''
    )

    print("Creating stops table")
    cursor.execute(
        '''
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
        )
        '''
    )

    print("Creating routes table")
    cursor.execute(
        '''
        CREATE TABLE IF NOT EXISTS routes
        (
            route_uid text,
            route_num text,
            route_name text,
            service_provider_uid text,
            mode text,
            info_url text,
            route_group text
        )
        '''
    )

    print("Creating indexes")
    cursor.execute('CREATE INDEX visit_stop_num_idx ON visit (stop_num);')


def dump_data(data, conn):
    cursor = conn.cursor()

    print("Adding stops to visit table")
    for stop_num, day_types in data.items():
        types = zip([WEEKDAYS, SATURDAY, SUNDAY], day_types)

        for day_type_num, day_type in types:
            for visit in day_type:
                hour, minute = map(int, visit[1].split(':'))

                cursor.execute(
                    'INSERT INTO visit VALUES (?, ?, ?, ?, ?)',
                    (
                        str(stop_num),
                        day_type_num,
                        visit[0],
                        hour,
                        minute
                    )
                )

def valid_key(haystack, needle):
    return haystack[needle] if needle in haystack else "Unknown"

def dump_stop_data(data, conn):
    cursor = conn.cursor()

    print("Dumping data into stops table")
    for item in data["TransitStopReferenceData"]:
        cursor.execute(
            'INSERT INTO stops VALUES (?,?,?,?,?,?,?,?)',
            (
                valid_key(item, "DataSet"),
                valid_key(item, "Code"),
                valid_key(item, "StopUid"),
                valid_key(item, "Description"),
                valid_key(item, "Position"),
                valid_key(item, "Zone"),
                valid_key(item, "SupportedModes"),
                valid_key(item, "Routes")
            )
        )

def dump_route_data(data, conn):
    cursor = conn.cursor()

    print("Dumping data into routes table")
    for item in data["RouteReferenceData"]:
        cursor.execute(
            'INSERT INTO routes VALUES (?,?,?,?,?,?,?)',
            (
                valid_key(item, "RouteUid"),
                valid_key(item, "Code"),
                valid_key(item, "Name"),
                valid_key(item, "ServiceProviderUid"),
                valid_key(item, "TransportMode"),
                valid_key(item, "Url"),
                valid_key(item, "RouteTimetableGroupId")
            )
        )

def main():
    db = 'Assets/transperthcache.db'
    if os.path.exists(db):
        os.unlink(db)

    conn = sqlite3.connect(db)

    setup(conn)

    with open('transperthcache.json') as fh:
        dump_data(
            json.load(fh),
            conn
        )

    with open('TransitStops.json') as fhb:
        dump_stop_data(
            json.load(fhb),
            conn
        )

    with open('Routes.json') as fhc:
        dump_route_data(
            json.load(fhc),
            conn
        )

    conn.commit()


if __name__ == '__main__':
    main()
