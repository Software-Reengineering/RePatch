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
├── README.md                     # Main project documentation (this file)
├── LICENSE / COPYING             # Licensing
├── build.gradle                  # Gradle build (IntelliJ Platform plugin, JDK 17 toolchain)
├── settings.gradle               # Gradle project settings
├── gradle.properties             # Build configuration properties
├── gradlew, gradle/              # Gradle wrapper
├── database.properties           # DB config for persisting conflict metrics
├── .github/workflows/gradle.yml  # GitHub Actions CI
│
├── src/main/java/edu/unlv/cs/evol/
│   ├── integration/                       # Evaluation pipeline (drives the experiment)
│   │   ├── IntegrationPipeline.java       # Entry point; resolves <user.home>/<dataPath>
│   │   ├── RePatchIntegration.java        # Core patch application logic; dataset selector
│   │   ├── data/                          # In-memory result structures
│   │   ├── database/                       # ActiveJDBC models + DatabaseUtils
│   │   └── utils/                          # Git, GitHub, evaluation, repo-naming helpers
│   └── repatch/                            # The refactoring-aware engine
│       ├── invertOperations/               # Temporarily invert refactorings
│       ├── replayOperations/               # Replay them after integration
│       ├── refactoringObjects/             # Data classes representing refactorings
│       ├── matrix/                         # Conflict matrix modeling and resolution
│       └── utils/                          # Git helpers, utility functions
│
├── src/main/resources/
│   ├── META-INF/plugin.xml                 # IntelliJ plugin configuration
│   ├── create_integration_schema.sql       # Live schema (refactoring_aware_integration_repatch)
│   ├── github-oauth.properties.template    # GitHub token template (real file git-ignored)
│   ├── sample_data/                        # Small scenario set (default)
│   └── complete_data/                      # Full paper scenario set
├── src/test/                               # Unit + PSI round-trip tests and fixtures
│
├── containers/                   # RePatch 2.0 headless evaluation image (see its README)
├── docker/
│   ├── HOW-TO.md                 # Loading the published result dump via phpMyAdmin
│   └── dev-container-repatch/    # GUI browser-desktop dev environment (see its README)
├── database-dump/                # Published results dump (refactoring_aware_integration)
├── analysis/SQL-Scripts.md       # Queries used for the paper's analysis
├── validation/                   # Validation-study schema + analysis script
├── scripts/                      # Golden-verdict regression check
├── lib/                          # Bundled jars resolved by build.gradle
└── figures/                      # Images used by the documentation
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
- Java 17 (OpenJDK) — builds and runs the plugin  
  - JDK 11 and JDK 8 are additionally required for the evaluation and validation targets (kafka-era project SDK and paper-era benchmarks respectively); the provided containers bake all three — see [containers/README.md](containers/README.md)  
- Maven 3.6 or higher  
- Python 3.10 or higher  
- Git (version 2.25 or higher)  
- MySQL Database version 8.0 or higher  
- IntelliJ IDEA 2024.3.7 Community Edition  
- Docker (for optional containerized setup) - Recommended 

#### **Others**
- MySQL Workbench or phpMyAdmin (for GUI-based database interaction)  
- Stable internet connection  

## Installation and Running RePatch

This section will get you through installation and execution of the RePatch tool.

You can run the **RePatch** tool using one of two approaches:

1. **Locally on your machine**, or
2. **Using Docker**.

### Which setup should I use?

| Setup | Best for | Guide |
|---|---|---|
| **Headless container** (`containers/`) | Reproducing the paper's evaluation in bulk. One self-contained image: pipeline, IntelliJ and MySQL inside, driven by a `repatch` CLI. No GUI needed. | [containers/README.md](containers/README.md) |
| **GUI dev container** (`docker/dev-container-repatch/`) | Working through a run interactively, or coursework. A full Linux desktop with IntelliJ in your browser. | [docker/dev-container-repatch/README.md](docker/dev-container-repatch/README.md) |
| **Local install** | Developing RePatch itself against your own IntelliJ. | The steps below |

**If you are unsure, use one of the two containers** — they install and configure every dependency
for you. The remainder of this section covers the local install.


### 1. Clone and build RefactoringMiner

