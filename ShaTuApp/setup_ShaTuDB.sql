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

INSERT INTO Assessment (Id, UserId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints)
   VALUES (1, 'test@regis.edu', 1, 'Not Started', 0, 0, 0);

INSERT INTO Assessment (Id, UserId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints)
   VALUES (100, 'test@regis.edu', 100, 'Not Started', 0, 0, 0);

INSERT INTO Assessment (Id, UserId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints)
   VALUES (101, 'test@regis.edu', 101, 'Not Started', 0, 0, 0);