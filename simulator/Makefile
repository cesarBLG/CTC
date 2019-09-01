JSOURCES = $(wildcard *.java)
JCLASSES=$(patsubst %.java, %.class, $(JSOURCES))
JFLAGS=-g
JC=javac
JVM=java
JAR= simulator.jar

all: $(JAR)

%.class: %.java
	javac $< -sourcepath ../.. -cp ../common/libscrt.jar

$(JAR): $(JCLASSES) jmanifest.txt
	jar cfm $(JAR) jmanifest.txt
	cd ../.. && zip -ur scrt/simulator/$(JAR) $(patsubst %, 'scrt/simulator/%', $(wildcard *.class) $(wildcard */*.class))

jmanifest.txt:
	echo "Main-Class: scrt.simulator.Simulator" > jmanifest.txt
	echo "Class-Path: ../common/libscrt.jar" >> jmanifest.txt

clean:
	rm $(JCLASSES)