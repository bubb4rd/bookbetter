# BookBetter 
CSE360 Project 
<br></br>
Group 30: Bo Hubbard, Wyatt Belscher, Amal Krishna, Jacky Leon Sanchez, Matheu Arenivas

### Admin login
admin | bookbetter1

## Contents
+ Prerequisites
+ Running the Project
+ Project Structure
+ Project Tasks
## Prerequisites
<ul>

* Selecting the right Java and JavaFX version
Your Java SDK version must be at least <Code>19.0.x</Code>
<br> Here is an acceptable <Code>19.0.2</Code>release https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html

* Connect the MySQL database
The MySQL database requires a driver to be connected to the Java project. In your Project Structure, add the Driver to your modules.

**Driver location**: 
<Code>com/example/cse360_project1/driver/mysql-connector-j-9.1.0/mysql-connector-j-9.1.0.jar</Code>

<br>

</ul>

IDEA build path: https://youtu.be/3tcIrNaxXxw

<br/>

Eclipse build path: https://youtu.be/fCDhLry_JW4


## Structure
The classes are split up based on how we interpret their uses in the project. 
### Models
Models are made up of the core objects of the project. The user model, book model, and a class for error handling.
### Controllers
These Java classes are used or implemented when we want to use a models attributes or variables in something like a **Scene**.
### Services
The services module is used to fetch and update data in the BookBetter MySQL Database. The server is hosted with AWS. <Code>JDBCConnection</Code> is the main service.  
## Running the project
There are two ways to run the project: Main and DevelopmentMode. 
<br><br>**Main** is the default option when executing the project. It takes the user to the login page before they are able to interact with the app.
<br>
**DevelopmentMode** is an experimental class, meaning you can launch the program from any scene you choose, as long as it is created with no errors. It logs in with a sample user and takes you to the appropriate scene.

## Tasks

 - [ ] Admin View
 - [ ] Seller View
 - [ ] Buyer View
 - [x] Login Page
   - [x] Database Connection

## MySQL Database Setup and Schema

 - User Schema
   -   users table
   -   books table
   -   book_collections table