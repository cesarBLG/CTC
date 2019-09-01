SUBDIRS = common ctc simulator gui
.PHONY: $(SUBDIRS)
all: $(SUBDIRS)

$(SUBDIRS):
	$(MAKE) -C $@