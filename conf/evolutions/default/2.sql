# Users schema

# --- !Ups

CREATE TABLE `Users` (
  `id` INT(11) NOT NULL,
  `email` VARCHAR(225) NOT NULL,
  `firstName` VARCHAR(225) NOT NULL,
  `lastName` VARCHAR(225) NOT NULL,
  `password` VARCHAR(225) NOT NULL,
  `role` ENUM('User', 'Moderator', 'Admin') NOT NULL,
  `cityId` INT(11) NOT NULL,
  `phone` VARCHAR(225) NOT NULL,
  `createdAt` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `Users`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `Users` ADD UNIQUE(`id`);
ALTER TABLE `Users` ADD UNIQUE(`email`);

ALTER TABLE `Users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- INSERT into `Countries` (title, abbreviation, isActive, createdAt) VALUES("Ukraine", "UA", true, CURRENT_TIMESTAMP)

# --- !Downs

DROP TABLE `Users`;