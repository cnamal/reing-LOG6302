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
	cp -rf src/*.java .
	$(JAVAC) *.java
	mv $(GRAMMAR).jj $(OUTPUT)
	mv *.class $(OUTPUT)
	mv *.java $(OUTPUT)

test: compile
	java com.namal.reing.JavaParser1_7 @examples/filelist.txt

clean:
	rm -rf com

mrproper: clean
	rm -rf *~ $(GRAMMAR).html
