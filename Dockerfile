# for deployment on ARM machines (e.g. Raspberry Pi)
# FROM arm32v7/openjdk:8-jdk-slim

# for deployment on x86_64 machines (e.g. Strato Server)
FROM amd64/openjdk:17-jdk-slim

WORKDIR /app
COPY build/libs/serverstatuschecker.jar /app/

COPY data /app/initial-data

RUN touch /app/.flag_docker

# Note: If this fails with "exec format error", make sure that Arm Emulation is enabled
#       Can be enabled by running the following command in terminal
#       docker run --rm -it --privileged torizon/binfmt

ENTRYPOINT ["java", "-jar", "serverstatuschecker.jar"]

EXPOSE 8080