SOURCES = $(wildcard *.cpp) $(wildcard */*.cpp)
BUILDPATH=../build/scrt/common
OBJECTS=$(patsubst %.cpp, $(BUILDPATH)/%.o, $(SOURCES))
DEPENDS=$(patsubst %.cpp, $(BUILDPATH)/%.d, $(SOURCES))
HEADERS= $(wildcard *.h) $(wildcard */*.h)
CFLAGS = -I. -Wall -Wno-sign-compare
CFLAGS += -g -rdynamic -fpic
CXXFLAGS = $(CFLAGS)
LDFLAGS = -shared
LIB = ../libscrt.so
DESTDIR=.

all: $(LIB)

$(LIB): $(OBJECTS)
	$(CXX) $(LDFLAGS) -o $(LIB) $(OBJECTS)

$(BUILDPATH)/%.d: %.cpp
	$(CXX) $(CFLAGS) -MM $< -MT $(patsubst %.cpp, $(BUILDPATH)/%.o, $<) > $@
	echo "	$(CXX) $(CFLAGS) $< -c -o $(patsubst %.cpp, $(BUILDPATH)/%.o, $<)" >> $@
	
include $(DEPENDS)

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
