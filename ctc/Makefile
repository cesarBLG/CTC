BUILDPATH=../build/scrt/ctc
SOURCES = $(wildcard *.cpp) $(wildcard */*.cpp) $(wildcard */*/*.cpp)
OBJECTS=$(patsubst %.cpp, $(BUILDPATH)/%.o, $(SOURCES))
DEPENDS=$(patsubst %.cpp, $(BUILDPATH)/%.d, $(SOURCES))
CFLAGS += -I../common -I. -Wall -Wno-sign-compare
LDFLAGS += -pthread -L.. -lscrt
CFLAGS += -g -rdynamic
#CFLAGS += -mwindows
#LDFLAGS += -static-libgcc -static-libstdc++ -Wl,-Bstatic -lstdc++ -lpthread -lwsock32 -Wl,-Bdynamic -lmingw32
CXXFLAGS = $(CFLAGS)

all: ../ctcd

../ctcd: $(OBJECTS)
	$(CXX) $(OBJECTS) -o ../ctcd $(LDFLAGS)

$(BUILDPATH)/%.d: %.cpp
	$(CXX) $(CFLAGS) -MM $< -MT $(patsubst %.cpp, $(BUILDPATH)/%.o, $<) > $@
	echo "	$(CXX) $(CXXFLAGS) $< -c -o $(patsubst %.cpp, $(BUILDPATH)/%.o, $<)" >> $@
include $(DEPENDS)

clean:
	rm $(OBJECTS)
	rm $(DEPENDS)
