# Base: Node (Debian) with OpenJDK
FROM mcr.microsoft.com/devcontainers/javascript-node:22

# Install OpenJDK 21
RUN apt-get update && apt-get install -y openjdk-21-jdk && apt-get clean

# Set Java environment
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install common tools
RUN apt-get install -y git curl && apt-get clean