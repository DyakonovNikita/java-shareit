DROP TABLE if exists comments;
DROP TABLE if exists bookings;
DROP TABLE if exists items;
DROP TABLE if exists requests;
DROP TABLE if exists users;

CREATE TABLE IF NOT EXISTS users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT null,
    email varchar(512) NOT null,
    UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description varchar(1024) NOT null,
    requestor_id bigint REFERENCES users(user_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null
);

CREATE TABLE IF NOT EXISTS items (
    item_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT null,
    description varchar(1024) NOT null,
    is_available boolean,
    owner_id bigint REFERENCES users(user_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null,
    requests_id bigint REFERENCES requests(request_id) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id bigint REFERENCES items(item_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null,
    booker_id bigint REFERENCES users(user_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null,
    status varchar(8) NOT null
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text varchar(1024) NOT null,
    item_id bigint REFERENCES items(item_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null,
    author_id bigint REFERENCES users(user_id) ON UPDATE RESTRICT ON DELETE CASCADE NOT null,
    created TIMESTAMP NOT NULL
);
