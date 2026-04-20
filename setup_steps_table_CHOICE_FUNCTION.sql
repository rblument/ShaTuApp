-- below is already done in setup_ShaTuDB.sql
--INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
-- VALUES (0, 0, 'Acknowledge Welcome', 'First Step in learning the tutor', 0, 0, 'INFO_MESSAGE', 0, 0);

-- below are all new (not in setup_ShaTuDB.sql


--INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
-- VALUES (1, 10, 'Encode ASCII', 'First Step in encoding ASCII', 0, 0, 'ENCODE_ASCII', 0, 0);

INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
 VALUES (1, 110, 'Select Variables', 'Select the variables that the Choice Function will use', 0, 0, 'CHOICE_FUNCTION', 0, 0);

INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
 VALUES (2, 110, 'Compare First Bit', 'using the selected vars, walk through how to calculate the first outcome', 1, 0, 'CHOICE_FUNCTION', 0, 0);

INSERT INTO Step (Id, TaskId, Title, Description, SequenceIndex, ExercisedComponentId, StepSubType, SubTypeId, TimeoutId)
 VALUES (3, 110, 'Repeat bit calculation', 'perform bit calculation on remaining bits', 2, 0, 'CHOICE_FUNCTION', 0, 0);



