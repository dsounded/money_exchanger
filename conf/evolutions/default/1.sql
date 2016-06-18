# Countries schema

# --- !Ups

CREATE TABLE Countries (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title TEXT NOT NULL,
    abbreviation VARCHAR(3),
    isActive boolean NOT NULL,
    createdAt datetime NOT NULL,
    PRIMARY KEY (id)
);

INSERT into `Countries` (title, abbreviation, isActive, createdAt) VALUES("Ukraine", "UA", true, CURRENT_TIMESTAMP)

# --- !Downs

DROP TABLE Countries;