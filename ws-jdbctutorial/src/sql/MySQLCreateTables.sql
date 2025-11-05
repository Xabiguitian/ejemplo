-- ----------------------------------------------------------------------------
-- JDBC Tutorial.
-------------------------------------------------------------------------------

DROP TABLE TutMovie;
CREATE TABLE TutMovie ( movieId VARCHAR(40) COLLATE latin1_bin NOT NULL,
    title VARCHAR(255) COLLATE latin1_bin NOT NULL,
    runtime SMALLINT NOT NULL,
    CONSTRAINT TutMoviePK PRIMARY KEY(movieId)) ENGINE = InnoDB;
