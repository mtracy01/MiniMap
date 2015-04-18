
compile:
	-find . -name *.class | xargs rm
	-git pull
	javac server/Server.java

run: compile
	java server.Server