`build.gradle` depends on **RefactoringMiner 2.1.0**, resolved with `transitive = false` so that
only the jar itself lands on the classpath. The latest upstream RefactoringMiner lives
[here](https://github.com/tsantalis/RefactoringMiner); this project uses the 2.1.0 tag of
[this fork](https://github.com/manuelohrndorf/com.github.tsantalis.refactoringminer):

```sh
git clone --branch 2.1.0 https://github.com/manuelohrndorf/com.github.tsantalis.refactoringminer
cd com.github.tsantalis.refactoringminer
./gradlew jar          # produces build/libs/RefactoringMiner-2.1.0.jar
```

> RefactoringMiner 2.1.0 ships an old Gradle wrapper that cannot run on JDK 17. Build it with
> JDK 11 (for example `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./gradlew jar`). The
> resulting jar is a plain library and is consumed fine by the JDK 17 build.

### 2. Add RefactoringMiner to your local Maven repository

`build.gradle` lists `mavenLocal()` first, so a locally installed copy takes precedence:

```sh
mvn install:install-file \
  -Dfile=build/libs/RefactoringMiner-2.1.0.jar \
  -DgroupId=com.github.tsantalis -DartifactId=refactoring-miner \
  -Dversion=2.1.0 -Dpackaging=jar -DgeneratePom=true
```

Verify it installed by checking `~/.m2/repository/com/github/tsantalis/refactoring-miner/2.1.0/`.
`-DgeneratePom=true` writes a dependency-free POM, which is what the `transitive = false` pin
expects.

### 3. Build the project
Clone this project (`git clone https://github.com/Software-Reengineering/RePatch.git`) and open it in IntelliJ IDEA. Wait for project to be indexed by IntelliJ. To build the project, click on build tab in the IntelliJ IDE and select `Build Project` to build RePatch.

**Follow the steps below to run the experiment:**

1. Create a GitHub token and add it to `src/main/resources/github-oauth.properties` (copy `github-oauth.properties.template` in the same directory). This is optional if you are running the tool using only the [sample data](src/main/resources/sample_data/) provided — without a token the tool connects anonymously with GitHub's lower rate limit.
   
2. Edit the run configuration in IntelliJ IDEA under `Run | Edit Configurations` (see the
   [JetBrains guide](https://www.jetbrains.com/help/idea/run-debug-configuration.html#create-permanent))
   to run the `:runIde` task with these arguments:

   ```
   -Pmode=integration -PdataPath=repatch-integration-projects -PevaluationProject=kafka
   ```

   | Argument | Meaning |
   |---|---|
   | `-Pmode=integration` | The only supported mode. |
   | `-PdataPath=` | Directory holding the evaluation clones. Resolved **relative to your home directory** (`IntegrationPipeline` joins it onto `user.home`), so this example checks out into `~/repatch-integration-projects`. Do not give a leading `/`. |
   | `-PevaluationProject=` | The target variant to evaluate. `kafka` runs the `apache/kafka → linkedin/kafka` scenario. |
   | `-PdataSet=` | Which scenario list to read: `sample` (default, [sample_data](src/main/resources/sample_data/)) or `complete` ([complete_data](src/main/resources/complete_data/)). |

   `-PdataPath` only names the clone directory — use `-PdataSet` to switch datasets. Note that the
   dataset *files* are named with underscores (`repatch_integration_projects`), while the clone
   *directory* above uses hyphens; they are unrelated.

   <p align="center">
      <img src="figures/edit-config.png" alt="Edit Configurations" width="600"/>
      <br>
   </p>

3. RePatch clones the target variant and adds the source variant as a remote. When that finishes,
   stop the run and open the cloned evaluation project (for `kafka`, that is
   `~/repatch-integration-projects/...`) in a **separate** IntelliJ window.
4. Wait for IntelliJ to finish indexing and building that cloned project, then close it.
5. Re-run `RePatch` with the `Run` button.
6. Wait for the integration pipeline to finish processing the project.


## Results

This project involves **two separate MySQL databases**. Keeping them straight avoids a lot of
confusion:

| Database | What it is | Created by |
|---|---|---|
| `refactoring_aware_integration_repatch` | **Your own run's output.** Written live by the pipeline. Includes the 2.0 `refactoring_conflict` table. | Created automatically by RePatch from [create_integration_schema.sql](src/main/resources/create_integration_schema.sql) |
| `refactoring_aware_integration` | **The published results** accompanying the paper, for browsing without running anything. | Imported by you from [database-dump/](database-dump/) — see [docker/HOW-TO.md](docker/HOW-TO.md) |

To explore your own results, connect to MySQL with any client (MySQL CLI, MySQL Workbench, DBeaver,
or the bundled phpMyAdmin) and inspect the tables — especially `merge_result`, which records how
**RePatch** reduced or resolved merge conflicts when `git cherry-pick` failed. `refactoring`,
`conflicting_file`, `conflict_block` and `refactoring_conflict` carry the supporting detail;
[analysis/SQL-Scripts.md](analysis/SQL-Scripts.md) collects the queries used for the paper.

**If you have any questions or need assistance, please don’t hesitate to contact the TA.**
