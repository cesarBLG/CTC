JSOURCES = $(wildcard *.java) $(wildcard */*.java)
JCLASSES=$(patsubst %.java, %.class, $(JSOURCES))
JFLAGS=-g
JC=javac
JVM=java
JAR= gui.jar

all: $(JAR)
$(JAR): $(JCLASSES) jmanifest.txt
	jar cfm $(JAR) jmanifest.txt
	cd ../.. && zip -ur scrt/gui/$(JAR) $(patsubst %, 'scrt/gui/%', $(wildcard *.class) $(wildcard */*.class))


%.class: %.java
	javac $< -sourcepath ../.. -cp ../common/libscrt.jar

jmanifest.txt:
	echo "Main-Class: scrt.gui.GUI" > jmanifest.txt
	echo "Class-Path: ../common/libscrt.jar" >> jmanifest.txt

run: $(JAR)
	java -jar gui.jar

clean:
	rm $(wildcard *.class) $(wildcard */*.class)