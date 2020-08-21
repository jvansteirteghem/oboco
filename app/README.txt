configure application.properties:
- data.paths: paths to book collections

!! you have to escape \ or use / on windows
!! example: c:/data or c:\\data

create ssl keystore:
	- execute tools/ssh.bat or bash tools/ssl.sh
	- enter dns, ip (of certificate) and password (of keystores)

import application-ca.pem in android: http://lpains.net/articles/2018/install-root-ca-in-android/

execute application:
java -jar oboco-app-1.0.0-SNAPSHOT.jar
java -Xms256m -Xmx1024m -jar oboco-app-1.0.0-SNAPSHOT.jar
java -XX:+UseG1GC -XX:+UseStringDeduplication -Xms256m -Xmx1024m -jar oboco-app-1.0.0-SNAPSHOT.jar
// with additional libraries in libs
// windows
java -cp libs/*;oboco-app-1.0.0-SNAPSHOT.jar com.gitlab.jeeto.oboco.Server
// linux
java -cp libs/*:oboco-app-1.0.0-SNAPSHOT.jar com.gitlab.jeeto.oboco.Server

debug application:
java -Xdebug -Xrunjdwp:transport=dt_socket,address=8500,server=y,suspend=y -jar oboco-app-1.0.0-SNAPSHOT.jar
in eclipse: 
	- select "oboco-app"
	- select "debug as, debug configurations"
	- select "remote java application"
	- set "connection type=standard, host=localhost, port=8500"
	- open "debug perspective"

reset password of user administrator to administrator (bcrypt 12 rounds):
create empty application.ddl
create application.sql
	update users set password = '$2a$12$msu32WtSMaQVCJsIDKCxkOTVOGRrncBjUe5x63GbY/RizCJ/zyFPC', updateDate = current_timestamp where name = 'administrator'

open browser:
http://127.0.0.1:<server.port>/
or
https://127.0.0.1:<server.ssl.port>/

database:
MariaDB:
	copy driver to libs:
	https://mvnrepository.com/artifact/mysql/mysql-connector-java

	configure application.properties:
	database.name=MySQL5InnoDB
	database.driver=com.mysql.cj.jdbc.Driver
	database.url=jdbc:mysql://localhost:3306/oboco
	database.user.name=oboco
	database.user.password=oboco

PostgreSQL:
	copy driver to libs:
	https://mvnrepository.com/artifact/org.postgresql/postgresql

	configure application.properties:
	database.name=PostgreSQL
	database.driver=org.postgresql.Driver
	database.url=jdbc:postgresql://localhost:5432/oboco2
	database.user.name=oboco2
	database.user.password=oboco2

os:
Raspbian:
	https://www.raspberrypi.org/downloads/raspbian/
	
	sudo apt update
	sudo apt full-upgrade
	java -version
	# sudo apt install default-jdk
	java -Dos.arch=armv71 -jar oboco-1.0.0-SNAPSHOT.jar

Docker:
	directory:
		- oboco-1.0.0-SNAPSHOT.zip
		- application.properties
			data.paths=/data1,/data2
		- Dockerfile:
			FROM openjdk:8
			ARG OBOCO_VERSION
			ENV ENV_OBOCO_VERSION=${OBOCO_VERSION}
			COPY oboco-${OBOCO_VERSION}.zip /usr/src/oboco/oboco-${OBOCO_VERSION}.zip
			RUN unzip /usr/src/oboco/oboco-${OBOCO_VERSION}.zip -d /usr/src/oboco/
			RUN rm /usr/src/oboco/oboco-${OBOCO_VERSION}.zip
			COPY application.properties /usr/src/oboco/oboco-${OBOCO_VERSION}/application.properties
			WORKDIR /usr/src/oboco/oboco-${OBOCO_VERSION}
			ENTRYPOINT java -cp libs/*:oboco-app-${ENV_OBOCO_VERSION}.jar com.gitlab.jeeto.oboco.Server
	execute:
		docker build -f Dockerfile --build-arg OBOCO_VERSION=1.0.0-SNAPSHOT -t oboco/openjdk:8 .
		docker run -it --rm -p 8080:8080 -v c:/data1:/data1 -v c:/data2:/data2 --name oboco oboco/openjdk:8

developer info:

https://validator.w3.org/feed/docs/atom.html
https://specs.opds.io/opds-1.2
http://sevenzipjbind.sourceforge.net/first_steps.html
https://sourceforge.net/projects/sevenzipjbind/files/OldFiles/JDownloader-16.02-2.01beta-try1/
http://opds-validator.appspot.com/
https://github.com/vidal-community/atom-jaxb -> fr.vidal.oss.jaxb.atom.core
https://github.com/cassiomolin/jersey-jwt/
https://gist.github.com/psamsotha/bc732e897e65916422245295a9369737
https://gist.github.com/granella/01ba0944865d99227cf080e97f4b3cb6

keytool -genkey -alias test -keyalg RSA -keystore application.jks -keysize 2048

in dbvisualizer add new driver for h2 1.4: C:\Users\<user>\.m2\repository\com\h2database\h2\1.4.199\h2-1.4.199.jar
database file name: <path>\application

download and install node 10.16.3: https://nodejs.org/en/
check npm with: npm -v
install angular cli: npm install -g @angular/cli
create new angular project: ng new angular-project
run app: 
	cd angular-project
	ng serve --open
	
	ng serve --open --host 0.0.0.0 --disable-host-check