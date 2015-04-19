
compile:
	-find . -name *.class | xargs rm
	-git pull
	javac -cp .:~/jdbc-driver/mysql-connector-java-5.1.35/mysql-connector-java-5.1.35-bin.jar server/Server.java

run: compile
	java -cp .:~/jdbc-driver/mysql-connector-java-5.1.35/mysql-connector-java-5.1.35-bin.jar server.Server
