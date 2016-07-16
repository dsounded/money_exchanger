# Countries schema

# --- !Ups

CREATE TABLE `Countries` (
  `id` INT(11) NOT NULL,
  `title` VARCHAR(225) NOT NULL,
  `abbreviation` VARCHAR(3) NOT NULL,
  `isActive` boolean NOT NULL,
  `createdAt` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `Countries`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `Countries` ADD UNIQUE(`id`);
ALTER TABLE `Countries` ADD UNIQUE(`title`);
ALTER TABLE `Countries` ADD UNIQUE(`abbreviation`);

ALTER TABLE `Countries`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

INSERT into `Countries` (title, abbreviation, isActive, createdAt) VALUES("Ukraine", "UA", true, CURRENT_TIMESTAMP)

# --- !Downs

DROP TABLE `Countries`;