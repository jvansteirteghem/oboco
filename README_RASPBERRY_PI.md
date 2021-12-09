# oboco on the raspberry pi

## raspberry pi

- java
	- sudo apt update
	- sudo apt install default-jdk
	- java –version
- oboco
	- download oboco to /home/pi/Downloads
	- unzip /home/pi/Downloads/oboco-x.x.x.zip to /home/pi/Downloads/oboco
	- configure ssl
		- bash /home/pi/Downloads/oboco/tools/ssl.sh
			- DNS: the dns of the server
			- IP: the ip of the server
			- PASSWORD: the password of the ssl key store
		- copy /home/pi/Downloads/oboco/tools/server.pem to /home/pi/Downloads/oboco
	- configure data.properties
	- configure application.properties
		- server.ssl.port: the ssl port of the server
		- server.ssl.keyStore.path: the path of the ssl key store
		- server.ssl.keyStore.password: the password of the ssl key store
	- configure applicationService.sh
		- JAVA_OPTIONS: "-Dos.arch=armv71"
		- if data needs to be mounted and unmounted
			- DATA_DEVICE: "/dev/sda1"
			- DATA_DIRECTORY: "/media/pi/data"

## android device

- configure ssl
	- copy /home/pi/Downloads/oboco/tools/server-ca.pem to the device storage
	- select "settings"
	- select "biometrics and security"
	- select "other security settings"
	- select "install from device storage"
- raspi check
	- install
	- configure
		- add pi
		- add commands
			- oboco start: cd /home/pi/Downloads/oboco && bash applicationService.sh start
			- oboco stop: cd /home/pi/Downloads/oboco && bash applicationService.sh stop
			- oboco status: cd /home/pi/Downloads/oboco && bash applicationService.sh status
- oboco
	- install
	- configure

## debug

visualvm over ssh

remote:
- permissions.txt
	grant {
	  permission java.security.AllPermission;
	};
- jstatd -J-Djava.security.policy=permissions.txt

local:
- ssh -D 9696 user@remote
- visualvm.exe
	- tools > options > network > socks proxy: localhost 9696
	- file > add remote host: remote