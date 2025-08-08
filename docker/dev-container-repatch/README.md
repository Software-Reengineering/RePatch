# Development Container for RePatch

This Docker compose starts a Linux container including a desktop accessible through a web browser or RustDesk.
All dependencies for building and running the repatch tool are included in the container.
You must have installed [Docker](https://docs.docker.com/get-started/get-docker/) and [Git](https://git-scm.com/downloads) on your local system.

## Build the Docker Image

First clone the RePatch repository. RePatch is an extension of RefMerge - the original repository of this project is [Ellis et al.](https://github.com/ualberta-smr/RefMerge).
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

### RustDesk:

RustDesk is installed in the container and can be used to access the desktop using a separated app on your system.
*Please note that this makes the container's desktop also remotely accessible via an ID and password!*
Use KasmVNC to initially connect to the desktop, then open the RustDesk app from the desktop shortcut.
In order to connect to it, install the corresponding [client for your system](https://github.com/rustdesk/rustdesk/releases/tag/1.3.7).
Then enter the ID and one time password shown in the app of the container.
You can also set a permanent password by clicking on the pencil icon, then click: ```Unlock security settings``` (no password) and click ```Set permanent Password```.

To automate the connection process (without connecting to KasmVNC), you can do the following adjustments in the Dockerfile:
1. To automatically start RustDesk at container start, remove the comments (first ```#```) in the Dockerfile below ```AUTOSTART RUSTDESK```.
1. Moreover, you can also add more desktop resolutions by adding the according lines to the additional script in ```Dockerfiles/add_resolutions.sh```.
1. To apply the changes you have to delete the container and volume if already created (using the Docker desktop app or according [commands](https://www.digitalocean.com/community/tutorials/how-to-remove-docker-images-containers-and-volumes)), then rebuild the image as described above.
Be aware that this resets all changes wrt. the container, including the RustDesk ID and password.

After connecting with RustDesk you can change the desktop resolutions in toolbar options.
Moreover, you can set the keyboard to compatibility mode for correcting the keyboard layout.
I also recommend setting the image quality to```Optimize reaction time```.

## Running RePatch in the Dev-Container

Stopping the container would be comparable to a shut down of the OS.
This is also what Docker does if you shut down your host system or Docker itself; therefore, you may have to start the container after rebooting.
```
docker stop dev-container-repatch
```
Starting the container would be comparable to booting the OS.
```
docker start dev-container-repatch
```

The desktop has two links to start IntelliJ for the projects repatch and RefactoringMiner:
- IntelliJ IDEA: RePatch
- IntelliJ IDEA: RefactoringMiner

The corresponding projects are in the git folder in the home directory: ```/config/git/```
- ```/config/git/RePatch```
- ```/config/git/com.github.tsantalis.refactoringminer```
