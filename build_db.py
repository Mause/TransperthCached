import json
import os
import sqlite3


WEEKDAYS = 0x1
SATURDAY = 0x2
SUNDAY = 0x3


def setup(conn):
    cursor = conn.cursor()

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

    cursor.execute('CREATE INDEX visit_stop_num_idx ON visit (stop_num);')


def dump_data(data, conn):
    cursor = conn.cursor()

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

    conn.commit()


if __name__ == '__main__':
    main()
