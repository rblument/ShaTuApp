# 
# SHATU: SHA-256 Tutor
# 
# (C) Johanna & Richard Blumenthal, All rights reserved
#
# Unauthorized use, duplication or distribution without the authors'
#  permission is strictly prohibited.
# 
#  Unless required by applicable law or agreed to in writing, this
#  software is distributed on an "AS IS" basis without warranties
# or conditions of any kind, either expressed or implied.
#


# Authors:  chand, rickb
# Created: Nov 8, 2024
#

# If the ShaTuDB exists, drop it. In general, you will lose any existing data.
DROP DATABASE IF EXISTS ShaTuDB;

#
# Create a database in MySql named: DiceTsDB
CREATE DATABASE ShaTuDB;

# Create user representing the DICE tutor.
CREATE USER 'ShaTuTs'@'localhost' IDENTIFIED BY 'ShaTu2023';

# Give the ShaTu tutor the following priveledges.
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP ON ShaTuDB.* TO 'ShaTuTs'@'localhost';

# Add tables to new database
USE ShaTuDB;

CREATE TABLE Account (
   UserId VARCHAR(256),
   Password VARCHAR(256) NOT NULL,
   FirstName VARCHAR(256),
   LastName VARCHAR(256),
   Question int,
   Answer VARCHAR(256),
   IsStudent tinyint DEFAULT 0,
   PRIMARY KEY (UserId)
);

CREATE TABLE TutoringSession (
    Id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UserId varchar(256) NOT NULL,
    SecurityToken varchar(256) NOT NULL,
    IsActive tinyint DEFAULT 0,
    StartDate TIMESTAMP NOT NULL,
    CourseId int NOT NULL,
    UnitId int NOT NULL
);

CREATE TABLE PendingTask (
  SessionId int NOT NULL,
  TaskId int NOT NULL,
  PendingStepId int NOT NULL,
  PRIMARY KEY (SessionId)
);

