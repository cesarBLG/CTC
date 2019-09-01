SOURCES = $(wildcard *.cpp) $(wildcard */*.cpp) $(wildcard */*/*.cpp)
OBJECTS=$(patsubst %.cpp, %.o, $(SOURCES))
DEPENDS=$(patsubst %.cpp, %.d, $(SOURCES))
CFLAGS += -I../common -I. -Wall -Wno-sign-compare
LDFLAGS += -pthread -L../common -lscrt
CFLAGS += -g -rdynamic
#CFLAGS += -mwindows
#LDFLAGS += -static-libgcc -static-libstdc++ -Wl,-Bstatic -lstdc++ -lpthread -lwsock32 -Wl,-Bdynamic -lmingw32
CXXFLAGS = $(CFLAGS)

all: ctcd

ctcd: $(OBJECTS)
	$(CXX) $(OBJECTS) -o ctcd $(LDFLAGS)

%.d: %.cpp
	$(CXX) $(CFLAGS) -MM $< -MT $(patsubst %.cpp, %.o, $<) > $@
include $(DEPENDS)

clean:
	rm $(OBJECTS)
	rm $(DEPENDS)