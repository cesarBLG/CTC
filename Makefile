SUBDIRS = common ctc
.PHONY: $(SUBDIRS) java
all: $(SUBDIRS) java

$(SUBDIRS):
	$(MAKE) -C $@
	
java:
	ant
