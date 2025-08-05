# Refactoring-Aware Patch Integration Across Structurally Divergent Java Forks

![GitHub last commit (branch)](https://img.shields.io/github/last-commit/unlv-evol/RePatch/main)
![GitHub License](https://img.shields.io/github/license/unlv-evol/RePatch)
## Overview

This project provides `RePatch` - a tool for refactoring-aware patch integration across structurally divergent Java forks. It automates the process of applying patches from one Java codebase to another, even when the codebases have diverged due to refactorings or structural changes. The tool aims to minimize manual effort and resolve conflicts intelligently, making it easier to maintain and synchronize multiple forks of a Java project.

## How RePatch Works

RePatch is a refactoring-aware patch integration tool designed to transfer bug-fix commits across structurally divergent Java variants. It begins by identifying "Missed Opportunity" patches -- bug fixes present in one variant but absent in its fork using the **PaReco** tool. The system links the source and target repositories, and attempts to apply these patches via `git cherry-pick`.

When standard cherry-pick fails due to refactorings (e.g., method renaming or relocation), RePatch detects and temporarily inverts these structural changes using RefactoringMiner. This alignment enables the patch to be applied successfully. After integration, the original refactorings are replayed to preserve the target’s evolution history. This two-phase process improves patch portability across independently evolving codebases.

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

## Project Structure

This section provides an overview of the key components and files in the RePatch repository.

### Project Metadata

These files define the repository's license, usage instructions, and ignore rules:

* `README.md`: Main documentation and overview of the project.
* `LICENSE`, `COPYING`: Licensing details (likely GPL or similar).
* `.gitignore`: Specifies files and folders to exclude from version control.

### Build System

RePatch uses **Gradle** for building and dependency management:

* `build.gradle`: Main Gradle build configuration.
* `settings.gradle`: Project inclusion settings.
* `gradlew`, `gradle/wrapper/*`: Gradle wrapper scripts and binaries.
* `gradle.properties`: Custom build properties.

### CI / GitHub Actions

* `.github/workflows/gradle.yml`: Defines the GitHub Actions pipeline to automatically build and test the project using Gradle.

### Docker Environment

Located in `docker/dev-container-repatch`, this directory contains a ready-to-use Dockerized desktop environment with tools for running and debugging RePatch:

* `Dockerfile`: Builds a full GUI development container with Git, Java, IntelliJ, and RefactoringMiner.
* `docker-compose.yml`: Orchestrates the dev container (and optionally MySQL/phpMyAdmin).
* `Dockerfiles/add_resolution.sh`, `add_resolutions.sh`: Helper scripts for screen resolution setup inside the container.
* `README.md`: Usage instructions for the dev environment.

### Core Integration Logic

The heart of RePatch’s patch integration functionality lives in:

* `IntegrationPipeline.java`: Main entry point that coordinates patch identification and transfer.
* `RePatchIntegration.java`: Contains logic for applying cherry-picked commits, detecting failures, and invoking semantic alignment.

### Data Models

Defines the data structures used during integration, conflict analysis, and result tracking:

* `ComparisonResult.java`, `ConflictBlockData.java`, `ConflictingFileData.java`, `FileDetails.java`, `SourceFile.java`: Represent structured data about file states, conflict regions, and integration metadata.

### Database Layer

Handles persistence of patch analysis and conflict metrics:

* `ConflictBlock.java`, `ConflictingFile.java`, `DatabaseUtils.java`, `FileStatistics.java`, `MergeCommit.java`: Define database entities and helper utilities for storing and querying conflict information.
  
```
RePatch/
├── LICENSE                          # License file (e.g., GPL or similar)
├── COPYING                          # GNU license copy (if applicable)
├── README.md                        # Main project documentation
├── build.gradle                     # Gradle build script
├── settings.gradle                  # Gradle project settings
├── gradle.properties                # Build configuration properties
├── github-autho.properties          # GitHub access credentials (likely excluded from Git)
├── database.properties              # DB config for persisting conflict metrics
├── .gitignore                       # Git ignore rules
├── .github
│   └── workflows
│       └── gradle.yml              # GitHub Actions CI config
├── docker
│   └── dev-container-repatch       # GUI-based dev environment
│       ├── Dockerfile              # Main Docker image build script
│       ├── docker-compose.yml      # Compose file for webtop + services
│       ├── README.md               # Setup instructions
│       └── Dockerfiles
│           ├── add_resolution.sh   # Adds a single screen resolution
│           └── add_resolutions.sh  # Adds multiple resolutions
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar      # Gradle wrapper binary
│       └── gradle-wrapper.properties # Wrapper settings
├── src
│   └── main
│       └── java
│           └── edu
│               └── unlv
│                   └── cs
│                       └── evol
│                           └── integration
│                               ├── IntegrationPipeline.java      # Main CLI entrypoint
│                               ├── RePatchIntegration.java       # Core patch application logic
│                               ├── data/
│                               │   ├── ComparisonResult.java     # Structure for analysis result
│                               │   ├── ConflictBlockData.java    # Structure for conflict block info
│                               │   ├── ConflictingFileData.java  # Structure for file-level conflict info
│                               │   ├── FileDetails.java          # Captures full file metadata
│                               │   └── SourceFile.java           # Represents a source variant file
│                               └── database/
│                                   ├── ConflictBlock.java        # Database model for conflict blocks
│                                   ├── ConflictingFile.java      # Database model for conflicting files
│                                   ├── DatabaseUtils.java        # DB connection helpers
│                                   ├── FileStatistics.java       # File-level integration stats
│                                   └── MergeCommit.java          # Represents merge commit metadata
```


## Getting Started

### Prerequisites

- Java 11
- Maven 3.6 or higher
- Git
- MySQL Database >=8.0
- MySQL Workbench or PHPMyAdmin (Optional)
- Intellij IDEA 2020.1.2 Community Edition
- Python >= 3.10
- Processor: CPU 1.18 GHZ or greater
- RAM: >=16 GB
- Operating System: Linux (Ubuntu/Debian distribution)
- Free Storage: >= 15 GB

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
#### RQ1: How often do source variant bug-fix patches fail to apply cleanly to target variants using Git’s cherry-pick?

#### RQ2: What proportion of cherry-pick failures are attributable to refactoring operations (e.g., method / class renaming or moving)?

#### RQ3: Can a refactoring-aware integration approach reduce merge conflicts and increase the success rate of applying patches across divergent variants?

## Contributing

Contributions are welcome! Please open issues or submit pull requests for bug fixes, enhancements, or new features.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

