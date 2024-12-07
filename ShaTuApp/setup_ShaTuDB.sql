/* 
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
/**
 * Author:  chand
 * Created: Nov 8, 2024
 */

# Drop and create new database
DROP DATABASE IF EXISTS ShaTuDB;
CREATE DATABASE ShaTuDB;

# Add tables to new database
USE ShaTuDB;

CREATE TABLE User (
   Email VARCHAR(255),
   Password VARCHAR(255) NOT NULL,
   Question int(10),
   Answer VARCHAR(255),
   PRIMARY KEY (Email)
);

CREATE TABLE Student (
   UserId VARCHAR(255),
   FirstName VARCHAR(30) NOT NULL,
   LastName VARCHAR(255) NOT NULL
);

CREATE TABLE StudentModel (
   UserId VARCHAR(255) NOT NULL PRIMARY KEY,
   ScaffoldLevel VARCHAR(16) NOT NULL
);

CREATE TABLE Assessment (
   Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   UserId VARCHAR(255) NOT NULL,
   KnowledgeComponentId INT NOT NULL,
   AssessmentLevel VARCHAR(32) NOT NULL,
   Exposures INT,
   Successes INT,
   Hints INT
);

# Populate tables

INSERT INTO User (Email, Password) 
   VALUES ('test@regis.edu', sha2('TestP@ss', 256));

INSERT INTO Student (UserId, FirstName, LastName)
   VALUES ('test@regis.edu', 'Testy', 'McTest');

INSERT INTO StudentModel (UserId, ScaffoldLevel)
   VALUES ('test@regis.edu', 'Extreme');

INSERT INTO Assessment (UserId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints)
   VALUES 
   ('test@regis.edu', 1, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 100, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 101, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 102, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 103, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 104, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 105, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 106, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 107, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 108, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 109, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 110, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 111, 'Not Started', 0, 0, 0),
   ('test@regis.edu', 112, 'Not Started', 0, 0, 0);
