-- ----------------------------------------------------------------------------
-- Movies Model
-------------------------------------------------------------------------------

-- -----------------------------------------------------------------------------
-- Drop tables. NOTE: before dropping a table (when re-executing the script),
-- the tables having columns acting as foreign keys of the table to be dropped,
-- must be dropped first (otherwise, the corresponding checks on those tables
-- could not be done).

DROP TABLE Sale;
DROP TABLE Movie;

-- --------------------------------- Movie ------------------------------------
CREATE TABLE Movie ( movieId BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) COLLATE latin1_bin NOT NULL,
    runtime SMALLINT NOT NULL,
    description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    price FLOAT NOT NULL,
    creationDate DATETIME NOT NULL,
    CONSTRAINT MoviePK PRIMARY KEY(movieId), 
    CONSTRAINT validRuntime CHECK ( runtime >= 0 AND runtime <= 1000 ),
    CONSTRAINT validPrice CHECK ( price >= 0 AND price <= 1000) ) ENGINE = InnoDB;

-- --------------------------------- Sale ------------------------------------

CREATE TABLE Sale ( saleId BIGINT NOT NULL AUTO_INCREMENT,
    movieId BIGINT NOT NULL,
    userId VARCHAR(40) COLLATE latin1_bin NOT NULL,
    expirationDate DATETIME NOT NULL,
    creditCardNumber VARCHAR(16),
    price FLOAT NOT NULL,
    movieUrl VARCHAR(255) NOT NULL,
    saleDate DATETIME NOT NULL,
    CONSTRAINT SalePK PRIMARY KEY(saleId),
    CONSTRAINT SaleMovieIdFK FOREIGN KEY(movieId)
        REFERENCES Movie(movieId) ON DELETE CASCADE ) ENGINE = InnoDB;
