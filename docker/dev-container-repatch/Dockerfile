# https://docs.linuxserver.io/images/docker-webtop/
# https://github.com/linuxserver/docker-webtop
# https://github.com/linuxserver/docker-webtop/releases
# FIXME: Memory crashes on WSL: dmesg -T | grep -i oom
FROM lscr.io/linuxserver/webtop:ubuntu-kde-951db35e-ls75

##### PORTS #####

# HTTP VNC
EXPOSE 3000
# HTTPS VNC
EXPOSE 3001

##### ENVIRONMENT VARIABLES #####

# User and Group ID
ENV PUID=1000
ENV PGID=1000

ENV HOME=/config

##### INSTALL PACKAGES #####

# Install additional tools: Git and the JDK ladder.
# JDK 17 builds and runs RePatch 2.0 (build.gradle pins a 17 toolchain).
# JDK 11 is still required: the evaluation project's model pins project
# SDK "11" (kafka-era .idea/misc.xml) and IntelliJ's SDK detector
# auto-registers any JDK found under /usr/lib/jvm; kafka's own gradle,
# and RefactoringMiner 2.1.0's gradle below, also predate JDK 17.
RUN apt-get update && apt-get install -y \
    nano \
	htop \
	dos2unix \
    git \
    openjdk-17-jdk \
    openjdk-11-jdk \
	maven \
	python3-full \
    wget \
    unzip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


RUN java -XshowSettings:properties -version
# Find the actual JAVA_HOME path dynamically
RUN echo "JAVA_HOME should be: $(dirname $(dirname $(readlink -f $(which java))))"

# Java: pin default-java to 17 and record 11's real path. Resolved by glob
# so this works on both amd64 and arm64 (…-openjdk-amd64 / -arm64). With two
# JDKs installed, detecting JAVA_HOME from `which javac` is ambiguous, so the
# 17 home is selected explicitly. JDK 11 stays under /usr/lib/jvm where
# IntelliJ's SDK detector finds it for the kafka-era project SDK.
RUN set -eux; \
    JDK17="$(echo /usr/lib/jvm/java-17-openjdk-*)"; \
    JDK11="$(echo /usr/lib/jvm/java-11-openjdk-*)"; \
    test -x "$JDK17/bin/javac"; \
    test -x "$JDK11/bin/javac"; \
    ln -sfn "$JDK17" /usr/lib/jvm/default-java; \
    ln -sfn "$JDK11" /usr/lib/jvm/java-11; \
    echo "export JAVA_HOME=/usr/lib/jvm/default-java" > /etc/profile.d/java_home.sh

ENV JAVA_HOME=/usr/lib/jvm/default-java
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Download and install RustDesk 1.3.7. 
# If you are running this on macOS or Windows, you may need to adjust the architecture in the download URL.
# For example, use `rustdesk-1.3.7-x86_64.deb` for x86_64 architecture.
# For ARM64 architecture, use `rustdesk-1.3.7-aarch64.deb`.
# The following command assumes you are using a Debian-based system (like Ubuntu).
# RUN apt-get update && apt-get install -y \
#     libxdo3 \
# 	gstreamer1.0-pipewire && \
# 	apt-get clean && \
# 	rm -rf /var/lib/apt/lists/*

# Install IntelliJ IDEA Community Edition 2024.3.7 — RePatch 2.0 declares
# sinceBuild/untilBuild 243, so 2020.1.2 would refuse to load the plugin.
# The extracted directory carries a build number (idea-IC-243.x.y), so it is
# discovered rather than hardcoded and exposed as the stable /opt/idea.
# JetBrains ships a separate aarch64 build; the unsuffixed tarball is x86_64
# only, so on an arm64 host (Apple Silicon) it would install an IDEA that
# cannot run. Pick the suffix from the build platform.
ARG IDEA_VERSION=2024.3.7
RUN set -eux; \
    case "$(uname -m)" in \
        aarch64|arm64) IDEA_ARCH='-aarch64' ;; \
        *)             IDEA_ARCH='' ;; \
    esac; \
    wget -q "https://download.jetbrains.com/idea/ideaIC-${IDEA_VERSION}${IDEA_ARCH}.tar.gz" -O /tmp/ideaIC.tar.gz; \
    mkdir -p /opt/idea-dist; \
    tar -xzf /tmp/ideaIC.tar.gz -C /opt/idea-dist --strip-components=1; \
    rm /tmp/ideaIC.tar.gz; \
    ln -sfn /opt/idea-dist /opt/idea; \
    ln -sfn /opt/idea/bin/idea.sh /usr/local/bin/intellij

##### REFACTORING MINER #####
	
# Prepare Git folder:
RUN mkdir -p $HOME/git

# Clone repository: RefactoringMiner
# RUN git clone --branch 2.1.0 https://github.com/tsantalis/RefactoringMiner.git $HOME/git/com.github.tsantalis.refactoringminer
RUN git clone --branch 2.1.0 https://github.com/manuelohrndorf/com.github.tsantalis.refactoringminer.git $HOME/git/com.github.tsantalis.refactoringminer

