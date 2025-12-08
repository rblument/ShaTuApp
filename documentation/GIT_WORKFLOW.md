# Git Workflow Guide

This document explains the recommended Git workflow for this project using
Git Bash and GitHub. It is intended as a guide for project members who may not have a lot 
of experience using Git. It assumes project members have already cloned the 
repository to their machine.

---

## Starting a New Task
Navigate to the project and ensure your **local development** branch has the latest
updates from the **remote development** branch.

Open Git Bash and type:

```
cd <path/to/ShaTuApp/>
git checkout development
git pull origin development
```

Create the new branch in your local repository:
```
git checkout -b <SHAT-n>
```
**Note:** The ShaTu project names branches after the Jira task for which the 
work is being done (e.g., SHAT-123).

## Stage Your Changes
To see which files you have changed or added, use:

```
git status
```

To stage specific files:

```
git add <path/to/file_1> <path/to/file_n>
```
To stage all files at once:
```
git add .
```

## Commit Your Changes
Use brief, meaningful commit messages written in the imperative mood:

```
git commit -m "Create Git Workflow guide"
```

## Push Your Branch
Before pushing your branch, you should make sure there will not be any merge
conflicts. 

**1. Switch to your local development branch:**
```
git checkout development
```
**2. Bring your local development branch up to date with the remote development branch:**
```
git pull origin development
```
**3. Return to the branch you want to push**
```
git checkout <branch-you-will-push>
```
**4. Bring your branch up to date with your local development branch:**
```
git merge development
```
**5. Resolve any merge conflicts that appear**

**6. Send your changes to the remote repository:**
```
git push -u origin <branch-you-will-push>
```

## Open a Pull Request (PR)
1. Go to the project's repository page on GitHub
2. Click **Compare & pull request**
3. Title your PR starting with the Jira task ID (e.g., SHAT 123: Create Git Worflow Guide)
4. In the description, explain the changes you made and why
5. Submit the pull request

## Review & Merge
Project members are encouraged to review each other's work respectfully and
constructively.

To look at another teammate's branch on your machine, use:
```
git fetch origin
git checkout -b <temporary-branch> origin/<teammate's-branch>
```
To review their PR:
1. Go to their PR on GitHub
2. Go to **Files changed**
3. Click **Review changes**
4. Choose **Approve** or **Request change**
5. Click **Submit review**
6. Click **Merge pull request** if approved

**Note:** The course instructor has requested that branches not be 
deleted from the remote repository after merging. However, you may delete
branches from your _local repository_.

## Additional Useful Git Commands

### ```restore```
Undo the changes you made to a file that you have not committed:
```
git restore <path/to/file>
```
This restores the file to the most recent commit. This is useful when you have made
changes that you don't necessarily want to keep.

### ```stash```
If you are not ready to commit your work but need to switch branches:
```
git stash
```
This puts all of your work onto a stack and saves it there.

When you are ready to continue your work:
```
git checkout <your-branch>
git stash pop
```


