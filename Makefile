#Add javacc binaries to the path
PATH := $(PATH):../javacc-5.0/bin

JJTREE=jjtree
JAVACC=javacc
JJDOC=jjdoc
JAVAC=javac
GRAMMAR=java1_7
OUTPUT=com/namal/reing

.PHONY: compile clean mrproper

compile:
	mkdir -p com
	mkdir -p com/namal
	mkdir -p $(OUTPUT)
	$(JJTREE) $(GRAMMAR).jjt
	$(JAVACC) $(GRAMMAR).jj
	$(JJDOC) $(GRAMMAR).jj
	cp -rf visitors/*.java .
	$(JAVAC) *.java
	mv $(GRAMMAR).jj $(OUTPUT)
	mv *.class $(OUTPUT)
	mv *.java $(OUTPUT)

clean:
	rm -rf $(OUTPUT)

mrproper: clean
	rm -rf *~ $(GRAMMAR).html
