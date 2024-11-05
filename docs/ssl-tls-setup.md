# Enable SSL/TLS on MongoDB Server

To enable SSL/TLS for MongoDB, follow these steps to generate the necessary certificates and configure MongoDB to use SSL/TLS.

## Step 1: Generate Certificates

1. **Generate the CA's Private Key and Certificate**

   ```bash
   openssl genrsa -out ca.key 4096
   openssl req -x509 -new -nodes -key ca.key -days 3650 -out ca.crt
   ```

   **NOTE**: During this process, you’ll be prompted to provide a **Common Name**; enter something like `mongo-ca-cert`.

2. **Generate MongoDB's Private Key and Certificate Signing Request (CSR)**

   ```bash
   openssl genrsa -out mongodb.key 4096
   openssl req -new -key mongodb.key -out mongodb.csr
   ```

   **NOTE**: When prompted for the **Common Name**, enter the appropriate value based on your environment. For local environments, use `localhost`. For other environments, use the IP address or domain name of the MongoDB server.

3. **Sign MongoDB's Certificate with the CA**

   ```bash
   openssl x509 -req -in mongodb.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out mongodb.crt -days 365 -sha256
   ```

4. **Combine MongoDB's Key and Certificate into a .pem File**

   ```bash
   cat mongodb.key mongodb.crt > mongodb.pem
   ```

5. **Set Correct Permissions**
   Ensure that the permissions on the certificates and keys are secure:
   ```bash
   chmod 600 mongodb.pem
   chmod 600 ca.crt
   ```

## Step 2: Create the PKCS12 Keystore for Java

To create a PKCS12 keystore compatible with Java's SSL/TLS configuration:

```bash
openssl pkcs12 -export -in mongodb.crt -inkey mongodb.key -out mongodb-keystore.p12 -name "mongo" -CAfile ca.crt -caname "mongo-ca" -password pass:<KEYSTORE_PASSWORD>
```

**NOTE**: Replace `<KEYSTORE_PASSWORD>` with your chosen password for the keystore.

Place the generated `mongodb-keystore.p12` file in the `/resources` directory of your Spring Boot application.

## MongoDB Configuration

Update the `mongod.conf` file as follows to configure MongoDB with SSL/TLS:

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
  tls:
    mode: requireTLS
    certificateKeyFile: C:\dev\mongodb\bin\tls\mongodb.pem
    CAFile: C:\dev\mongodb\bin\tls\ca.crt

security:
  authorization: disabled

replication:
  replSetName: rs0
```

## Start MongoDB with Replica Set and SSL/TLS

Use the following command in a batch file (`start_mongodb_repl_set_ssl.bat`) or directly in the command line to start MongoDB with the specified configuration:

```bash
mongod --config C:\dev\mongodb\bin\mongod.conf
pause
```

Now your MongoDB server is set up with SSL/TLS, and your Spring Boot application is ready to connect securely in non-local environments.

## Connecting to MongoDB with an IDE

To connect to your MongoDB server with SSL/TLS using an IDE like **MongoDB Compass** or **Studio 3T**, follow these steps:

### Configure SSL/TLS Connection Settings

Under the SSL tab:

- SSL: Check the option to enable SSL.
- SSL Certificate:
  - Root CA File: Choose the `ca.crt` file.
  - Client Certificate: Choose the `mongodb.pem` file.