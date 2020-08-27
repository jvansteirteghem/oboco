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
the frontend is a single-page application (for now a basic proof-of-concept).

you should use oboco with [oboco for android](https://gitlab.com/jeeto/oboco-android).
[oboco for android](https://gitlab.com/jeeto/oboco-android) is a client for oboco.

## requirements

- java >= 8

## installation

- install [the latest build](https://gitlab.com/jeeto/oboco/-/jobs/artifacts/master/download?job=package).

## configuration

remark: on windows you have to use \\\\ or / as path seperator.

### user data (required)

- configure user.properties
	- data.path: the path of your book collection. you can add more than one path by using a ",".

### application logger

- configure application.properties
	- logger.path: the path of the logs.
	- logger.rootLevel: the root level of the logger: "FATAL", "ERROR", "WARN", "INFO", "DEBUG" or "TRACE".
	- logger.level: the level of the logger: "FATAL", "ERROR", "WARN", "INFO", "DEBUG" or "TRACE".

### application server

- configure application.properties
	- server.port: the port of the server.

### application ssl server

- create the ssl key store (server.jks) with tools/ssl.bat or tools/ssl.sh
	- dns: the dns of the ssl certificate.
	- ip: the ip of the ssl certificate.
	- password: the password of the ssl key store.
- add the ssl certificate authority (server-ca.pem) to the trust store
	- configure chrome
		- select "settings"
			- select "security"
				- select "manage certificates"
					- select "import trusted certificates"
- configure application.properties
	- server.ssl.port: the ssl port of the server.
	- server.ssl.keyStore.path: the path of the ssl key store.
	- server.ssl.keyStore.password: the password of the ssl key store.

### application data

- configure application.properties
	- data.path: the path of the data.
- configure data.csv
	- page: the page of the book or "" (all pages).
	- scaleType: the scale type: "DEFAULT", "FIT", "FILL" or "" (do not scale).
	- scaleWidth: the scale width (in pixels) or "" (do not scale).
	- scaleHeight: the scale height (in pixels) or "" (do not scale).
		
### application database

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

### application security (required)

- configure application.properties
	- security.authentication.secret: the secret of the authentication.
	- security.authentication.idToken.age: the age of the id token of the authentication (in seconds).
	- security.authentication.refreshToken.age: the age of the refresh token of the authentication (in seconds).

### application plugins

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
- open your browser to http://<server.address>:<server.port> or https://<server.address>:<server.ssl.port>
- log in
	- name: administrator
	- password: administrator

## license

mit license