CREATE TABLE PendingStep (
  Id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  SessionId int NOT NULL,
  StepId int NOT NULL,
  NotifyTutor tinyint DEFAULT 0,
  IsCompleted tinyint DEFAULT 0,
  CurrentHintIndex int NOT NULL
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

CREATE TABLE Course (
  Id int NOT NULL DEFAULT '0',
  Title varchar(255) DEFAULT NULL,
  PrimaryPedagogy varchar(255) DEFAULT NULL,
  Description varchar(255) DEFAULT NULL,
  PRIMARY KEY (Id));

CREATE TABLE Unit (
  UnitId int NOT NULL DEFAULT '0',
  CourseId int DEFAULT NULL,
  Title varchar(255) DEFAULT NULL,
  Description varchar(255) DEFAULT NULL,
  SequenceIndex int DEFAULT NULL,
  Pedagogy varchar(255) DEFAULT NULL,
  PRIMARY KEY (UnitId));

# A static description of a task to complete within a course unit.
CREATE TABLE Task (
  TaskId int NOT NULL DEFAULT 0,
  CourseId int NOT NULL DEFAULT 1,
  UnitId int DEFAULT NULL,
  Title varchar(256) NOT NULL,
  Description varchar(256) NOT NULL,
  Kind varChar(256) NOT NULL,
  SequenceIndex int,
  ExampleType varchar(256) DEFAULT NULL,
  ProblemId int,
  PRIMARY KEY (TaskId));

CREATE TABLE Step (
  Id int NOT NULL,
  CourseId int NOT NULL,
  UnitId int NOT NULL,
  TaskId int NOT NULL,
  Title varchar(256) DEFAULT NULL,
  Description varchar(256) DEFAULT NULL,
  SequenceIndex int DEFAULT NULL,
  ExercisedComponentId int,
  StepSubType varchar(256) DEFAULT NULL,
  SubTypeId int,
  TimeoutId int,
  PRIMARY KEY (Id));

CREATE TABLE InfoMsgStep (
  SubStepId int NOT NULL,
  Text varchar(4096),
  PRIMARY KEY(SubStepId));

CREATE TABLE Timeout (
  id int NOT NULL,
  TimeoutType varchar(256),
  Seconds int,
  Event varchar(256),
  Msg varchar(4096),
  PRIMARY KEY(id));

CREATE TABLE KnowledgeComponent (
  Id int NOT NULL ,
  CourseId int NOT NULL,
  Title varchar(256) NOT NULL,
  Description varchar(255),
  BloomLevel varchar(256) NOT NULL,
  IsDomainFocus tinyint,
  Pedagogy varchar(256),
  ExercisingLocations varchar(256),
  Granularity varchar(256),
  PRIMARY KEY (Id));

CREATE TABLE ExercisingLocation (
  Id int NOT NULL,
  CourseId int,
  UnitId int,
  TaskId int,
  StepId int,
  PRIMARY KEY (Id));

CREATE TABLE Hint (
  Id int NOT NULL DEFAULT '0',
  StepId int NOT NULL,
  Text varchar(256) DEFAULT NULL,
  SequenceIndex int DEFAULT NULL,
  PRIMARY KEY (Id));

CREATE TABLE Assessment (
   Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   UserId VARCHAR(256) NOT NULL,
   KnowledgeComponentId INT NOT NULL,
   AssessmentLevel VARCHAR(32) NOT NULL,
   Exposures INT,
   Successes INT,
   Hints INT
);

# Truncate Table Assessment;
# Will delete data, but also reset the next id counter to zero

# Populate tables

INSERT INTO User (Email, Password) 
   VALUES ('test@regis.edu', sha2('TestP@ss', 256));

INSERT INTO Student (UserId, FirstName, LastName)
   VALUES ('test@regis.edu', 'Testy', 'McTest');

INSERT INTO StudentModel (UserId, ScaffoldLevel)
   VALUES ('test@regis.edu', 'Extreme');

INSERT INTO Course
(Id,
 Title,
 PrimaryPedagogy,
 Description)
VALUES
(1,'SHA-256 Message Digest', 'Fixed Sequence', 
  'Familiarizes students with the operation of the SHA-256 message digest
        algorithm along with its underlying bitwise operations and encodings.');

INSERT INTO Unit
(UnitId,
CourseId,
Title,
Description,
SequenceIndex,
Pedagogy)
VALUES
(0, 1, 'SHA-256: See One', 
  'In this unit, the student will see an example of how each primary
            step of the SHA-256 algorithm is performed. It also exposes the
            student to the general operation of the ShaTu application.', 
    0, 'Fixed Sequence');


INSERT INTO Task
(TaskId,
CourseId,
UnitId,
Title,
Description,
Kind,
SequenceIndex,
ExampleType,
ProblemId)
VALUES
(0, 1, 0, 'Welcome', 'Let''s get started', 'Usage', 0, 'N/A', -1);


INSERT INTO Step
(Id,
 CourseId,
 UnitId,
 TaskId,
 Title,
 Description,
 SequenceIndex,
 ExercisedComponentId,
 StepSubType,
 SubTypeId,  
 TimeoutId)
VALUES
(0, 1, 0, 0, 'Acknowledge Welcome', 
 'First Step in learning the tutor', 0, 0, 'Information Message', 0, 0);

INSERT INTO InfoMsgStep (SubStepId, Text) VALUES
 (0, 'Welcome, I''m ShaTu. I''ll begin by showing you how this
     application works by demonstrating each step used to create
     an SHA-256 message digest including the bitwise operations
     associated with these steps.\n\n
     When I send you an information message, like this one, all you
     have to do is acknowledge it by pressing the ''Acknowledged'' button.');

INSERT INTO Timeout (id, TimeoutType, Seconds, Event, Msg) VALUES
 (0, 'Info Message', 60, 'Reminder', 'Please acknowledge the current information message to continue.');

INSERT INTO Hint
(Id,
 StepId,
 Text,
 SequenceIndex)
VALUES
(0, 0, 'Acknowledge this message by pressing the ''Acknowledged'' button.', 0);

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(0, 1, 'Information Message Acknowledgement', 
 'Student has appropriately demonstrated acknowleding information messages presented by the tutor.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(10, 1, 'Step Completion Reply', 
 'Student has appropriately demonstrated step completion.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(20, 1, 'Hint Request', 
 'Student has appropriately requested a step hint.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(100, 1, 'Encode ASCII', 
 'Convert an English Text String into its ASCII equivalent.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(101, 1, 'Encode Hex', 
 'Convert to Hex equivalent',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(102, 1, 'Binary Hex', 
 'Convert to Binary equivalent',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(110, 1, 'Add One Bit', 
 'Add a single bit to the end of a bit string.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(120, 1, 'Pad with Zeroes', 
 'Pad a bit string to the appropriate length with zeroes',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(130, 1, 'Add a msg length to the bit string', 
 'Add the appropriate message length to the bit string',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(140, 1, 'Prepare Schedue', 
 'Prepare the schedule',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(150, 1, 'Initialize Variables', 
 'Initialize the compression round variables.',
 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(160, 1, 'Compression Round', 
 'Sequence the Compression Round.',
 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(170, 1, 'Rotate BIts', 
 'Rotate a bit string.',
 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(180, 1, 'Shift Bits', 
 'Shift a bit string.',
 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(190, 1, 'XOR Bits', 
 'XOR a bit string.',
 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(200, 1, 'Majority Function', 
 'Use the majority function on appropriate bit strings.',
 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(210, 1, 'Choice Function', 
 'Demonstrate the choice function on appropriate bit strings.',
 'Application', 0, 'Other', '0', 'Knowledge Component'); 

INSERT INTO KnowledgeComponent
(Id, CourseId, Title,
 Description,
 BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES
(220, 1, 'Add bit strings', 
 'Demonstrate the ability to add two bit strings together.',
 'Application', 0, 'Other', '0', 'Knowledge Component'); 


INSERT INTO ExercisingLocation
(Id, CourseId, UnitId, TaskId, StepId)
VALUES (0,1,0,0,0);
