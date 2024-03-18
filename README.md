# Online Marketplace Restful API

## Project Description

This project is an implementation of an online marketplace RESTful API in Java. It provides endpoints for managing various aspects of an online marketplace, including user authentication, product management, orders, and more.

## How to Run Locally

 Clone the Repository: => git clone https://github.com/MunyinyaMaurice/Online-Marketplace-RestFull-AP-sI.git

 Extract Repository:
Extract the cloned repository to your preferred location on your local machine.

## Update Configuration:

 Navigate to src/main/resources directory.
Update the application.properties file with your database connection details, API keys, or any other required configurations.
Reload Maven Dependencies:
Ensure you are connected to the internet and reload Maven dependencies.

## Run the Application:

 Allocate and run the main file of the application.

## Access your EndPoint using swagger api 3 ui: => http://localhost:your-port-number/swagger-ui/index.htm

## Database Initialization:
 After running the main file, the database specified in the application.properties file will be created, and necessary tables will be populated. An admin user will be created with the following credentials:

Email: admin@user.com
Password: Admin123@



# Running Docker Image

## Ensure Maven CLI is Installed:
Make sure you have Maven Command Line Interface (CLI) installed on your system. You can verify this by running:=> mvn -v

## Navigate to Maven Lifecycle and Clean:
Navigate to your IDE or terminal where you can access the Maven lifecycle, and double-click on the clean phase to ensure any previous builds are removed.

## Navigate to Project Directory:
Open a terminal and navigate to the project directory: cd Online-Marketplace-RestFull-AP-sI

 Modify Docker Compose Configuration:
Navigate to the project home directory and locate the docker-compose.yml file. Edit this file to change any properties or configurations as needed for your environment.

## Clean the application:
Run the following command to clean the application before execution: => mvn clean install

## Package the Application:
Run the following command to package the application and create an executable JAR: => mvn package

## N.B:
     Masure your docker (app) in your local computer is up and running.

Run Docker Compose:
Once the JAR is created, you can run the Docker Compose command to start the services and the Dockerfile for the Java application: => docker-compose up

This command will read the docker-compose.yml file in your project directory and start the defined services, including the Docker image for the Java application.
 
