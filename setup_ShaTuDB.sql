
-- SHATU: SHA-256 Tutor
-- (C) Johanna & Richard Blumenthal, All rights reserved
-- Unauthorized use, duplication or distribution without the authors' permission is strictly prohibited.
-- Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" basis without warranties or conditions of any kind, either expressed or implied.
-- Authors: chand, rickb
-- Created: Nov 8, 2024
-- Last modified: October 22, 2025 for SHAT-330

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
    AssessmentLevel VARCHAR(32) NOT NULL,
    Exposures INT,
    Successes INT,
    Hints INT
);

-- Truncate Table Assessment;
-- Will delete data, but also reset the next id counter to zero

CREATE TABLE Course (
    CourseId INT NOT NULL DEFAULT 0 PRIMARY KEY,
    Title VARCHAR(255) DEFAULT NULL,
    PrimaryPedagogy VARCHAR(255) DEFAULT NULL,
    Description VARCHAR(255) DEFAULT NULL
);

CREATE TABLE ExercisingLocation (
    ExercisingLocationId INT NOT NULL PRIMARY KEY,
    CourseId INT,
    UnitId INT,
    TaskId INT,
    StepId INT
);

CREATE TABLE Hint (
    HintId INT NOT NULL DEFAULT 0 PRIMARY KEY,
    StepId INT NOT NULL,
    Text VARCHAR(256) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL
);

CREATE TABLE InfoMsgStep (
    SubStepId INT NOT NULL PRIMARY KEY,
    Text VARCHAR(4096)
);

CREATE TABLE KnowledgeComponent (
    KnowledgeComponentId INT NOT NULL PRIMARY KEY,
    CourseId INT NOT NULL,
    Title VARCHAR(256) NOT NULL,
    Description VARCHAR(255),
    BloomLevel VARCHAR(256) NOT NULL,
    IsDomainFocus TINYINT,
    Pedagogy VARCHAR(256),
    ExercisingLocations VARCHAR(256),
    Granularity VARCHAR(256)
);

CREATE TABLE PendingStep (
    PendingStepId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    ProblemId INT PRIMARY KEY,
    Title VARCHAR(256) NOT NULL,
    Description VARCHAR(256) NOT NULL,
    Message VARCHAR(512) NOT NULL
);

