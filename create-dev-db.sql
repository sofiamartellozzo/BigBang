SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema ProgTIW
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `ProgTIW` ;
CREATE SCHEMA IF NOT EXISTS `ProgTIW` DEFAULT CHARACTER SET utf8;
USE `ProgTIW` ;

-- -----------------------------------------------------
-- Table `item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `item` ;
CREATE TABLE IF NOT EXISTS `item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `category` LONGTEXT NOT NULL,
  `picture` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (1,'Animal Farm','Book written by George Orwell','Books','link1');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (2,'Best T-Shirt ever','T-Shirt with the logo of this ecommerce, the best!','Clothing','link2');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (3,'Farenheit 451','Another book yo!','Books','link3');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (4,'Keyboard','Useful for typing on your computer','Technology','link4');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (5,'Monitor','Useful for seeing what\'s going on on your computer','Technology','link5');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (6,'Socks','Nice warm socks for summer','Clothing','link6');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (7,'Gelato','Sweet and sugary food to eat when it\'s hot outside','Food','link7');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (8,'The robots of dawn','A classic by Isaac Asimov','Books','link8');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (9,'Pizza','Best food in the world, even better than gelato!','Food','link9');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (42,'The Hitchhiker\'s Guide to the Galaxy','This book has a very long title and it\'s also very good.','Books','link10');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (10,'Murder on the Orient Express','Very thrilling!','Books','link11');
