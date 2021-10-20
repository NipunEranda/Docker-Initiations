DROP DATABASE IF EXISTS `delivery_db`;

CREATE DATABASE IF NOT EXISTS `delivery_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `delivery_db`;

CREATE TABLE delivery (
  id varchar(255) NOT NULL,
  orderId varchar(255) NOT NULL,
  customerId bigint(200) NOT NULL,
  deliveryDate varchar(255) DEFAULT NULL,
  deliveryDescription varchar(255) DEFAULT NULL,
  CONSTRAINT delivery_d_pk PRIMARY KEY(id)
);

DROP DATABASE IF EXISTS `order_db`;

CREATE DATABASE IF NOT EXISTS `order_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `order_db`;

CREATE TABLE orderI(
  id varchar(255) NOT NULL,
  customer bigint(200) NOT NULL,
  product bigint(200) NOT NULL,
  paymentId varchar(255) NOT NULL,
  cartId varchar(255) NOT NULL,
  CONSTRAINT orderI_pk PRIMARY KEY(id)
);

CREATE TABLE cart(
  id varchar(255) NOT NULL,
  customer bigint(200) NOT NULL,
  totalPrice double NOT NULL,
  CONSTRAINT cart_pk PRIMARY KEY(id)
);

CREATE TABLE item_cart(
  cart varchar(255) NOT NULL,
  item bigint(200) NOT NULL,
  quantity int(10) NOT NULL,
  CONSTRAINT item_cart_pk PRIMARY KEY(cart, item)
);


DROP DATABASE IF EXISTS `item_db`;

CREATE DATABASE IF NOT EXISTS `item_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;

USE `item_db`;

CREATE TABLE itemType(
  id bigint(200) AUTO_INCREMENT NOT NULL,
  name varchar(255) NOT NULL,
  CONSTRAINT itemType_pk PRIMARY KEY(id)
);

CREATE TABLE item(
  id bigint(200) AUTO_INCREMENT NOT NULL,
  name varchar(255) NOT NULL,
  description text NOT NULL,
  itemType bigint(200) NOT NULL,
  price double NOT NULL,
  CONSTRAINT item_pk PRIMARY KEY(id),
  CONSTRAINT item_fk_itemType FOREIGN KEY(itemType) REFERENCES itemType(id)
);

INSERT INTO itemType(id, name) VALUES
(0, 'Mobile');

INSERT INTO item VALUES
(0, 'Samsung s10', 'Samsung s10', 1, 105950.00);


DROP DATABASE IF EXISTS `user_db`;

CREATE DATABASE IF NOT EXISTS `user_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `user_db`;

CREATE TABLE usr_User (
  id bigint(200) AUTO_INCREMENT NOT NULL,
  fname varchar(255) NOT NULL,
  lname varchar(255) NOT NULL,
  initials varchar(255) DEFAULT NULL,
  dob date DEFAULT NULL,
  phoneNo varchar(10) DEFAULT NULL,
  gender varchar(1) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  isActive BOOLEAN NOT NULL,
  isDelete BOOLEAN NOT NULL,
  CONSTRAINT usr_User_pk PRIMARY KEY(id)
);

CREATE TABLE usr_Login (
  userId bigint(200) DEFAULT NULL,
  email varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  roleId bigint(200) DEFAULT NULL,
  isConfirmed int(1) NOT NULL,
  confirmationCode varchar(8) NOT NULL,
  loginCount bigint(200) NOT NULL,
  CONSTRAINT usr_login_pk PRIMARY KEY(email),
  CONSTRAINT usr_login_fk FOREIGN KEY(userId) REFERENCES usr_User(id)
);

CREATE TABLE usr_Role (
  id bigint(200) AUTO_INCREMENT NOT NULL,
  roleName varchar(255) NOT NULL,
  roleDesc varchar(255) DEFAULT NULL,
  isActive BOOLEAN NOT NULL,
  CONSTRAINT usr_role_pk PRIMARY KEY(id)
);

CREATE TABLE usr_dtl_Customer(
    id bigint(200) NOT NULL,
    CONSTRAINT usr_Client_pk PRIMARY KEY(id),
    CONSTRAINT usr_Client_fk_id FOREIGN KEY(id) REFERENCES usr_User(id)
);

INSERT INTO usr_Role (id, roleName, roleDesc, isActive) VALUES
(0, 'Admin', 'Admin', 1),
(0, 'Manager', 'Manager', 1),
(0, 'Customer', 'Customer', 1);

