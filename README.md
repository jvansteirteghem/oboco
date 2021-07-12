# oboco

oboco is a server to help you read the books in your book collection (zip, cbz, cbr, rar, rar5).
oboco is short for "open book collection".

you can:
- read your books and book collections.
- search your books and book collections.
- download your books.
- manage your book marks.
- manage your users.

the backend is a rest api with token-based authentication.
the frontend is a single-page application (work in progress).

you should use oboco with [oboco for android](https://gitlab.com/jeeto/oboco-android).
[oboco for android](https://gitlab.com/jeeto/oboco-android) is a client for oboco.

## requirements

- java >= 8

## installation

- install [the latest release](https://gitlab.com/jeeto/oboco/-/jobs/artifacts/2a6971e0/download?job=release).

## configuration

you are required to configure "application.properties/security.authentication.secret" and "data.properties".

### logger

- configure application.properties
	- logger.path: the path of the logs. [^1]
	- logger.rootLevel: the root level of the logger: "FATAL", "ERROR", "WARN", "INFO", "DEBUG" or "TRACE".
	- logger.level: the level of the logger: "FATAL", "ERROR", "WARN", "INFO", "DEBUG" or "TRACE".

### server

- configure application.properties
	- server.port: the port of the server.

### ssl server

- create the ssl key store (server.jks) with tools/ssl.bat or tools/ssl.sh
	- dns: the dns of the ssl certificate.
	- ip: the ip of the ssl certificate.
	- password: the password of the ssl key store.
- add the ssl certificate authority (server-ca.pem) to the trust store
	- configure chrome
		- select "settings"
		- select "security"
		- select "manage certificates"
		- import server-ca.pem
- configure application.properties
	- server.ssl.port: the ssl port of the server.
	- server.ssl.keyStore.path: the path of the ssl key store. [^1]
	- server.ssl.keyStore.password: the password of the ssl key store.

### data

- configure application.properties
	- data.path: the path of the data (book pages). [^1]
- configure data.csv
	- page: the page of the book or "" (all book pages).
	- scaleType: the scale type: "DEFAULT", "FIT", "FILL" or "" (do not scale).
	- scaleWidth: the scale width (in pixels) or "" (do not scale).
	- scaleHeight: the scale height (in pixels) or "" (do not scale).
- configure data.properties
	- %ROOT_BOOK_COLLECTION%=%DATA_PATH%: the path of the data (books and book collections). you can add more than one path by using a ",". [^1]

### database

- add the lib of the driver of the database to the libs directory.
- configure application.properties
	- database.name: the name of the database: "DB2", "DB2400", "DB2390", "PostgreSQL", "MySQL5", "MySQL5InnoDB", "MySQLMyISAM", "Oracle", "Oracle9i", "Oracle10g", "Oracle11g", "SybaseASE15", "SybaseAnywhere", "SQLServer", "SQLServer2005", "SQLServer2008", "SAPDB", "Informix", "HSQL", "H2", "Ingres", "Progress", "Mckoi", "Interbase", "Pointbase", "Frontbase" or "Firebird".
	- database.driver: the driver of the database.
	- database.url: the url of the database: "jdbc:.."
	- database.user.name: the user name of the database.
	- database.user.password: the user password of the database.
	- database.connectionPool.size: the connection pool size of the database.
	- database.connectionPool.age: the connection pool age of the database (in seconds).
- configure application.ddl
- configure application.sql

### security

- configure application.properties
	- security.authentication.secret: the secret of the authentication.
	- security.authentication.idToken.age: the age of the id token of the authentication (in seconds).
	- security.authentication.refreshToken.age: the age of the refresh token of the authentication (in seconds).

### plugins

- configure application.properties
	- plugin.archive.archiveReaderPool.size: the size of the archive reader pool.
	- plugin.archive.archiveReaderPool.interval: the interval of the archive reader pool (in seconds).
	- plugin.archive.archiveReaderPool.age: the age of the archive reader pool (in seconds).
- configure plugins/enabled.txt
	- JDKArchivePlugin: supports zip.
	- JUnrarArchivePlugin: supports rar.
	- SevenZipJBindingArchivePlugin: supports zip, rar and rar5.
	- JDKHashPlugin: supports sha256.
	- JDKImagePlugin: supports jpg and png.

## usage

- start the server with application.bat or application.sh
- open your browser to http://%SERVER_ADDRESS%:%SERVER_PORT% or https://%SERVER_ADDRESS%:%SERVER_SSL_PORT%
- select "Web"
- log in
	- name: administrator
	- password: administrator

## faq

### can I install oboco on a raspberry pi?

yes, but you have to start oboco with additional parameters: "-Dos.arch=armv71"

### can I configure oboco to use another database?

yes, you can use: "DB2", "DB2400", "DB2390", "PostgreSQL", "MySQL5", "MySQL5InnoDB", "MySQLMyISAM", "Oracle", "Oracle9i", "Oracle10g", "Oracle11g", "SybaseASE15", "SybaseAnywhere", "SQLServer", "SQLServer2005", "SQLServer2008", "SAPDB", "Informix", "HSQL", "H2", "Ingres", "Progress", "Mckoi", "Interbase", "Pointbase", "Frontbase" or "Firebird".
you have to add the lib of the driver of your database to the libs directory.

- mysql
	- add the lib to the libs directory: https://mvnrepository.com/artifact/mysql/mysql-connector-java
	- configure application.properties:
		- database.name: "MySQL5InnoDB"
		- database.driver: "com.mysql.cj.jdbc.Driver"
		- database.url: "jdbc:mysql://address:port/database"
		- database.user.name: "name"
		- database.user.password: "password"
- postgresql
	- add the lib to the libs directory: https://mvnrepository.com/artifact/org.postgresql/postgresql
	- configure application.properties:
		- database.name: "PostgreSQL"
		- database.driver: "org.postgresql.Driver"
		- database.url: "jdbc:postgresql://address:port/database"
		- database.user.name: "name"
		- database.user.password: "password"
- ..

### can I reset the password of the "administrator" user?

yes. the password is hashed with a 12-round bcrypt and the password hash is stored in the user table of the database.

- reset the password of the "administrator" user to "administrator"
	- stop oboco
	- create application.ddl: ""
	- create application.sql: "update users set passwordhash = '$2a$12$msu32WtSMaQVCJsIDKCxkOTVOGRrncBjUe5x63GbY/RizCJ/zyFPC', updateDate = current_timestamp where name = 'administrator'"
	- start oboco

### can I debug oboco?

yes, but you have to start oboco with additional parameters: "-Xdebug -Xrunjdwp:transport=dt_socket,address=8500,server=y,suspend=y"

- eclipse
	- select "oboco-app"
	- select "debug as, debug configurations"
	- select "remote java application"
	- set "connection type=standard, host=localhost, port=8500"
	- select "debug perspective"

### can I create a frontend for oboco?

yes, oboco deploys the first "*.war" file in the web directory.

### can I deploy oboco in the cloud?

yes.

- [oboco-backend-frontend](https://gitlab.com/jeeto/oboco-backend-frontend) is the backend and the frontend of oboco (quarkus-native, angular).
- [oboco-backend](https://gitlab.com/jeeto/oboco-backend) is the backend of oboco (quarkus, quarkus-native).
- [oboco-backend for heroku](https://gitlab.com/jeeto/oboco-backend-heroku) is the backend of oboco for heroku (quarkus-native).
- [oboco-frontend](https://gitlab.com/jeeto/oboco-frontend) is the frontend of oboco (angular).
- [oboco-frontend for heroku](https://gitlab.com/jeeto/oboco-frontend-heroku) is the frontend of oboco for heroku (angular).

## license

mit license

[^1]: on windows you have to use \\\\ or / as path separator.