CREATE TABLE Step (
    StepId INT NOT NULL PRIMARY KEY,
    CourseId INT NOT NULL,
    UnitId INT NOT NULL,
    TaskId INT NOT NULL,
    Title VARCHAR(256) DEFAULT NULL,
    Description VARCHAR(256) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL,
    ExercisedComponentId INT,
    StepSubType VARCHAR(256) DEFAULT NULL,
    SubTypeId INT,
    TimeoutId INT
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

-- A static description of a task to complete within a course unit.
-- Should ExampleType be ProblemType instead?
CREATE TABLE Task (
    TaskId INT NOT NULL DEFAULT 0 PRIMARY KEY,
    CourseId INT NOT NULL DEFAULT 1,
    UnitId INT DEFAULT NULL,
    Title VARCHAR(256) NOT NULL,
    Description VARCHAR(256) NOT NULL,
    Kind ENUM('PROBLEM', 'MESSAGE') NOT NULL,
    SequenceIndex INT,
    ExampleType ENUM(
        'ASCII_ENCODE', 'ADD_ONE_BIT', 'PAD_ZEROS', 'ADD_MSG_LENGTH', 'PREPARE_SCHEDULE', 'INITIALIZE_VARS',
        'COMPRESS_ROUND', 'ROTATE_BITS', 'SHIFT_BITS', 'XOR_BITS', 'ADD_BITS', 'MAJORITY_FUNCTION', 'CHOICE_FUNCTION',
        'SHA_ZERO', 'SHA_ONE', 'STEP_COMPLETION_REPLY', 'REQUEST_HINT', 'DEFAULT'),
    ProblemId INT
);

CREATE TABLE Timeout (
    TimeoutId INT NOT NULL PRIMARY KEY,
    TimeoutType VARCHAR(256),
    Seconds INT,
    Event VARCHAR(256),
    Msg VARCHAR(4096)
);

CREATE TABLE TutoringSession (
    TutoringSessionId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UserId VARCHAR(256) NOT NULL,
    SecurityToken VARCHAR(256) NOT NULL,
    IsActive TINYINT DEFAULT 0,
    StartDate TIMESTAMP NOT NULL,
    CourseId INT NOT NULL,
    UnitId INT NOT NULL,
    ProblemId INT
);

CREATE TABLE Unit (
    UnitId INT NOT NULL DEFAULT 0 PRIMARY KEY,
    CourseId INT DEFAULT NULL,
    Title VARCHAR(255) DEFAULT NULL,
    Description VARCHAR(255) DEFAULT NULL,
    SequenceIndex INT DEFAULT NULL,
    Pedagogy VARCHAR(255) DEFAULT NULL
);

/* ********** POPULATE TABLES ********** */

INSERT INTO Course (
    CourseId, Title, PrimaryPedagogy,
    Description)
VALUES (
    1, 'SHA-256 Message Digest', 'Fixed Sequence',
    'Familiarizes students with the operation of the SHA-256 message digest algorithm along with its underlying bitwise operations and encodings.');

INSERT INTO ExercisingLocation (ExercisingLocationId, CourseId, UnitId, TaskId, StepId)
VALUES (0,1,0,0,0);

INSERT INTO Hint (HintId, StepId, Text, SequenceIndex)
VALUES (0, 0, 'Acknowledge this message by pressing the Acknowledged button.', 0);

INSERT INTO InfoMsgStep (
    SubStepId, Text)
VALUES (
    0, CONCAT('Welcome, I''m ShaTu. I''ll begin by showing you how this application works by demonstrating each step used to create a ',
        'SHA-256 message digest including the bitwise operations associated with these steps.\n\nWhen I send you an information message, ',
        'like this one, all you have to do is acknowledge it by pressing the ''Acknowledged'' button.')
);

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    0, 1, 'Information Message Acknowledgement', 'Student has appropriately demonstrated acknowleding information messages presented by the tutor.',
    'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    10, 1, 'Step Completion Reply', 'Student has appropriately demonstrated step completion.',
    'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    20, 1, 'Hint Request', 'Student has appropriately requested a step hint.', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    100, 1, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.',
    'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    101, 1, 'Encode Hex', 'Convert to Hex equivalent', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    102, 1, 'Binary Hex', 'Convert to Binary equivalent', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    110, 1, 'Add One Bit', 'Add a single bit to the end of a bit string.', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    120, 1, 'Pad with Zeroes', 'Pad a bit string to the appropriate length with zeroes', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description,
    BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    130, 1, 'Add a msg length to the bit string', 'Add the appropriate message length to the bit string',
    'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    140, 1, 'Prepare Schedue', 'Prepare the schedule', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    150, 1, 'Initialize Variables', 'Initialize the compression round variables.', 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    160, 1, 'Compression Round', 'Sequence the Compression Round.', 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    170, 1, 'Rotate BIts', 'Rotate a bit string.', 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    180, 1, 'Shift Bits', 'Shift a bit string.', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    190, 1, 'XOR Bits', 'XOR a bit string.', 'Application', 0, 'Other', '0', 'Knowledge Component');
    
INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    200, 1, 'Majority Function', 'Use the majority function on appropriate bit strings.', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    210, 1, 'Choice Function', 'Demonstrate the choice function on appropriate bit strings.', 'Application', 0, 'Other', '0', 'Knowledge Component'); 

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    220, 1, 'Add bit strings', 'Demonstrate the ability to add two bit strings together.', 'Application', 0, 'Other', '0', 'Knowledge Component');

INSERT INTO KnowledgeComponent (
    KnowledgeComponentId, CourseId, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity)
VALUES (
    230, 1, 'SHA Sum 0 Function', 'Use the Sigma0 function.', 'Application', 0, 'Other', '0', 'Knowledge Component');

-- This problem will be referenced by the Encode as ASCII task
INSERT INTO Problem (ProblemId, Title, Description, Message)
VALUES (1, 'Introductory Problem', 'Introduces the student to the tutor.', 'Regis Computer Science Rocks!');

