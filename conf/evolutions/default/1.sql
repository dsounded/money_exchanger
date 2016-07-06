# Countries schema

# --- !Ups

CREATE TABLE Countries (
    id bigint(20) AUTO_INCREMENT,
    title VARCHAR(225) NOT NULL,
    abbreviation VARCHAR(3) NOT NULL,
    isActive boolean NOT NULL,
    createdAt datetime NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY (title),
    UNIQUE KEY (abbreviation)
);

INSERT into `Countries` (title, abbreviation, isActive, createdAt) VALUES("Ukraine", "UA", true, CURRENT_TIMESTAMP)

# --- !Downs

DROP TABLE Countries;