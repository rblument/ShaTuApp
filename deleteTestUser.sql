-- SHATU: SHA-256 Tutor

-- (C) Johanna & Richard Blumenthal, All rights reserved

-- Unauthorized use, duplication or distribution without the authors' permission is strictly prohibited.

-- Unless required by applicable law or agreed to in writing, this software is 
-- distributed on an "AS IS" basis without warranties or conditions of any kind, 
-- either expressed or implied.


-- Author:  rickb
-- Created: Jan 11, 2025

-- Eventually, this script will be useless since it's a very heavy hammer which 
-- cleans up the 'test@regis.edu' user, but also all assessment data for every 
-- student account. It's usefulness is that it leaves static course data untouched.

USE ShaTuDB;

SET FOREIGN_KEY_CHECKS = 0;  -- Disable foreign key checks

DELETE FROM Account WHERE UserId = 'test@regis.edu';
DELETE FROM StudentModel WHERE UserId = 'test@regis.edu';
DELETE FROM Student WHERE UserId = 'test@regis.edu';

-- Note this will clear every user and reset the auto-increment to zero.
TRUNCATE TABLE Assessment;
TRUNCATE TABLE TutoringSession;
TRUNCATE TABLE PendingTask;
TRUNCATE TABLE PendingStep;

DELETE FROM LoginHistory WHERE UserId = 'test@regis.edu';

SET FOREIGN_KEY_CHECKS = 1;  -- Reinstate foreign key checks