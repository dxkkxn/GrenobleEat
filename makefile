##
# GrenobleEat
#
# @file
# @version 0.1

jline = jline-3.21.0.jar
jbdc = /usr/share/java/mariadb-jdbc/mariadb-java-client.jar

all: Main Demo
Main:
	javac -d bin -classpath bin/$(jline) -sourcepath src src/Main.java
Demo:
	javac -d bin -sourcepath src src/DBDemo.java
exec:
	java -classpath bin:$(jbdc):bin/$(jline) Main
demo:
	java -classpath bin:$(jbdc) DBDemo

# end
