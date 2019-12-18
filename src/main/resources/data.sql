DROP TABLE IF EXISTS room CASCADE;
CREATE TABLE IF NOT EXISTS room (
  id          INTEGER PRIMARY KEY auto_increment,
  room_type    VARCHAR NOT NULL,
  rating DOUBLE NOT NULL,
  PRIMARY KEY (id));

DROP TABLE IF EXISTS reservation;
CREATE TABLE IF NOT EXISTS reservation (
  id          INTEGER PRIMARY KEY auto_increment,
  username    VARCHAR(64) NOT NULL,
  number_of_people INTEGER NOT NULL,
  start_date  DATE NOT NULL,
  end_date    DATE NOT NULL,
  room_id INTEGER,
  PRIMARY KEY (id),
  FOREIGN KEY (room_id) REFERENCES room (id));

INSERT INTO room VALUES(1, 'PENTHOUSE', 4.5);
INSERT INTO room VALUES(2, 'BASIC', 3.5);
INSERT INTO room VALUES(3, 'BASIC', 3.5);

INSERT INTO reservation VALUES(1, 'test_user', 7, '2020-12-17', '2020-12-20', 1);
INSERT INTO reservation VALUES(2, 'test_user', 8, '2021-12-22', '2021-12-28', 1);
INSERT INTO reservation VALUES(3, 'test_user', 3, '2020-12-12', '2020-12-28', 2);
INSERT INTO reservation VALUES(4, 'test_user', 3, '2021-12-12', '2021-12-28', 2);