# Build repository: RefactoringMiner
WORKDIR $HOME/git/com.github.tsantalis.refactoringminer
# RefactoringMiner 2.1.0 ships an old gradle wrapper that cannot run on
# JDK 17, so this one build is pinned to JDK 11. The jar itself is a plain
# library and is consumed fine by the JDK 17 build.
RUN JAVA_HOME=/usr/lib/jvm/java-11 PATH="/usr/lib/jvm/java-11/bin:$PATH" ./gradlew jar

# Install RefactoringMiner in Maven as local libarary
USER $PUID
RUN mvn install:install-file -Dfile=$HOME/git/com.github.tsantalis.refactoringminer/build/libs/RefactoringMiner-2.1.0.jar -DgroupId=com.github.tsantalis -DartifactId=refactoring-miner -Dversion=2.1.0 -Dpackaging=jar -DgeneratePom=true
USER root

##### REPATCH #####

# Clone repository: repatch. Upstream unlv-evol/RePatch is still the 1.x
# code (gradle 6.8 / IDEA 2020.1.2) and would not build against the
# toolchain above, so the lab image tracks this repository's 2.0 branch.
# Override at build time with --build-arg REPATCH_REF=<branch|tag|sha>.
ARG REPATCH_REPO=https://github.com/Software-Reengineering/RePatch.git
ARG REPATCH_REF=RePatch-2.0-upgrade
RUN git clone --branch "$REPATCH_REF" "$REPATCH_REPO" $HOME/git/RePatch

# Setup Git identity for repatch
RUN git config --global user.email "you@example.com"
RUN git config --global user.name "Your Name"
RUN git config --global init.defaultBranch main



##### CREATE DESKTOP SHORTCUTS #####

# Clear default desktop shortcuts
RUN rm -rf /home/kasm-default-profile/Desktop

# Ensure the Desktop directory exists
RUN mkdir -p $HOME/Desktop
RUN mkdir -p $HOME/repatch-integration-projects

# Create a desktop shortcut for RustDesk
# RUN printf "[Desktop Entry]\n\
# Type=Application\n\
# Name=RustDesk\n\
# Exec=rustdesk %%u\n\
# Icon=rustdesk\n\
# Terminal=false\n\
# Categories=Network;RemoteAccess;\n" > $HOME/Desktop/rustdesk.desktop && \
#     chmod +x $HOME/Desktop/rustdesk.desktop

# Create a desktop shortcut for IntelliJ: RefactoringMiner
# RUN printf "[Desktop Entry]\n\
# Type=Application\n\
# Name=IntelliJ IDEA: RefactoringMiner\n\
# Exec=/usr/local/bin/intellij $HOME/git/com.github.tsantalis.refactoringminer\n\
# Icon=/opt/idea-IC-201.7846.76/bin/idea.png\n\
# Terminal=false\n\
# Categories=Development;\n" > $HOME/Desktop/intellij-refactoringminer.desktop && \
#     chmod +x $HOME/Desktop/intellij-refactoringminer.desktop

# Create a desktop shortcut for IntelliJ: repatch
RUN printf "[Desktop Entry]\n\
Type=Application\n\
Name=IntelliJ IDEA: RePatch\n\
Exec=/usr/local/bin/intellij $HOME/git/RePatch\n\
Icon=/opt/idea/bin/idea.png\n\
Terminal=false\n\
Categories=Development;\n" > $HOME/Desktop/intellij-repatch.desktop && \
    chmod +x $HOME/Desktop/intellij-repatch.desktop

##### AUTOSTART RUSTDESK #####

RUN mkdir -p $HOME/.config/my-scripts
RUN mkdir -p $HOME/.config/autostart

## RustDesk: Please note that this makes the container's desktop also remotely accessible via an ID and password!
#RUN printf "[Desktop Entry]\n\
#Type=Application\n\
#Name=RustDesk\n\
#Exec=/usr/bin/rustdesk %%u\n\
#StartupNotify=false\n\
#X-GNOME-Autostart-enabled=true\n" > $HOME/.config/autostart/rustdesk.desktop && \
#    chmod +x $HOME/.config/autostart/rustdesk.desktop

# Add resolution scripts	
COPY Dockerfiles/add_resolution.sh $HOME/.config/my-scripts/add_resolution.sh
COPY Dockerfiles/add_resolutions.sh $HOME/.config/my-scripts/add_resolutions.sh
RUN chmod +x $HOME/.config/my-scripts/add_resolution.sh && \
    chmod +x $HOME/.config/my-scripts/add_resolutions.sh && \
    dos2unix $HOME/.config/my-scripts/add_resolution.sh && \
	dos2unix $HOME/.config/my-scripts/add_resolutions.sh
RUN printf "[Desktop Entry]\n\
Type=Application\n\
Name=Resolution\n\
Exec=bash $HOME/.config/my-scripts/add_resolutions.sh\n\
StartupNotify=false\n\
X-GNOME-Autostart-enabled=true\n" > $HOME/.config/autostart/resolutions.desktop && \
    chmod +x $HOME/.config/autostart/resolutions.desktop

##### MOVE: /home/kasm-user > /config #####

# Workaround: during Docker build, e.g., Maven, uses /home/kasm-user but our abc user home directory is /config
RUN mv /home/kasm-user/* /home/kasm-user/.* $HOME/ 2>/dev/null || true

##### SET USER FOLDER PERMISSIONS #####

USER root
RUN chown -R $PUID:$PGID $HOME
USER $PUID
RUN chmod -R u+rw $HOME
USER root