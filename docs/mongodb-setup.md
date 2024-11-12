# MongoDB Setup

## 1. Download MongoDB

Download the [MongoDB](https://www.mongodb.com/try/download/community) ZIP file and extract it.

## 2. Download MongoDB Shell

Download the [MongoDB Shell](https://www.mongodb.com/try/download/shell) ZIP file (required for latest mongodb versions) and extract it.

## 3. Set Environment Variables

Add the MongoDB bin path (`C:\dev\mongodb\bin`) to the system environment variables.

## 4. Create Data Directory

- Create the `data/db` directory in `C:/`:
  - Path: `C:\data\db`

## 5. Create Batch File for MongoDB

- Create a batch file (`start_mongodb_repl_set.bat`) to run MongoDB with a replica set and no authentication:
  ```bash
  mongod --replSet rs0 --dbpath C:\data\db
  pause
  ```

## 6. Replica Set Setup (Only One Time Required)

1.  Run the batch file `start_mongodb_repl_set.bat` previously created or the command in the terminal:
    ```bash
    mongod --replSet rs0 --dbpath C:\data\db
    ```
2.  Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) the follow the steps:

    2.1 Use the following connection string to connect to MongoDB:

    ```bash
    mongodb://localhost:27017/
    ```

    2.2 Create a replica set and check its status:

    ```bash
    rs.initiate()

    rs.status()
    ```

    2.3. Exit the MongoDB Shell, leaving the `mongod` terminal open.

**Note**: No additional action is required once the replica set is created.

## (Optional) Set Up Basic Authentication

1. Run the batch file or the command in the terminal:
   ```bash
   mongod --replSet rs0 --dbpath C:\data\db
   ```
2. Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) the follow the steps:

   2.1 Connect to MongoDB using the connection string:

   ```bash
   mongodb://localhost:27017/
   ```

   2.2 Create the 'admin' and 'root' users in the `admin` database:

   ```bash
   use admin

   db.createUser({
     user: "admin",
     pwd: "<ADMIN_PWD>",
     roles: [
       { role: "userAdminAnyDatabase", db: "admin" },
       { role: "dbAdminAnyDatabase", db: "admin" },
       { role: "readWriteAnyDatabase", db: "admin" },
       { role: "clusterAdmin", db: "admin" }
     ]
   })

   db.createUser({
     user: "root",
     pwd: "<ROOT_PWD>",
     roles: [
       { role: "root", db: "admin" }
     ]
   })
   ```

   **Note**: Replace `<ADMIN_PWD>` and `<ROOT_PWD>` with the desired passwords for 'admin' and 'root' users.

   2.3. Create the `demo_db` database and collections (`profile` and `profile_history`), and create a new user (`regular`) for the `demo_db`:

   ```bash
   use demo_db

   db.createCollection("profile")

   db.createCollection("profile_history")

   db.createUser({
     user: "regular",
     pwd: "<REGULAR_PWD>",
     roles: [
       { role: "readWrite", db: "demo_db" }
     ]
   })
   ```

   **Note**: Replace `<REGULAR_PWD>` with the desired password for 'regular' user.

   2.4. Exit the MongoDB Shell.

3. Generate a random sequence of 756 bytes using OpenSSL in the mongodb path (`C:\dev\mongodb`) and grant read permission only for the file owner:

   ```bash
   openssl rand -base64 756 > keyfile

   $ chmod 400 keyfile
   ```

4. Create the `mongod-auth.conf` file in the bin path (`C:\dev\mongodb\bin`) with the following content:

   ```yaml
   storage:
     dbPath: C:\data\db

   systemLog:
     destination: file
     path: C:\dev\mongodb\bin\logs\mongod.log
     logAppend: true

   net:
     port: 27017
     bindIp: 127.0.0.1

   security:
     authorization: enabled
     keyFile: C:\dev\mongodb\bin\auth\keyfile

   replication:
     replSetName: rs0
   ```

5. Create a new batch file (`start_mongodb_repl_set_auth.bat`) with the following content:

   ```bash
   mongod --config C:\dev\mongodb\bin\mongod-auth.conf
   pause
   ```

6. Close the running `mongod` terminal and run the new batch file `start_mongodb_repl_set_auth.bat` previously created or the command in the terminal:

   ```bash
   mongod --config C:\dev\mongodb\bin\mongod-auth.conf
   ```

7. Use an IDE like **MongoDB Compass** or **Studio 3T** to connect with the following connection string:

   ```bash
   mongodb://<DB_USER>:<DB_PASSWORD>@localhost:27017/?authSource=<DB_NAME>&replicaSet=rs0
   ```

## (Optional) Enable SSL/TLS

Refer to the [Enable SSL/TLS on MongoDB Server](https://github.com/erebelo/spring-mongodb-demo/blob/main/docs/ssl-tls-setup.md) documentation for step-by-step instructions on setting up secure connections. This includes generating SSL certificates, configuring MongoDB to use them, and ensuring secure communication between MongoDB instances and clients.

## (Optional) Index creation

1.  Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) the follow the steps:

    1.1 Use the following connection string to connect to MongoDB:

    ```bash
    mongodb://localhost:27017/
    ```

    1.2 Create indexes for `profile` and `profile_history` collections from `demo_db` database:

    ```bash
    use demo_db

    db.auth("regular", "<REGULAR_PWD>")

    db.createCollection("profile")

    db.createCollection("profile_history")

    db.profile.createIndex({userId: 1}, {unique: true, name: "user_id_index"})

    db.profile_history.createIndex({documentId: 1}, {name: "document_id_index"})

    db.profile_history.createIndex({"document.userId": 1}, {name: "user_id_index"})
    ```

    **Note**: Replace `<REGULAR_PWD>` with the desired password for 'regular' user.

    1.3. Exit the MongoDB Shell, leaving the `mongod` terminal open.
