
compile:
	-find . -name *.class | xargs rm
	-git pull
	javac -cp .:mysql-connector-java-5.1.35-bin.jar database/DbInteract.java
	javac -cp .:mysql-connector-java-5.1.35-bin.jar server/Server.java

run: compile
	java -cp .:mysql-connector-java-5.1.35-bin.jar server.Server
