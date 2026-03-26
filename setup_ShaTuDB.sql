-- SHATU: SHA-256 Tutor
-- (C) Johanna & Richard Blumenthal, All rights reserved
-- Unauthorized use, duplication or distribution without the authors' permission is strictly prohibited.
-- Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" basis without warranties or conditions of any kind, either expressed or implied.
-- Authors: chand, rickb
-- Created: Nov 8, 2024
-- Last modified: Dec 2, 2025 for SHAT-293

-- If the ShaTuDB exists, drop it. In general, you will lose any existing data.
DROP DATABASE IF EXISTS ShaTuDB;

-- Create a database in MySql named: ShaTuDB
CREATE DATABASE ShaTuDB;

-- Create user representing the ShaTu tutor.
CREATE USER IF NOT EXISTS 'ShaTuTs'@'localhost' IDENTIFIED BY 'ShaTu2023';

-- Give the ShaTu tutor the following priveledges.
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP ON ShaTuDB.* TO 'ShaTuTs'@'localhost';

/* ********** ADD TABLES TO NEW DATABASE ********** */
USE ShaTuDB;

CREATE TABLE Account (
    UserId VARCHAR(256) PRIMARY KEY,
    Password VARCHAR(256) NOT NULL,
    FirstName VARCHAR(256),
    LastName VARCHAR(256),
    Question INT,
    Answer VARCHAR(256),
    IsStudent TINYINT DEFAULT 0
);

CREATE TABLE Assessment (
    AssessmentId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UserId VARCHAR(256) NOT NULL,
    KnowledgeComponentId INT NOT NULL,
    AssessmentLevel ENUM ('VERY_LOW', 'LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH',
                          'IN_PROGRESS', 'COMPLETED', 'NOT_STARTED'),
    Exposures INT,
    Successes INT,
    Hints INT,
    CorrectAnswersRequested INT DEFAULT 0
);

-- Truncate Table Assessment;
-- Will delete data, but also reset the next id counter to zero

CREATE TABLE Course (
    Id INT NOT NULL DEFAULT 0 PRIMARY KEY,
    Title VARCHAR(255) DEFAULT NULL,
    PrimaryPedagogy ENUM ('STUDENT_CHOICE', 'FIXED_SEQUENCE', 'MASTERY_LEARNING',
                   'MICROADAPTATION', 'OTHER'),
    Description VARCHAR(255) DEFAULT NULL
);

CREATE TABLE ExercisingLocation (
    Id INT NOT NULL PRIMARY KEY,
    CourseId INT,
    UnitId INT,
    TaskId INT,
    StepId INT
);

CREATE TABLE Hint (
    Id INT NOT NULL DEFAULT 0 PRIMARY KEY,
    StepId INT NOT NULL,
    Text VARCHAR(256) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL
);

CREATE TABLE InfoMsgStep (
    SubStepId INT NOT NULL PRIMARY KEY,
    Text VARCHAR(4096)
);

CREATE TABLE KnowledgeComponent (
    Id INT NOT NULL PRIMARY KEY,
    CourseId INT NOT NULL,
    Title VARCHAR(256) NOT NULL,
    Description VARCHAR(255),
    BloomLevel ENUM ('KNOWLEDGE', 'COMPREHENSION', 'APPLICATION', 'ANALYSIS',
                     'SYNTHESIS', 'EVALUATION'),
    IsDomainFocus TINYINT,
    Pedagogy ENUM ('STUDENT_CHOICE', 'FIXED_SEQUENCE', 'MASTERY_LEARNING',
                   'MICROADAPTATION', 'OTHER'),
    ExercisingLocations VARCHAR(256),
    Granularity VARCHAR(256)
);

CREATE TABLE PendingStep (
    Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SessionId INT NOT NULL,
    StepId INT NOT NULL,
    NotifyTutor TINYINT DEFAULT 0,
    IsCompleted TINYINT DEFAULT 0,
    CurrentHintIndex INT NOT NULL
);

CREATE TABLE PendingTask (
    SessionId INT NOT NULL PRIMARY KEY,
    TaskId INT NOT NULL,
    PendingStepId INT NOT NULL
);

