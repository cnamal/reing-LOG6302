#Add javacc binaries to the path
PATH := $(PATH):../javacc-5.0/bin

JJTREE=jjtree
JAVACC=javacc
JJDOC=jjdoc
JAVAC=javac
GRAMMAR=java1_7
MODELS=$(notdir $(wildcard src/com/namal/reing/models/*.java))
MODELS_CLASS=$(patsubst %.java,%.class,$(MODELS))
VISITOR=$(notdir $(wildcard src/com/namal/reing/visitors/*.java))
VISITOR_CLASS=$(patsubst %.java,%.class,$(VISITOR))
OUPUTJ=$(notdir $(wildcard src/com/namal/reing/output/*.java))
OUPUT_CLASS=$(patsubst %.java,%.class,$(OUPUTJ))
OUTPUT=target/com/namal/reing

.PHONY: compile clean mrproper

compile: clean
	mkdir -p target
	mkdir -p target/com
	mkdir -p target/com/namal
	mkdir -p $(OUTPUT)
	mkdir -p $(OUTPUT)/models
	mkdir -p $(OUTPUT)/visitors
	mkdir -p $(OUTPUT)/output
	mkdir -p graphs
	$(JJTREE) $(GRAMMAR).jjt
	$(JAVACC) $(GRAMMAR).jj
	$(JJDOC) $(GRAMMAR).jj
	ls *.java | xargs -I{} cp {} src/com/namal/reing
	#cp -rf visitors/*.java .
	#cp -rf src/models/*.java .
	#cp -rf src/output/*.java .
	find src/ -name "*.java" | xargs -I{} cp {} .
	$(JAVAC) *.java
	
	mv $(MODELS) $(MODELS_CLASS) $(OUTPUT)/models
	mv $(VISITOR) $(VISITOR_CLASS) $(OUTPUT)/visitors
	mv $(OUTPUTJ) $(OUPUT_CLASS) $(OUTPUT)/output
	mv *.java *.class $(OUTPUT)

test: compile
	java -cp target com.namal.reing.JavaParser1_7 @examples/filelist.txt

project: compile
	java -cp target com.namal.reing.JavaParser1_7 @projects/zest-java.txt

clean:
	rm -rf com *.java *.class graphs/

mrproper: clean
	rm -rf *~ $(GRAMMAR).html
