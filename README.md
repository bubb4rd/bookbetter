# BookBetter 
CSE360 Project 
<br></br>
Group 30: Bo Hubbard, Wyatt Belscher, Amal Krishna, Jacky Leon Sanchez, Matheu Arenivas
## Contents
+ Prerequisites
+ Running the Project
+ Project Structure
## Prerequisites
<ul>

* Selecting the right Java and JavaFX version
Your Java SDK version must be at least <Code>19.0.x</Code>
<br> Here is an acceptable <Code>19.0.2</Code>release https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html
<br>
* Connect the MySQL database

    The MySQL database requires a driver to be connected to the Java project. In your Project Structure, add the Driver to your modules.
<br>
<br>
**Driver location** 
<Code>com/example/cse360_project1/driver/mysql-connector-j-9.1.0/mysql-connector-j-9.1.0.jar</Code>

<br>

</ul>

## Structure
The classes are split up based on how we interpret their uses in the project. 
### Models
Models are made up of the core objects of the project. The user model, book model, and a class for error handling.
### Controllers
These Java classes are used or implemented when we want to use a models attributes or variables in something like a **Scene**.
### Services
The services module is used to run the project. See "Running the project"
## Running the project
There are two ways to run the project. Main and DevelopmentMode. 
<br>**Main** is the default option when executing the project. It takes the user to the login page before they are able to interact with the app.
<br>
**DevelopmentMode** is an experimental class, meaning you can launch the program from any scene you choose, as long as it is created with no errors. It logs in with a sample user and takes you to the appropriate scene.