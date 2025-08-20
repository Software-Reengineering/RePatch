# RePatch

## Overview

This project provides `RePatch` - a tool for refactoring-aware patch integration across structurally divergent Java forks. It automates the process of applying patches from one Java codebase to another, even when the codebases have diverged due to refactorings or structural changes. The tool aims to minimize manual effort and resolve conflicts intelligently, making it easier to maintain and synchronize multiple forks of a Java project.

## How RePatch Works

RePatch is a refactoring-aware patch integration tool designed to transfer bug-fix commits across structurally divergent Java variants. It begins by identifying "Missed Opportunity" patches -- bug fixes present in one variant but absent in its fork using the **PaReco** tool. The system links the source and target repositories, and attempts to apply these patches via `git cherry-pick`.

When standard cherry-pick fails due to refactorings (e.g., method renaming or relocation), RePatch detects and temporarily inverts these structural changes using RefactoringMiner. This alignment enables the patch to be applied successfully. After integration, the original refactorings are replayed to preserve the target’s evolution history. This two-phase process improves patch portability across independently evolving codebases.



> **Read more about this work [here](https://arxiv.org/abs/2508.06718)**

## Features

- **Refactoring Detection:** Identifies structural changes between codebases to improve patch application accuracy.
- **Automated Patch Integration:** Applies patches across divergent forks with minimal manual intervention.
- **Conflict Resolution:** Detects and helps resolve integration conflicts.
- **Extensible Architecture:** Modular design for easy extension and customization.

## Project Structure

This section provides an overview of the key components and files in the RePatch repository.

  
```
RePatch/
├── LICENSE                          # License file (e.g., GPL or similar)
├── COPYING                          # GNU license copy (if applicable)
├── README.md                        # Main project documentation
├── build.gradle                     # Gradle build script
├── settings.gradle                  # Gradle project settings
├── gradle.properties                # Build configuration properties
├── github-autho.properties          # GitHub access credentials (credentials excluded from Git)
├── database.properties              # DB config for persisting conflict metrics
├── .gitignore                       # Git ignore rules
├── .github
│   └── workflows
│       └── gradle.yml                                  # GitHub Actions CI config
├── docker
│   └── dev-container-repatch                           # GUI-based dev environment
│       ├── Dockerfile                                  # Main Docker image build script
│       ├── docker-compose.yml                          # Compose file for webtop + services
│       ├── README.md                                   # Setup instructions
│       └── Dockerfiles
├── gradle/                                             # Gradle wrapper configurations
├── src
│   └── main
│       └── java/edu/unlv/cs/evol
│            └── integration
│                ├── IntegrationPipeline.java           # Main integration logic included
│                ├── RePatchIntegration.java            # Core patch application logic
│                ├── data/
│                    ├── ComparisonResult.java          # Structure for analysis result
│                    │   ├── ConflictBlockData.java     # Structure for conflict block info
│                    │   ├── ConflictingFileData.java   # Structure for file-level conflict info
│                    │   ├── FileDetails.java           # Captures full file metadata
│                    │   └── SourceFile.java            # Represents a source variant file
│                 └── database/
│                    ├── ConflictBlock.java             # Database model for conflict blocks
│                    ├── ConflictingFile.java           # Database model for conflicting files
│                    ├── DatabaseUtils.java             # DB connection helpers
│                    ├── FileStatistics.java            # File-level integration stats
│                    └── MergeCommit.java               # Represents merge commit metadata
│                 └── utils/                            # Helper functions
│            └── repatch
│               ├── matrix/                             # Conflict matrix modeling and resolution
│               ├── refactoringObjects/                 # Data classes representing refactorings
│               ├── replayOperations/                   # Classes to replay transformations
│               ├── invertOperations/                   # Classes to invert transformations
│               └── utils/                              # Git helpers, utility functions
│   │   └── resources/
│   │       ├── META-INF/                               # Plugin configuration
│   │       ├── complete_data/                          # Real-world patch/project integration data
│   │       ├── sample_data/                            # Sample integration scenarios
│   │       ├── repatch_database/                       # Database configuration
│   │       └── create_integration_schema.sql           # SQL setup script
│   └── test/
│       └── resources/
│           ├── extractMethod*                          # Test cases for extract method refactorings
│           ├── moveRename*                             # Test cases for class/method renaming/moving
│           ├── rename*                                 # Method and class rename test data
│           └── rePatchTestData/                        # Refactoring merge replay test cases
```

## Getting Started

### **System Requirements**

To ensure the successful execution and review of the `RePatch` artifact, we recommend the following system configuration:

#### **Hardware**
- **Processor:** 1.18 GHz CPU or faster  
- **RAM:** At least 16 GB  
- **Disk Space:** At least 15 GB of free storage  

#### **Operating System**
- Linux (Ubuntu or Debian-based distribution)  

#### **Software Dependencies**
- Java 11 (OpenJDK)  
- Maven 3.6 or higher  
- Python 3.10 or higher  
- Git (version 2.25 or higher)  
- MySQL Database version 8.0 or higher  
- IntelliJ IDEA 2020.1.2 Community Edition  
- Docker (for optional containerized setup) - Recommended 

#### **Others**
- MySQL Workbench or phpMyAdmin (for GUI-based database interaction)  
- Stable internet connection  

## Installation and Running RePatch

This section will get you through installation and execution of the RePatch tool.

You can run the **RePatch** tool using one of two approaches:

1. **Locally on your machine**, or
2. **Using Docker**.

The instructions below explain how to run the tool locally. **For most users, we recommend using the containerized setup provided in the [docker](docker/dev-container-repatch/) directory, which includes a dedicated [README](docker/dev-container-repatch/README.md) with step-by-step guidance. This will automatically install and configure all neccessary tools**.


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

**Follow the steps below to run the experiment:**

1. Create a GitHub token and add it to `github-oauth.properties`. This is optional if you are running the tool using only the [sample data](src/main/resources/sample_data/) provided.
   
2. Edit the configuration tasks in the IntelliJ IDE under `Run | Edit Configurations` (more information can be found [here](https://www.jetbrains.com/help/idea/run-debug-configuration.html#create-permanent)) to have `:runIde` and include set `-Pmode=` to `integration` and `-PdataPath=` to `repatch-integration-projects`. Then, set `-PevaluationProject=` to the project (target variant) that you want to evaluate on. For our case, it would look like `-PevaluationProject=kafka` since we want to test run integration on `linkedin/kafka`.
```
-Pmode=integration -PdataPath=/repatch-integration-projects -PevaluationProject=kafka
```

   <p align="center">
      <img src="figures/edit-config.png" alt="Edit Configurations" width="600"/>
      <br>
   </p>

2. RePatch will automatically clone the target variant and add the remote source variant. Once this is done, stop the
   running project and open the project being integrated - specified in the `-PevaluationProject`(for our case, it **kafka**) with the IntelliJ IDEA in a new window. This project will be located in the directory specified in the `-PdataPath` -- for our case, it will be located in **/repatch_integration_projects**
3. Wait for IntelliJ to build the cloned project, then close it.
4. Now re-run the `RePatch` by clicking the `Run` button in the IntelliJ IDE.
5. Wait for the integration pipeline to finish processing that project.


## Results

Data produced by the integration pipeline is stored in the MySQL database `refactoring_aware_integration_repatch`. If it does not already exist, **RePatch** will create it automatically.

To explore the results, connect to MySQL with any client (e.g., MySQL CLI, MySQL Workbench, DBeaver) and inspect the tables—especially `merge_result`, which records how **RePatch** reduced or resolved merge conflicts when `git cherry-pick` failed. The other tables also contain useful metadata and diagnostics such as *refactorings*, *conflicting files*, *conflict blocks*, etc., so give them a look as well.

**If you have any questions or need assistance, please don’t hesitate to contact the TA.**
