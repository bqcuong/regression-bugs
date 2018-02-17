# Builds and runs application (in Windows Powershell environment - recommend running this in Docker CLI window)
# To run, first set session's permission
#   Set-ExecutionPolicy -Scope CurrentUser Unrestricted
./mvnw install dockerfile:build
docker-compose up