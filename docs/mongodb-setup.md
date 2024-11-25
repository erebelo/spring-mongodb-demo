# MongoDB Setup

This guide provides step-by-step instructions to set up MongoDB with replica sets, authentication, SSL/TLS, and indexing.

**NOTE**: This setup uses **MongoDB version 8**, which includes changes in documentation and configuration that may differ from other versions. For accurate guidance, refer to the [MongoDB v8 documentation](https://www.mongodb.com/docs/manual/).

## 1. Download MongoDB

Download the [MongoDB](https://www.mongodb.com/try/download/community) ZIP file and extract it.

## 2. Download MongoDB Shell

Download the [MongoDB Shell](https://www.mongodb.com/try/download/shell) ZIP file (required for latest mongodb versions) and extract it.

## 3. Set Environment Variables

Add the MongoDB bin path (`C:\dev\mongodb\bin`) to the system environment variables.

## 4. Create Data Directory

Create the `data/db` directory in `C:/`:

- Path: `C:\data\db`

## 5. Create Batch File for MongoDB

Create a batch file (`start_mongodb_repl_set.bat`) to run MongoDB with a replica set and no authentication:

```bash
mongod --replSet rs0 --dbpath C:\data\db
pause
```

## 6. Replica Set Setup (Only One Time Required)

1.  Run the batch file `start_mongodb_repl_set.bat` previously created or the command in the terminal:
    ```bash
    mongod --replSet rs0 --dbpath C:\data\db
    ```
2.  Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) then follow the steps:

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

**NOTE**: No additional action is required once the replica set is created.

## 7. Connect to MongoDB with an IDE

To connect to your MongoDB server using an IDE like **MongoDB Compass** or **Studio 3T**, follow these steps:

Connection String:

- Without Authentication:

  ```bash
  mongodb://localhost:27017/
  ```

- With Authentication:

  ```bash
  mongodb://<DB_USER>:<DB_PASSWORD>@localhost:27017/?authSource=<DB_NAME>&replicaSet=rs0
  ```

  **NOTE**: Replace `<DB_USER>`, `<DB_PASSWORD>`, and `<DB_NAME>` with the correct values.

## (Optional) Set Up Basic Authentication

1. Run the batch file or the command in the terminal:
   ```bash
   mongod --replSet rs0 --dbpath C:\data\db
   ```
2. Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) then follow the steps:

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

   **NOTE**: Replace `<ADMIN_PWD>` and `<ROOT_PWD>` with the desired passwords for 'admin' and 'root' users.

   2.3. Create the `demo_db` database and collections (`profiles` and `profiles_history`), and create a new user (`regular`) for the `demo_db`:

   ```bash
   use demo_db

   db.createCollection("profiles")

   db.createCollection("profiles_history")

   db.createUser({
     user: "regular",
     pwd: "<REGULAR_PWD>",
     roles: [
       { role: "readWrite", db: "demo_db" }
     ]
   })
   ```

   **NOTE**: Replace `<REGULAR_PWD>` with the desired password for 'regular' user.

   2.4. Exit the MongoDB Shell.

3. Generate a random sequence of 756 bytes using OpenSSL in the MongoDB bin path (`C:\dev\mongodb\bin`) and grant read permission only for the file owner:

   ```bash
   openssl rand -base64 756 > keyfile

   $ chmod 400 keyfile
   ```

   The `keyfile` is crucial for replica set authentication. It is shared between all members of the replica set to verify the identity of each node and secure internal communications. This key is not related to user authentication but is used to establish trust between replica set members.

4. Create the `mongod-auth.conf` file in the MongoDB bin path (`C:\dev\mongodb\bin`) with the following content:

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
     keyFile: C:\dev\mongodb\bin\keyfile

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

## (Optional) Enable SSL/TLS

Refer to the [Enable SSL/TLS on MongoDB Server](https://github.com/erebelo/spring-mongodb-demo/blob/main/docs/ssl-tls-setup.md) documentation for step-by-step instructions on setting up secure connections. This includes generating SSL certificates, configuring MongoDB to use them, and ensuring secure communication between MongoDB instances and clients.

## (Optional) Index creation

1.  Open the MongoDB Shell (`mongo.exe` or `mongosh.exe`) then follow the steps:

    1.1 Use the following connection string to connect to MongoDB:

    ```bash
    mongodb://localhost:27017/
    ```

    1.2 Create indexes for `profiles` and `profiles_history` collections from `demo_db` database:

    ```bash
    use demo_db

    db.auth("regular", "<REGULAR_PWD>")

    db.createCollection("profiles")

    db.createCollection("profiles_history")

    db.profiles.createIndex({userId: 1}, {unique: true, name: "user_id_index"})

    db.profiles_history.createIndex({documentId: 1}, {name: "document_id_index"})

    db.profiles_history.createIndex({"document.userId": 1}, {name: "user_id_index"})
    ```

    **NOTE**: Replace `<REGULAR_PWD>` with the correct password for 'regular' user.

    1.3. Exit the MongoDB Shell, leaving the `mongod` terminal open.
