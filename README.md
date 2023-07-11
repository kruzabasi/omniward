# Omniward

OmniWard is a Clojure application for managing patient data. It provides a CRUD API for patient records and is built with Clojure and PostgreSQL.

## Requirements

Before running the application locally, make sure you have the following installed:

* Docker
* Clojure

## How to Run Locally

Follow the steps below to set up and run the OmniWard application locally:

1. Clone the GitHub repository:
```bash
git clone https://github.com/kruzabasi/omniward.git
```
2. Change into the project directory:
```bash
cd omniward
```
3. Start the PostgreSQL database:
```bash
docker-compose -f omniward.yaml up
```
This command will start the PostgreSQL container, which is required for running the application.
two new tables are created if they don't already exist: omniward and omniward_test.
Ensure that the database is running and accessible before proceeding.

PS: If UberJar and omniward docker image hasn't been built. 
You would have to comment out the api service in 'omniward.yaml' and then start the server from repl
```
5. Start the application in development mode:
```bash
clj -M:dev
```
This command starts the application in development mode. 
In the running REPL, execute:
```bash
(start-server)
```
to start the server. The project should now be running on port 8081.
Create the patients table:
In the REPL, execute the following command to create the patients table in the database:
```bash
(require '[omniward.postgres.db :as db])

(db/create-patients-table (get-db))
```
This step is necessary for managing patient records.

6. Run tests:
To run the tests for the OmniWard application, use the following command:
```bash
clj -M:test
```
This will execute the tests and provide feedback on the test results.

7. Build an Uber JAR:
To build an Uber JAR of the application, use the following command:
```bash
clj -T:build uber
```
This command will create a standalone JAR file that contains all the necessary dependencies to run the application.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests for any improvements or bug fixes.
