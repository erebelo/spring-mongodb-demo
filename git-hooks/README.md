# Git Hooks Setup

This repository provides a streamlined setup for managing Git hooks that enforce code formatting standards automatically. Using Maven with the Spotless plugin, this setup ensures consistent code quality across contributions by applying a pre-commit hook to check and, if necessary, auto-correct formatting.

## Overview

### Git Hooks

Git hooks are scripts that Git executes before or after events such as commits, merges, and more. This setup provides a pre-commit hook that automatically checks and applies code formatting using the Spotless Maven plugin.

## Scripts

### 1. `setup-hooks.sh`

This script sets up the Git hooks by copying the relevant hook scripts from the `git-hooks` directory to the `.git/hooks` directory in your project. It ignores specific files (like the setup and remove scripts) to prevent them from being executed as hooks.

#### Usage:

To set up the Git hooks, run the following command in your terminal from the root of your Git repository:

```sh
bash git-hooks/setup-hooks.sh
```

### 2. `remove-hooks.sh`

This script removes all the Git hook scripts from the `.git/hooks` directory. It is useful for cleaning up the hooks if you no longer want them or if you're making changes to the scripts. The script checks for the existence of the hooks directory and removes all hook files, ensuring a clean slate for any new configurations.

#### Usage:

To remove the Git hooks, run the following command in your terminal from the root of your Git repository:

```sh
bash git-hooks/remove-hooks.sh
```

### 3. `pre-commit`

This is a Git pre-commit hook script that checks for code formatting before allowing a commit. It uses the Spotless Maven plugin to ensure that your code follows the specified formatting rules.

## How it works

- Checks if the `mvn` command is available in your PATH.
- Runs `mvn spotless:check` to validate the code formatting.
- If the check fails, it runs `mvn spotless:apply` to format the code automatically.
- Displays messages to inform you about the status of the formatting checks.
- After running `mvn spotless:apply`, it will execute `git status` to refresh the Git status.
