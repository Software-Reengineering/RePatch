# Development Container for RePatch

This Docker compose starts a Linux container including a desktop accessible through a web browser. While you can run the docker setup in any machine, we still recommended that you use a Linux Os with Debain/Ubuntu distribution.

All dependencies for building and running the repatch tool are included in the container.
You must have installed [Docker](https://docs.docker.com/get-started/get-docker/) and [Git](https://git-scm.com/downloads) on your local system.

## Build the Docker Image

First clone the RePatch repository. RePatch is an extension of RefMerge by [Ellis et al.](https://github.com/ualberta-smr/RefMerge).
```
cd <path to your git folder>
git clone  https://github.com/danielogen/RePatch.git
```

Assuming Docker is running, initially, build the Docker image with:
```
cd RePatch/docker/dev-container-repatch
docker compose build --no-cache
```

Then start the container with:
```
docker compose up --build
```
In principle, you can install packages (```sudo apt update && sudo apt install -y <packages>```, no sudo password) and make changes within the containerized desktop as on a normal Ubuntu Linux OS.
However, only the user data in ```/config``` (the user's home directory) is mapped to a Docker volume ```dev_container_repatch_data``` (see docker-compose.yml), i.e., if you delete the container, only changes wrt. the volume are persistent until you also delete the volume.
 (Checkout the corresponding pages in the Docker Desktop UI or use ```docker ps``` and ```docker volume ls``` to list your containers and volumes.)

## Access the Remote Desktop

### KasmVNC:

After the container has been started, go to ```http://localhost:3000/``` in your browser.
The desktop is only accessible locally.
Note, to use it remotely you would have to remove ```127.0.0.1``` from the docker-compose.yml and consider using a password and HTTPS ```https://localhost:3001/```.
Adjusting the size of the browser window will set the according screen resolution of the desktop.
In the browser window in the left (initially collapsed) side panel, you can also edit settings such as the streaming quality.

#### ```Windowa Issue:``` Clock Synchronization

TLDR; If you use *Windows Docker with WSL* make sure your clock is correctly, recently synchronized. 
Otherwise, KasmVNC can cause memory spikes that may crash the desktop session.
Run "synchronize now"  in the settings app (Time and Language > Date and Time) or run```w32tm /resync``` in terminal (administrator).
You can also automate this using the script below.
Lowering the streaming quality (medium streaming setting at FHD 1920 × 1080 pixel, 24 FPS) can also help. 
Alternatively, use RustDesk as described below.

1. Create a new file: C:\Scripts\sync-clock.vbs
   ```
   Set objShell = CreateObject("WScript.Shell")
   objShell.Run "powershell.exe -ExecutionPolicy Bypass -Command ""net start w32time; w32tm /resync""", 0, False
   ```
1. Windows + R: taskschd.msc
1. Create Task... (not Create Basic Task...)
1. General -> Name: Sync-Clock
1. Select Run with highest privileges.
1. Triggers -> New...
   - On workstation unlock
1. Triggers -> New...  
   - On a schedule (One time) -> Advanced settings: Repeat every 1 hour for a duration of Indefinitely
1. Action -> New...
   - Program: wscript.exe
   - Arguments: C:\Scripts\sync-clock.vbs

## Installing and Running RePatch in the Dev-Container
This section will get you through installation and execution of the RePatch tool using docker setup. 

Stopping the container would be comparable to a shut down of the OS.
This is also what Docker does if you shut down your host system or Docker itself; therefore, you may have to start the container after rebooting.
```
docker stop dev-container-repatch
```
Starting the container would be comparable to booting the OS.
```
docker start dev-container-repatch
```

The desktop has a link to start IntelliJ for the project repatch:
- IntelliJ IDEA: RePatch

The corresponding project is in the git folder in the home directory: ```/config/git/```
- ```/config/git/RePatch```


## Build the project
Use the desktop icon to open the project in the IntelliJ IDE. Wait for the project to be indexed by IntelliJ. To build the project, click on build tab in the IntelliJ IDE and select `Build Project`.

<!-- #### Edit configuration
Edit the configuration tasks in the IntelliJ IDE under `Run | Edit Configurations` (more information can be found [here](https://www.jetbrains.com/help/idea/run-debug-configuration.html#create-permanent)) to have `:runIde` and include set `-Pmode=` to `integration` and `-PdataPath=` to `repatch-integration-projects`.
Then, set `-PevaluationProject=` to the project (target variant) that you want to evaluate on. For our case,
it would look like `-PevaluationProject=kafka` since we want to test run integration on `linkedin/kafka`.

<p align="center">
  <img src="figures/edit-config.png" alt="Edit Configurations" width="600"/>
  <br>
</p> -->


<!-- **NB: Running the entire experiment takes more than 10 hour to complete. For this reason, we provide one source -> target variant (apache/kakfa -> linkedin/kafka) and 5 bugfix patches (pull requests), alongside the full dataset, to facilitate quick testing of the tool/experiment. Both the test and full projects are located in: `src/main/resources` (sample_data and completed_data) directory**.  -->

**Follow the steps below to run the experiment:**

1. Create a GitHub token and add it to `github-oauth.properties`. This is optional if you are running the tool using the sample data.
   
2. Edit the configuration tasks in the IntelliJ IDE under `Run | Edit Configurations` (more information can be found [here](https://www.jetbrains.com/help/idea/run-debug-configuration.html#create-permanent)) to have `:runIde` and include set `-Pmode=` to `integration` and `-PdataPath=` to `repatch-integration-projects`. Then, set `-PevaluationProject=` to the project (target variant) that you want to evaluate on. For our case, it would look like `-PevaluationProject=kafka` since we want to test run integration on `linkedin/kafka`.
```
-Pmode=integration -PdataPath=/repatch-integration-projects -PevaluationProject=kafka
```
   <p align="center">
      <img src="../../figures/edit-config.png" alt="Edit Configurations" width="600"/>
      <br>
   </p>

3. RePatch will automatically clone the target variant and add the remote source variant. Once this is done stop the running project and open the project being integrated - specified in the `-PevaluationProject`(for our case, it **kafka**) with the IntelliJ IDEA in a new window. This project will be located in the directory specified in the `-PdataPath` -- for our case, it will be located in **/config/repatch_integration_projects**

4. Wait for IntelliJ to build the cloned project, then close it.

5. Now re-run the `RePatch` by clicking the `Run` button in the IntelliJ IDE.

6. Wait for the integration pipeline to finish processing that project.

## Results

Data produced by the integration pipeline is stored in the MySQL database `refactoring_aware_integration_repatch`. If it does not already exist, **RePatch** will create it automatically.

To explore the results, go to you browser on `http://localhost:8080`. This will open phpMyAdmin - **`user`=root** and **`password` = root**. Inspect the tables—especially `merge_result`, which records how **RePatch** reduced or resolved merge conflicts when `git cherry-pick` failed. The other tables also contain useful metadata and diagnostics such as *refactorings*, *conflicting files*, *conflict blocks*, etc., so give them a look as well.

**If you have any questions or need assistance, please don’t hesitate to contact the TA.**