INSERT INTO Step (StepId, CourseId, UnitId, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
VALUES (0, 1, 0, 0, 'Acknowledge Welcome', 'First Step in learning the tutor', 0, 0, 'Information Message', 0, 0);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (0, 1, 0, 'Welcome', 'Let''s get started', 'MESSAGE', 0, 'DEFAULT', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (10, 1, 0, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.', 'PROBLEM', 1, 'ASCII_ENCODE', 1);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (20, 1, 0, 'Add One Bit', 'Add a single bit to the end of a bit string.', 'PROBLEM', 2, 'ADD_ONE_BIT', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (30, 1, 0, 'Pad with Zeroes', 'Pad a bit string to the appropriate length with zeroes', 'PROBLEM', 3, 'PAD_ZEROS', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (40, 1, 0, 'Add a msg length to the bit string', 'Add the appropriate message length to the bit string', 'PROBLEM', 4, 'ADD_MSG_LENGTH', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (50, 1, 0, 'Prepare Schedule', 'Prepare the schedule', 'PROBLEM', 5, 'PREPARE_SCHEDULE', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (60, 1, 0, 'Initialize Variables', 'Initialize the compression round variables.', 'PROBLEM', 6, 'INITIALIZE_VARS', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (70, 1, 0, 'Compression Round', 'Sequence the Compression Round.', 'PROBLEM', 7, 'COMPRESS_ROUND', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (80, 1, 0, 'Rotate Bits', 'Rotate a bit string.', 'PROBLEM', 8, 'ROTATE_BITS', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (90, 1, 0, 'Shift Bits', 'Shift a bit string.', 'PROBLEM', 9, 'SHIFT_BITS', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (100, 1, 0, 'XOR Bits', 'XOR a bit string.', 'PROBLEM', 10, 'XOR_BITS', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (110, 1, 0, 'Choice Function', 'Demonstrate the choice function on appropriate bit strings.', 'PROBLEM', 11, 'CHOICE_FUNCTION', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (120, 1, 0, 'Majority Function', 'Use the majority function on appropriate bit strings.', 'PROBLEM', 12, 'MAJORITY_FUNCTION', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (130, 1, 0, 'Encode ASCII', 'Convert an English Text String into its ASCII equivalent.', 'PROBLEM', 13, 'ASCII_ENCODE', NULL);

INSERT INTO Task (TaskId, CourseId, UnitId, Title, Description, Kind, SequenceIndex, ExampleType, ProblemId)
VALUES (140, 1, 0, 'SHA Sum 0 Function', 'Use the Sigma0 function.', 'PROBLEM', 14, 'SHA_ZERO', NULL);

INSERT INTO Timeout (TimeoutId, TimeoutType, Seconds, Event, Msg)
VALUES (0, 'Info Message', 60, 'Reminder', 'Please acknowledge the current information message to continue.');

INSERT INTO Unit (
    UnitId, CourseId, Title,
    Description,
    SequenceIndex, Pedagogy)
VALUES (
    0, 1, 'SHA-256: See One',
    CONCAT('In this unit, the student will see an example of how each primary step of the SHA-256 algorithm is performed. ',
        'It also exposes the student to the general operation of the ShaTu application.'),
    0, 'Fixed Sequence');

/* ********** ADD FOREIGN KEY CONSTRAINTS ********** */

ALTER Table Assessment
ADD CONSTRAINT fk_assessment_userid
FOREIGN KEY (UserId) REFERENCES Account(UserId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Assessment
ADD CONSTRAINT fk_assessment_knowledgecomponentid
FOREIGN KEY (KnowledgeComponentId) REFERENCES KnowledgeComponent(KnowledgeComponentId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_stepid
FOREIGN KEY (StepId) REFERENCES Step(StepId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_taskid
FOREIGN KEY (TaskId) REFERENCES Task(TaskId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table ExercisingLocation
ADD CONSTRAINT fk_exercisinglocation_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(UnitId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table Hint
ADD CONSTRAINT fk_hint_stepid
FOREIGN KEY (StepId) REFERENCES Step(StepId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table KnowledgeComponent
ADD CONSTRAINT fk_knowledgecomponent_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingStep
ADD CONSTRAINT fk_pendingstep_sessionid
FOREIGN KEY (SessionId) REFERENCES TutoringSession(TutoringSessionId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingStep
ADD CONSTRAINT fk_pendingstep_stepid
FOREIGN KEY (StepId) REFERENCES Step(StepId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingTask
ADD CONSTRAINT fk_pendingtask_pendingstepid
FOREIGN KEY (PendingStepId) REFERENCES PendingStep(PendingStepId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table PendingTask
ADD CONSTRAINT fk_pendingtask_taskid
FOREIGN KEY (TaskId) REFERENCES Task(TaskId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(UnitId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_taskid
FOREIGN KEY (TaskId) REFERENCES Task(TaskId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Step
ADD CONSTRAINT fk_step_timeoutid
FOREIGN KEY (TimeoutId) REFERENCES Timeout(TimeoutId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table Task
ADD CONSTRAINT fk_task_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Task
ADD CONSTRAINT fk_task_problemid
FOREIGN KEY (ProblemId) REFERENCES Problem(ProblemId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Task
ADD CONSTRAINT fk_task_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(UnitId)
ON UPDATE CASCADE ON DELETE SET NULL;  -- Currently nullable in table definition

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_problemid
FOREIGN KEY (ProblemId) REFERENCES Problem(ProblemId)
ON UPDATE CASCADE ON DELETE SET NULL;    -- Deleting a problem should not delete a session

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_unitid
FOREIGN KEY (UnitId) REFERENCES Unit(UnitId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table TutoringSession
ADD CONSTRAINT fk_tutoringsession_userid
FOREIGN KEY (UserId) REFERENCES Account(UserId)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER Table Unit
ADD CONSTRAINT fk_unit_courseid
FOREIGN KEY (CourseId) REFERENCES Course(CourseId)
ON UPDATE CASCADE ON DELETE CASCADE;

