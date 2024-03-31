create TABLE IF NOT EXISTS users
(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birthday DATE
);

create TABLE IF NOT EXISTS ratings
(
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

create TABLE IF NOT EXISTS genres
(
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

create TABLE IF NOT EXISTS films
(
    film_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    release_date DATE,
    duration INT NOT NULL,
    rating_id INT REFERENCES ratings (rating_id)
);

create TABLE IF NOT EXISTS film_genre
(
    film_id INT REFERENCES films (film_id),
    genre_id INT REFERENCES genres (genre_id)
);

create TABLE IF NOT EXISTS friendship
(
    user_id INT REFERENCES users (user_id),
    friend_id INT REFERENCES users (user_id),
    status BOOLEAN
);

create TABLE IF NOT EXISTS film_likes
(
    user_id INT REFERENCES users (user_id),
    film_id INT REFERENCES films (film_id)
);