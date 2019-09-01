SOURCES = $(wildcard *.cpp) $(wildcard */*.cpp)
OBJECTS=$(patsubst %.cpp, build/%.o, $(SOURCES))
DEPENDS=$(patsubst %.cpp, build/%.d, $(SOURCES))
HEADERS= $(wildcard *.h) $(wildcard */*.h)
CFLAGS = -I. -Wall -Wno-sign-compare
CFLAGS += -g -rdynamic -fpic
CXXFLAGS = $(CFLAGS)
LDFLAGS = -shared
LIB = libscrt.so
DESTDIR=.

JSOURCES = $(wildcard *.java) $(wildcard */*.java)
JCLASSES=$(patsubst %.java, build/scrt/common/%.class, $(JSOURCES))
JFLAGS=-g
JC=javac
JVM=java
JLIB = libscrt.jar

all: $(LIB) $(JLIB)

$(LIB): $(OBJECTS)
	$(CXX) $(LDFLAGS) -o $(LIB) $(OBJECTS)

build/%.d: %.cpp
	$(CXX) $(CFLAGS) -MM $< -MT $(patsubst %.cpp, build/%.o, $<) > $@
	echo "	$(CXX) $(CFLAGS) $< -c -o $(patsubst %.cpp, build/%.o, $<)" >> $@
	
include $(DEPENDS)

$(JLIB): $(JCLASSES) jmanifest.txt
	jar cfm $(JLIB) jmanifest.txt
	cd build && zip -ur ../$(JLIB) scrt
	cd ../.. && zip -ur scrt/common/$(JLIB) scrt/common/Images

build/scrt/common/%.class: %.java
	javac $< -d build -cp build -sourcepath ../..

jmanifest.txt:
	echo "" > jmanifest.txt

install:
	install -d $(DESTDIR)/bin/
	install $(LIB) $(DESTDIR)/bin
	install -d $(DESTDIR)/include
	install *.h $(DESTDIR)/include
	install -d $(DESTDIR)/include/packet
	install packet/*.h $(DESTDIR)/include/packet

clean:
	rm $(OBJECTS)
	rm $(DEPENDS)