CREATE TABLE Problem (
  Id INT NOT NULL DEFAULT -1,
  Title varchar(256) NOT NULL,
  Description varchar(256) NOT NULL,
  UnitId INT NOT NULL,
  SequenceIndex INT NOT NULL,
  Message VARCHAR(512) NOT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE Step (
    Id INT NOT NULL PRIMARY KEY,
    TaskId INT NOT NULL,
    Title VARCHAR(256) DEFAULT NULL,
    Description VARCHAR(256) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL,
    ExercisedComponentId INT,
    StepSubType ENUM(
      'INFO_MESSAGE', 'REQUEST_HINT', 'GUI_ACTION', 'ENCODE_BINARY', 'ENCODE_HEX',
      'ENCODE_ASCII', 'ADD_ONE_BIT', 'PAD_ZEROS', 'ADD_MSG_LENGTH', 'PREPARE_SCHEDULE',
      'INITIALIZE_VARS', 'COMPRESS_ROUND', 'ROTATE_BITS', 'SHIFT_BITS', 'XOR_BITS',
      'ADD_BITS', 'MAJORITY_FUNCTION', 'CHOICE_FUNCTION', 'SHA_ZERO', 'SHA_ONE',    
      'STEP_COMPLETION_REPLY'),
    SubTypeId INT,
    TimeoutId INT
);

CREATE TABLE Student (
    UserId VARCHAR(255),
    FirstName VARCHAR(30) NOT NULL,
    LastName VARCHAR(255) NOT NULL,
    LastLogin TIMESTAMP,
    LastLogout TIMESTAMP
);

CREATE TABLE LoginHistory (
    Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UserId VARCHAR(255) NOT NULL,
    LogoutTime TIMESTAMP NULL,
    LoginTime TIMESTAMP NOT NULL,
    INDEX idx_login_userid (UserId),
    INDEX idx_login_current (LoginTime)
);

CREATE TABLE StudentModel (
    UserId VARCHAR(255) NOT NULL PRIMARY KEY,
    ScaffoldLevel ENUM ('NONE', 'LOW', 'MEDIUM', 'HIGH', 'EXTREME')
);

-- A static description of a task to complete within a course unit.
-- Should ExampleType be ProblemType instead?
CREATE TABLE Task (
    Id INT NOT NULL DEFAULT 0 PRIMARY KEY,
    ProblemId INT NOT NULL DEFAULT 1,
    Title VARCHAR(256) NOT NULL,
    Description VARCHAR(256) NOT NULL,
    Kind ENUM('PROBLEM', 'MESSAGE') NOT NULL,
    SequenceIndex INT,
    ExampleType ENUM(
        'ASCII_ENCODE', 'ADD_ONE_BIT', 'PAD_ZEROS', 'ADD_MSG_LENGTH', 'PREPARE_SCHEDULE', 'INITIALIZE_VARS',
        'COMPRESS_ROUND', 'ROTATE_BITS', 'SHIFT_BITS', 'XOR_BITS', 'ADD_BITS', 'MAJORITY_FUNCTION', 'CHOICE_FUNCTION',
        'SHA_ZERO', 'SHA_ONE', 'STEP_COMPLETION_REPLY', 'REQUEST_HINT', 'ALL')
);

CREATE TABLE Timeout (
    Id INT NOT NULL PRIMARY KEY,
    TimeoutType VARCHAR(256),
    Seconds INT,
    Event VARCHAR(256),
    Msg VARCHAR(4096)
);

CREATE TABLE TutoringSession (
    Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UserId VARCHAR(256) NOT NULL,
    SecurityToken VARCHAR(256) NOT NULL,
    IsActive TINYINT DEFAULT 0,
    StartDate TIMESTAMP NOT NULL,
    CourseId INT NOT NULL,
    UnitId INT NOT NULL,
    ProblemId INT
);

CREATE TABLE Unit (
    Id INT NOT NULL DEFAULT 0 PRIMARY KEY,
    CourseId INT DEFAULT NULL,
    Title VARCHAR(255) DEFAULT NULL,
    Description VARCHAR(255) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL,
    Pedagogy ENUM ('STUDENT_CHOICE', 'FIXED_SEQUENCE', 'MASTERY_LEARNING',
                   'MICROADAPTATION', 'OTHER')
);

/* ********** POPULATE TABLES ********** */

INSERT INTO Course (Id, Title, PrimaryPedagogy, Description)
 VALUES (1, 'SHA-256 Message Digest', 'FIXED_SEQUENCE',
    'Familiarizes students with the operation of the SHA-256 message digest algorithm along with its underlying bitwise operations and encodings.');

INSERT INTO ExercisingLocation (Id, CourseId, UnitId, TaskId, StepId)
  VALUES (0,1,0,0,0);

INSERT INTO Hint (Id, StepId, Text, SequenceIndex)
 VALUES (0, 0, 'Acknowledge this message by pressing the Acknowledged button.', 0);

INSERT INTO InfoMsgStep (SubStepId, Text)
 VALUES (
    0, CONCAT('Welcome, I''m ShaTu. I''ll begin by showing you how this application works by demonstrating each step used to create a ',
        'SHA-256 message digest including the bitwise operations associated with these steps.\n\nWhen I send you an information message, ',
        'like this one, all you have to do is acknowledge it by pressing the ''Acknowledged'' button.')
);

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (0, 1, 'Information Message Acknowledgement', 'Student has appropriately demonstrated acknowleding information messages presented by the tutor.',
    'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (10, 1, 'Step Completion Reply', 'Student has appropriately demonstrated step completion.',
    'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (20, 1, 'Hint Request', 'Student has appropriately requested a step hint.', 
    'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (
    100, 1, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.',
    'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, BloomLevel, 
    IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (101, 1, 'Encode Hex', 'Convert to Hex equivalent', 
  'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (102, 1, 'Binary Hex', 'Convert to Binary equivalent', 
   'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (110, 1, 'Add One Bit', 'Add a single bit to the end of a bit string.', 
   'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (120, 1, 'Pad with Zeroes', 'Pad a bit string to the appropriate length with zeroes', 
   'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (130, 1, 'Add a msg length to the bit string', 'Add the appropriate message length to the bit string',
    'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
     BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (140, 1, 'Prepare Schedue', 'Prepare the schedule', 
  'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
     BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (150, 1, 'Initialize Variables', 'Initialize the compression round variables.', 
  'APPLICATION', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
     BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
  VALUES (160, 1, 'Compression Round', 'Sequence the Compression Round.', 
     'APPLICATION', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (170, 1, 'Rotate BIts', 'Rotate a bit string.', 
     'APPLICATION', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (180, 1, 'Shift Bits', 'Shift a bit string.', 
   'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (190, 1, 'XOR Bits', 'XOR a bit string.', 'APPLICATION', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (200, 1, 'Majority Function', 'Use the majority function on appropriate bit strings.', 'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (210, 1, 'Choice Function', 'Demonstrate the choice function on appropriate bit strings.', 'APPLICATION', 0, 'Other', '0', 'Knowledge Component'); 

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (220, 1, 'Add bit strings', 'Demonstrate the ability to add two bit strings together.', 'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (230, 1, 'SHA Sum 0 Function', 'Use the Sigma0 function.', 'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (Id, CourseId, Title, Description, 
   BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
 VALUES (240, 1, 'SHA Sum 1 Function', 'Use the Sigma1 function.', 
  'APPLICATION', 0, 'Other', '0', 'Knowledge Component');

-- This problem will be referenced by the Encode as ASCII task
INSERT INTO Problem (Id, Title, Description, UnitId, SequenceIndex, Message)
 VALUES (1, 'Introductory Problem', 'Introduces the student to the tutor.', 0, 0, 
         'Regis Computer Science Rocks!');

INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
 VALUES (0, 0, 'Acknowledge Welcome', 'First Step in learning the tutor', 0, 0, 'INFO_MESSAGE', 0, 0);


INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (0, 1, 'Welcome', 'Let''s get started', 'MESSAGE', 0, 'ALL');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (10, 1, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.', 'PROBLEM', 1, 'ASCII_ENCODE');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (20, 1, 'Add One Bit', 'Add a single bit to the end of a bit string.', 'PROBLEM', 2, 'ADD_ONE_BIT');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (30, 1, 'Pad with Zeroes', 'Pad a bit string to the appropriate length with zeroes', 'PROBLEM', 3, 'PAD_ZEROS');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (40, 1, 'Add a msg length to the bit string', 'Add the appropriate message length to the bit string', 'PROBLEM', 4, 'ADD_MSG_LENGTH');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (50, 1, 'Prepare Schedule', 'Prepare the schedule', 'PROBLEM', 5, 'PREPARE_SCHEDULE');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (60, 1, 'Initialize Variables', 'Initialize the compression round variables.', 'PROBLEM', 6, 'INITIALIZE_VARS');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (70, 1, 'Compression Round', 'Sequence the Compression Round.', 'PROBLEM', 7, 'COMPRESS_ROUND');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (80, 1, 'Rotate Bits', 'Rotate a bit string.', 'PROBLEM', 8, 'ROTATE_BITS');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (90, 1, 'Shift Bits', 'Shift a bit string.', 'PROBLEM', 9, 'SHIFT_BITS');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (100, 1, 'XOR Bits', 'XOR a bit string.', 'PROBLEM', 10, 'XOR_BITS');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (110, 1, 'Choice Function', 'Demonstrate the choice function on appropriate bit strings.', 'PROBLEM', 11, 'CHOICE_FUNCTION');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (120, 1, 'Majority Function', 'Use the majority function on appropriate bit strings.', 'PROBLEM', 12, 'MAJORITY_FUNCTION');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (130, 1, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.', 'PROBLEM', 13, 'ASCII_ENCODE');

INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (140, 1, 'SHA Sum 0 Function', 'Use the Sigma0 function.', 'PROBLEM', 14, 'SHA_ZERO');

-- NEW: SHA ONE Task (TaskId 230) for the Sigma1 function
INSERT INTO Task (Id, ProblemId, Title, Description, Kind, SequenceIndex, ExampleType)
 VALUES (230, 1, 'SHA Sum 1 Function', 'Use the Sigma1 function.', 'PROBLEM', 15, 'SHA_ONE');


INSERT INTO Timeout (Id, TimeoutType, Seconds, Event, Msg)
 VALUES (0, 'Info Message', 60, 'Reminder', 'Please acknowledge the current information message to continue.');


INSERT INTO Unit (Id, CourseId, Title,
    Description,
    SequenceIndex, Pedagogy)
 VALUES (0, 1, 'SHA-256: See One',
    CONCAT('In this unit, the student will see an example of how each primary step of the SHA-256 algorithm is performed. ',
        'It also exposes the student to the general operation of the ShaTu application.'),
    0, 'FIXED_SEQUENCE');

/* ********** ADD FOREIGN KEY CONSTRAINTS ********** */

ALTER Table Assessment
ADD CONSTRAINT fk_assessment_userid
FOREIGN KEY (UserId) REFERENCES Account(UserId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Assessment
ADD CONSTRAINT fk_assessment_knowledgecomponentid
FOREIGN KEY (KnowledgeComponentId) REFERENCES KnowledgeComponent(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_courseid
FOREIGN KEY (CourseId) REFERENCES Course(Id)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_stepid
FOREIGN KEY (StepId) REFERENCES Step(Id)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_taskid
FOREIGN KEY (TaskId) REFERENCES Task(Id)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(Id)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table Hint
ADD CONSTRAINT fk_hint_stepid
FOREIGN KEY (StepId) REFERENCES Step(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table KnowledgeComponent
ADD CONSTRAINT fk_knowledgecomponent_courseid
FOREIGN KEY (CourseId) REFERENCES Course(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingStep
ADD CONSTRAINT fk_pendingstep_sessionid
FOREIGN KEY (SessionId) REFERENCES TutoringSession(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingStep
ADD CONSTRAINT fk_pendingstep_stepid
FOREIGN KEY (StepId) REFERENCES Step(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingTask
ADD CONSTRAINT fk_pendingtask_pendingstepid
FOREIGN KEY (PendingStepId) REFERENCES PendingStep(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingTask
ADD CONSTRAINT fk_pendingtask_taskid
FOREIGN KEY (TaskId) REFERENCES Task(id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_taskid
FOREIGN KEY (TaskId) REFERENCES Task(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_timeoutid
FOREIGN KEY (TimeoutId) REFERENCES Timeout(Id)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table Task
ADD CONSTRAINT fk_task_problemid
FOREIGN KEY (ProblemId) REFERENCES Problem(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_courseid
FOREIGN KEY (CourseId) REFERENCES Course(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_problemid
FOREIGN KEY (ProblemId) REFERENCES Problem(Id)
ON UPDATE CASCADE ON DELETE SET NULL;    -- Deleting a problem should not delete a session

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_userid
FOREIGN KEY (UserId) REFERENCES Account(UserId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Unit
ADD CONSTRAINT fk_unit_courseid
FOREIGN KEY (CourseId) REFERENCES Course(Id)
ON UPDATE CASCADE ON DELETE CASCADE;

