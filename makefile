##
# GrenobleEat
#
# @file
# @version 0.1

jline = jline-3.21.0.jar

Main:
	javac -d bin -classpath bin/$(jline) -sourcepath src src/Main.java
exec:
	java -classpath bin:bin/$(jline) Main

# end