INSERT INTO `item` (`id`,`name`,`description`,`category`,`picture`) VALUES (11,'A cool hat','this is a very cool hat','Clothing','link12');

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user` ;
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(60) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `address` VARCHAR(60) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

INSERT INTO `user` (`id`,`name`,`surname`,`email`,`password`,`address`) VALUES (1,'Sofia','Martellozzo','sofia.martellozzo@mail.polimi.it','prova1','via , Milano');
INSERT INTO `user` (`id`,`name`,`surname`,`email`,`password`,`address`) VALUES (2,'Margherita','Musumeci','margherita.musumeci@mail.polimi.it','prova2','via , Milano');
INSERT INTO `user` (`id`,`name`,`surname`,`email`,`password`,`address`) VALUES (3,'Alberto Maria','Mosconi','alberto.mosconi@mail.polimi.it','prova3','via , Pavia');
INSERT INTO `user` (`id`,`name`,`surname`,`email`,`password`,`address`) VALUES (4,'Pluto','Manzo','a@a.com','a','via , Topolinia');

-- -----------------------------------------------------
-- Table `order_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `order_info` ;
CREATE TABLE IF NOT EXISTS `order_info` (
  `id` CHAR(36) NOT NULL,
  `id_user` INT NOT NULL,
  `id_vendor` INT NOT NULL,
  `date` TIMESTAMP NOT NULL,
  `shipping_cost` FLOAT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `order_info_id_user`
    FOREIGN KEY (`id_user`)
    REFERENCES `user` (`id`)
    ON UPDATE CASCADE,
  CONSTRAINT `order_info_id_vendor`
    FOREIGN KEY (`id_vendor`)
    REFERENCES `vendor` (`id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO `order_info` (`id`,`id_user`,`id_vendor`,`date`,`shipping_cost`) VALUES ('1e6ff61f-e059-410f-8410-3aa3cb116236',4,1,timestamp("2021-01-26","18:32:11"),4.2);
INSERT INTO `order_info` (`id`,`id_user`,`id_vendor`,`date`,`shipping_cost`) VALUES ('85ab8b69-cc90-4852-a29f-0f16e31dc1f4',4,2,timestamp("2021-02-02","13:02:40"),0);
INSERT INTO `order_info` (`id`,`id_user`,`id_vendor`,`date`,`shipping_cost`) VALUES ('7457aa24-387b-456a-9e68-c7a8bfbc32bb',4,3,timestamp("2021-02-18","01:26:51"),24.99);
INSERT INTO `order_info` (`id`,`id_user`,`id_vendor`,`date`,`shipping_cost`) VALUES ('b9e97e78-972c-473c-a679-6f8d057adf61',4,1,timestamp("2021-03-14","15:09:26"),3.10);

-- -----------------------------------------------------
-- Table `ordered_item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ordered_item` ;
CREATE TABLE IF NOT EXISTS `ordered_item` (
  `id_order` CHAR(36) NOT NULL,
  `id_item` INT NOT NULL,
  `quantity` INT NOT NULL,
  `cost` FLOAT NOT NULL,
  PRIMARY KEY (`id_order`,`id_item`),
  CONSTRAINT `ordered_item_id_order`
    FOREIGN KEY (`id_order`)
    REFERENCES `order_info` (`id`)
    ON UPDATE CASCADE,
  CONSTRAINT `ordered_item_id_item`
    FOREIGN KEY (`id_item`)
    REFERENCES `item` (`id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('1e6ff61f-e059-410f-8410-3aa3cb116236',42,1,9.20);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('1e6ff61f-e059-410f-8410-3aa3cb116236',6,2,500.0);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('85ab8b69-cc90-4852-a29f-0f16e31dc1f4',2,1,4.33);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('85ab8b69-cc90-4852-a29f-0f16e31dc1f4',11,1,4.00);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('85ab8b69-cc90-4852-a29f-0f16e31dc1f4',42,1,12.2);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('7457aa24-387b-456a-9e68-c7a8bfbc32bb',3,2,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('7457aa24-387b-456a-9e68-c7a8bfbc32bb',9,3,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('7457aa24-387b-456a-9e68-c7a8bfbc32bb',10,1,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('7457aa24-387b-456a-9e68-c7a8bfbc32bb',1,1,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('b9e97e78-972c-473c-a679-6f8d057adf61',4,2,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('b9e97e78-972c-473c-a679-6f8d057adf61',5,1,1);
INSERT INTO `ordered_item` (`id_order`,`id_item`,`quantity`,`cost`) VALUES ('b9e97e78-972c-473c-a679-6f8d057adf61',42,1,1);

-- -----------------------------------------------------
-- Table `range`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `range` ;
CREATE TABLE IF NOT EXISTS `range` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `min` INT NOT NULL,
  `max` INT NOT NULL,
  `shipping_cost` FLOAT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

INSERT INTO `range` (`id`,`min`,`max`,`shipping_cost`) VALUES (1,0,2,6.1);
INSERT INTO `range` (`id`,`min`,`max`,`shipping_cost`) VALUES (2,3,5,5.2);
INSERT INTO `range` (`id`,`min`,`max`,`shipping_cost`) VALUES (3,6,30,2.3);

-- -----------------------------------------------------
-- Table `vendor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vendor` ;
CREATE TABLE IF NOT EXISTS `vendor` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `score` INT NOT NULL,
  `free_limit` FLOAT NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

INSERT INTO `vendor` (`id`,`name`,`score`,`free_limit`) VALUES (1,'SAMcommerce',5,24.99);
INSERT INTO `vendor` (`id`,`name`,`score`,`free_limit`) VALUES (2,'Mondadori',4,9.99);
INSERT INTO `vendor` (`id`,`name`,`score`,`free_limit`) VALUES (3,'Mercatino',3.5,49.99);

-- -----------------------------------------------------
-- Table `shipping_policy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `shipping_policy` ;
CREATE TABLE IF NOT EXISTS `shipping_policy` (
  `id_range` INT NOT NULL,
  `id_vendor` INT NOT NULL,
  PRIMARY KEY (`id_range`, `id_vendor`),
  CONSTRAINT `shipping_id_range`
    FOREIGN KEY (`id_range`)
    REFERENCES `range` (`id`)
    ON UPDATE CASCADE,
  CONSTRAINT `shipping_id_vendor`
    FOREIGN KEY (`id_vendor`)
    REFERENCES `vendor` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO `shipping_policy` (`id_range`,`id_vendor`) VALUES (1,1);
INSERT INTO `shipping_policy` (`id_range`,`id_vendor`) VALUES (2,1);
INSERT INTO `shipping_policy` (`id_range`,`id_vendor`) VALUES (3,1);
INSERT INTO `shipping_policy` (`id_range`,`id_vendor`) VALUES (1,2);
INSERT INTO `shipping_policy` (`id_range`,`id_vendor`) VALUES (2,2);

-- -----------------------------------------------------
-- Table `view`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `view` ;
CREATE TABLE IF NOT EXISTS `view` (
  `id_user` INT NOT NULL,
  `id_item` INT NOT NULL,
  `date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id_user`, `id_item`,`date`),
  CONSTRAINT `view_id_item`
    FOREIGN KEY (`id_item`)
    REFERENCES `item` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `view_id_user`
    FOREIGN KEY (`id_user`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO `view` (`id_user`,`id_item`,`date`) VALUES (4,3, timestamp("2021-01-26","18:32:11"));
INSERT INTO `view` (`id_user`,`id_item`,`date`) VALUES (4,6, timestamp("2021-01-26","18:42:11"));
INSERT INTO `view` (`id_user`,`id_item`,`date`) VALUES (4,42, timestamp("2021-03-30","19:50:11"));

-- -----------------------------------------------------
-- Table `price`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `price` ;
CREATE TABLE IF NOT EXISTS `price` (
  `id_vendor` INT NOT NULL,
  `id_item` INT NOT NULL,
  `price` FLOAT NOT NULL,
  PRIMARY KEY (`id_vendor`, `id_item`),
  CONSTRAINT `price_id_vendor`
    FOREIGN KEY (`id_vendor`)
    REFERENCES `vendor` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `price_id_item`
    FOREIGN KEY (`id_item`)
    REFERENCES `item` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,1, 20.99);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,2, 5.73);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,3, 2.90);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,4, 29.44);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,5, 62.21);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,6, 500.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,7, 2.50);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,8, 9.50);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,9, 4.5);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,10, 13.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,11, 5.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (1,42, 9.20);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,1, 24.99);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,2, 4.33);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,3, 2.39);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,4, 44.44);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,5, 82.21);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,6, 299.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,7, 1.20);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,8, 7.50);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,9, 4.5);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,10, 7.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,11, 4.00);
INSERT INTO `price` (`id_vendor`,`id_item`,`price`) VALUES (2,42, 12.20);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