/*Start Of Procedures*/
    /*Start Of Login Management*/

        /*Login count increment*/
                delimiter //

                    CREATE PROCEDURE login_increaseLoginCount(
                        IN id bigint(200),
                        OUT status int
                        )
                        BEGIN
                            /*Start Error Handling*/
                                DECLARE EXIT HANDLER FOR SQLEXCEPTION
                                BEGIN
                                    ROLLBACK;
                                    SET status = 0;
                                    SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                                END;
                            /*End Error Handling*/

                            START TRANSACTION;

                                UPDATE usr_Login
                                SET usr_Login.loginCount = loginCount + 1
                                WHERE usr_Login.userId = id;
                                SET status = 1;   

                            COMMIT;
                        END//
                delimiter ;

    /*End Of Login Management*/

    /*Start Of User Management*/
        /*Update User Details*/
            delimiter //

                CREATE PROCEDURE user_updateUserDetails(
                    IN fname varchar(255),
                    IN lname varchar(255),
                    IN initials varchar(255),
                    IN dob date,
                    IN phoneNo varchar(10),
                    IN gender varchar(1),
                    IN address varchar(255),
                    IN id bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            UPDATE usr_User
                            SET usr_User.fname = fname,
                            usr_User.lname = lname,
                            usr_User.initials = initials,
                            usr_User.dob = dob,
                            usr_User.phoneNo = phoneNo,
                            usr_User.gender = gender,
                            usr_User.address = address
                            WHERE usr_User.id = id;
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;

        /*Delete User*/
            delimiter //

                CREATE PROCEDURE user_deleteUser(
                    IN id bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            UPDATE usr_User
                            set usr_User.isDelete = 1, usr_User.isActive = 0
                            WHERE usr_User.id = id;
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;

        /*Change Password*/
            delimiter //

            CREATE PROCEDURE user_changePassword(
                IN password varchar(255),
                IN email varchar(255),
                OUT status int
                )
                BEGIN
                    /*Start Error Handling*/
                        DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                        END;
                    /*End Error Handling*/

                    
                    START TRANSACTION;

                        UPDATE usr_Login
                        SET usr_Login.password = password
                        WHERE usr_Login.email = email;
                        SET status = 1;

                    COMMIT;
                
                END//
                delimiter ;

        /*Forgot Password*/
            delimiter //

                CREATE PROCEDURE user_forgotPassword(
                    IN password varchar(255),
                    IN userId bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            set usr_Login.password = password
                            WHERE usr_Login.userId = userId;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

        /*Email Confirmation*/
            delimiter //

                CREATE PROCEDURE user_confirmEmailConfirmation(
                    IN email varchar(255),
                    IN confirmationCode varchar(255),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            SET usr_Login.isConfirmed = 1
                            WHERE usr_Login.email = email and usr_Login.confirmationCode = confirmationCode;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

        /*Request Email Confirmation Code*/
            delimiter //

                CREATE PROCEDURE user_requestEmailConfirmationCode(
                    IN confirmationCode varchar(255),
                    IN email varchar(255),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        
                        START TRANSACTION;

                            UPDATE usr_Login
                            SET usr_Login.confirmationCode = confirmationCode
                            WHERE usr_Login.email = email;
                            SET status = 1;
                    
                        COMMIT;
                    
                    END//
            delimiter ;

    /*End Of User Management*/

    /*Start Of Customer Management*/

      /* Customer Registration */

            delimiter //

            CREATE PROCEDURE customer_Registration(
                /*usr_User*/
                IN fname varchar(255),
                IN lname varchar(255),
                IN initials varchar(255),
                IN dob date,
                IN phoneNo varchar(10),
                IN gender varchar(1),
                IN address varchar(255),
                /*usr_Login*/
                IN email varchar(255),
                IN password varchar(255),
                IN roleId bigint(200),
                IN confirmationCode varchar(8),
                /*usr_dtl_Customer*/
                OUT status int
                )
                BEGIN
                    DECLARE userID bigint(200);
                    /*Start Error Handling*/
                        DECLARE EXIT HANDLER FOR 1062
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT CONCAT('Duplicate key occurred') AS message;
                        END;

                        DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            ROLLBACK;
                            SET status = 0;
                            SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                        END;
                    /*End Error Handling*/

                    
                    START TRANSACTION;

                        INSERT INTO usr_User VALUES(0, fname, lname, initials, dob, phoneNo, gender, address, 1, 0);
                        SELECT MAX(id) INTO userID FROM usr_User;
                        INSERT INTO usr_Login VALUES(userID, email, password, roleId, 1, confirmationCode, 0);
                        INSERT INTO usr_dtl_Customer VALUES(userID);
                        SET status = 1;

                    COMMIT;
                
                END//
            delimiter ;

      /*Update Client details*/
            delimiter //

                CREATE PROCEDURE customer_updateCustomerDetails(
                    IN fname varchar(255),
                    IN lname varchar(255),
                    IN initials varchar(255),
                    IN dob date,
                    IN phoneNo varchar(10),
                    IN gender varchar(1),
                    IN address varchar(255),
                    IN userId bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            UPDATE usr_User uu
                            SET uu.fname = fname,
                            uu.lname = lname,
                            uu.initials = initials,
                            uu.dob = dob,
                            uu.phoneNo = phoneNo,
                            uu.gender = gender,
                            uu.address = address
                            WHERE uu.id = userId;
                            
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;

      /*Delete Client*/
            delimiter //

                CREATE PROCEDURE customer_deleteCustomer(
                    IN id bigint(200),
                    OUT status int
                    )
                    BEGIN
                        /*Start Error Handling*/
                            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                            BEGIN
                                ROLLBACK;
                                SET status = 0;
                                SELECT 'An error has occurred, operation rollbacked and the stored procedure was terminated';
                            END;
                        /*End Error Handling*/

                        START TRANSACTION;

                            DELETE FROM cart WHERE cart.user = id;
                            DELETE FROM orderI WHERE orderI.user = id;
                            DELETE FROM usr_dtl_Customer WHERE usr_dtl_Customer.id = id;
                            DELETE FROM usr_Login WHERE usr_Login.userId = id;
                            DELETE FROM usr_User WHERE usr_User.id = id;
                            SET status = 1;

                        COMMIT;
                    END//
            delimiter ;


    /*End Of Customer Management*/
  /*End Of Procedures*/
