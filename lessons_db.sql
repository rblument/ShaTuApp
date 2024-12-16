-- login to database with root.
CREATE DATABASE lessons_db;

GRANT ALL PRIVILEGES ON student_db.* TO 'student'@'localhost'; -- EDIT Accordingly

USE lessons_db;


CREATE TABLE Course (
  CourseId int NOT NULL DEFAULT '0',
  CourseTitle varchar(255) DEFAULT NULL,
  CoursePrimaryPedagogy varchar(255) DEFAULT NULL,
  CourseDescription varchar(255) DEFAULT NULL,
  PRIMARY KEY (CourseId));
  
CREATE TABLE ExercisingLocation (
  ExercisingLocationId int NOT NULL DEFAULT '0',
  CourseId int DEFAULT NULL,
  UnitId int DEFAULT NULL,
  TaskId int DEFAULT NULL,
  StepId int DEFAULT NULL,
  KnowledgeComponentId int DEFAULT NULL,
  PRIMARY KEY (ExercisingLocationId));
  
CREATE TABLE Hint (
  HintId int NOT NULL DEFAULT '0',
  HintText varchar(255) DEFAULT NULL,
  HintSequence int DEFAULT NULL,
  PRIMARY KEY (HintId));
  
CREATE TABLE KnowledgeComponent (
  KnowledgeComponentId int NOT NULL DEFAULT '0',
  KnowledgeComponentTitle varchar(255) DEFAULT NULL,
  KnowledgeComponentBloomLevel varchar(255) DEFAULT NULL,
  KnowledgeComponentFocus tinyint DEFAULT NULL,
  KnowledgeComponentPedagogy varchar(255) DEFAULT NULL,
  KnowledgeComponentDescription varchar(255) DEFAULT NULL,
  KnowledgeComponentExercisingLocation int DEFAULT NULL,
  KnowledgeComponentGranularity varchar(255) DEFAULT NULL,
  PRIMARY KEY (KnowledgeComponentId));
  
CREATE TABLE Step (
  StepId int NOT NULL DEFAULT '0',
  TaskId int DEFAULT NULL,
  StepTitle varchar(255) DEFAULT NULL,
  StepDescription varchar(45) DEFAULT NULL,
  StepType varchar(255) DEFAULT NULL,
  StepSequence int DEFAULT NULL,
  StepScaffolding varchar(255) DEFAULT NULL,
  StepHint int DEFAULT NULL,
  StepNotifyTutor tinyint DEFAULT '1',
  CourseId int DEFAULT NULL,
  StepExcercisedComponent varchar(255) DEFAULT NULL,
  StepTimeout varchar(255) DEFAULT NULL,
  StepOutcome int DEFAULT NULL,
  StepUnit int DEFAULT NULL,
  StepMsg varchar(255) DEFAULT NULL,
  PRIMARY KEY (StepId));
  
CREATE TABLE Task (
  TaskId int NOT NULL DEFAULT '0',
  UnitId int DEFAULT NULL,
  TaskTitle varchar(255) DEFAULT NULL,
  TaskDescription varchar(255) DEFAULT NULL,
  TaskSequence int DEFAULT NULL,
  StepIndex int DEFAULT NULL,
  CourseId int DEFAULT NULL,
  ComponentId int DEFAULT NULL,
  TaskProblemId int DEFAULT NULL,
  ComponentKind varchar(255) DEFAULT NULL,
  ComponentType varchar(255) DEFAULT NULL,
  PRIMARY KEY (TaskId));
  
CREATE TABLE Unit (
  UnitId int NOT NULL DEFAULT '0',
  CourseId int DEFAULT NULL,
  UnitSequence int DEFAULT NULL,
  UnitTitle varchar(255) DEFAULT NULL,
  UnitDescription varchar(255) DEFAULT NULL,
  UnitPedagogy varchar(255) DEFAULT NULL,
  PRIMARY KEY (UnitId));


-- Populate tables for testing.

INSERT INTO Course
(CourseId,
CourseTitle,
CoursePrimaryPedagogy,
CourseDescription)
VALUES
(0,'Course Title 0', 'Fixed Sequence', 'Course Description 0'),
(1,'Course Title 1', 'Fixed Sequence', 'Course Description 1'),
(2,'Course Title 2', 'Fixed Sequence', 'Course Description 2');

INSERT INTO Hint
(HintId,
HintText,
HintSequence)
VALUES
(0, 'Click the button to continue 0', 0),
(1, 'Click the button to continue 1', 1),
(2, 'Click the button to continue 2', 2);

INSERT INTO ExercisingLocation
(ExercisingLocationId,
CourseId,
UnitId,
TaskId,
StepId,
KnowledgeComponentId)
VALUES
(0, 0, 0, 0),
(1, 1, 1, 1),
(2, 2, 2, 2);

INSERT INTO KnowledgeComponent
(KnowledgeComponentId,
KnowledgeComponentTitle,
KnowledgeComponentBloomLevel,
KnowledgeComponentFocus,
KnowledgeComponentPedagogy,
KnowledgeComponentDescription,
KnowledgeComponentExercisingLocation,
KnowledgeComponentGranularity)
VALUES
(0,'Knowledge Component Title 0', 'Knowledge', 1, 'Other', 'Knowledge Description 0', 0, 'Granularity'),
(1,'Knowledge Component Title 1', 'Comprehension', 1, 'Other', 'Knowledge Description 1', 1, 'Granularity'),
(2,'Knowledge Component Title 2', 'Knowledge', 1, 'Other', 'Knowledge Description 2', 2, 'Granularity');

INSERT INTO Step
(StepId,
TaskId,
StepTitle,
StepDescription,
StepType,
StepSequence,
StepScaffolding,
StepNotifyTutor,
CourseId,
StepExcercisedComponent,
StepTimeout,
StepOutcome,
StepUnit,
StepMsg)
VALUES
(0,'Step Title 0', 'Step Description 0', 'Information Message', 0, 'Low', 1, 0, 0, 0, 0, 0, 0, 'Step Msg 0'),
(1,'Step Title 1', 'Step Description 1', 'Information Message', 1, 'Extreme', 1, 1, 0, 0, 0, 0, 0, 'Step Msg 1'),
(2,'Step Title 2', 'Step Description 2', 'Information Message', 2, 'Medium', 1, 2, 0, 0, 0, 0, 0, 'Step Msg 2');

INSERT INTO Task
(TaskId,
UnitId,
TaskTitle,
TaskDescription,
TaskSequence,
StepIndex,
CourseId,
ComponentId,
TaskProblemId,
ComponentKind,
ComponentType)
VALUES
(0, 0, 'Task Title 0', 'Task Description 0', 0, 0, 0, 0, 0, 'Information Message', 'ComponentType'),
(1, 1, 'Task Title 0', 'Task Description 1', 1, 1, 1, 1, 1, 'Information Message', 'ComponentType'),
(2, 2, 'Task Title 0', 'Task Description 2', 2, 2, 2, 2, 2, 'Information Message', 'ComponentType');

INSERT INTO Unit
(UnitId,
CourseId,
UnitSequence,
UnitTitle,
UnitDescription,
UnitPedagogy)
VALUES
(0, 0, 0, 'Unit Title 0', 'Unit Description 0', 'Fixed Sequence'),
(1, 1, 1, 'Unit Title 0', 'Unit Description 1', 'Fixed Sequence'),
(2, 2, 2, 'Unit Title 0', 'Unit Description 2', 'Fixed Sequence');



