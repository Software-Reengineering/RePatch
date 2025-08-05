# Refactoring-Aware Patch Integration Across Structurally Divergent Java Forks

![GitHub last commit (branch)](https://img.shields.io/github/last-commit/unlv-evol/RePatch/main)
![GitHub License](https://img.shields.io/github/license/unlv-evol/RePatch)
## Overview

This project provides `RePatch` - a tool for refactoring-aware patch integration across structurally divergent Java forks. It automates the process of applying patches from one Java codebase to another, even when the codebases have diverged due to refactorings or structural changes. The tool aims to minimize manual effort and resolve conflicts intelligently, making it easier to maintain and synchronize multiple forks of a Java project.

## Features

- **Refactoring Detection:** Identifies structural changes between codebases to improve patch application accuracy.
- **Automated Patch Integration:** Applies patches across divergent forks with minimal manual intervention.
- **Conflict Resolution:** Detects and helps resolve integration conflicts.
- **Extensible Architecture:** Modular design for easy extension and customization.

## Project Structure

Below is an overview of the main files and directories in this project:

- **src/**: Contains the Java source code for the patch integration tool.
- **src/main/java/**: Main application logic, including patch analysis, integration, and conflict resolution modules.
- **src/test/java/**: Unit and integration tests for the core functionality.
- **pom.xml**: Maven configuration file specifying dependencies and build instructions.
- **target/**: Directory where compiled classes and packaged JAR files are generated after building the project.
- **README.md**: This documentation file.
- **LICENSE**: The license file for the project (MIT License).
- **.gitignore**: Specifies files and directories to be ignored by Git.

Each file and directory is organized to support modular development, testing, and easy usage of the patch integration tool.

## Getting Started

### Prerequisites

- Java 11
- Maven 3.6 or higher
- Git
- MySQL
- MySQL Workbench or PHPMyAdmin (Optional)
- Intellij IDEA 2020.1.2 Community Edition

## Running the Tool

### 1. Clone and build RefactoringMiner 
The lastest version of RefactoringMiner is [here](https://github.com/tsantalis/RefactoringMiner). However, for this project, we used RefactoringMiner 2.2 found [here](https://github.com/manuelohrndorf/com.github.tsantalis.refactoringminer). 
```sh
git clone https://github.com/manuelohrndorf/com.github.tsantalis.refactoringminer
```
Then build RefactoringMiner with `./gradlew distzip`. It will be under build/distributions.

### 2. Add RefactoringMiner to your local maven repository
You will need to add RefactoringMiner to your local maven repository to use it in the build.gradle. You can use `mvn install:install-file -Dfile=<path-to-file>` to add it to your local maven repository. You can verify that it's been installed by checking the path `/home/username/.m2/repository/org/refactoringminer`.

### 3. Build the project
Clone this project (`git clone https://github.com/unlv-evol/Repatch.git`) and open it in IntelliJ IDE. Wait for project to be indexed by IntelliJ. To build the project, click on build tab in the IntelliJ IDE and select `Build Project` to build RePatch.

## Reproducing the Results in the Paper
Use the refactoring aware patch integration dump found [here]() to populate the *refactoring_aware_integration* database. Once the database is populated, you can use the SQL scripts provide in `script` directory of this project.
#### RQ1: RQ1: How often do source variant bug-fix patches fail to apply cleanly to target variants using Git’s cherry-pick?

#### RQ2: What proportion of cherry-pick failures are attributable to refactoring operations (e.g., method / class renaming or moving)?

#### RQ3: Can a refactoring-aware integration approach reduce merge conflicts and increase the success rate of applying patches across divergent variants?

## Contributing

Contributions are welcome! Please open issues or submit pull requests for bug fixes, enhancements, or new features.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

