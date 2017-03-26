JFLAGS = -cp
JAR = ./:./lib/*
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $(JAR) $*.java

CLASSES = \
	Tester.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class ./*/*.class *.db *.lg