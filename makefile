# Simple Makefile for Java RMI Calculator

JUNIT=junit-platform-console-standalone-1.10.2.jar

all:
	javac CalculatorServer.java CalculatorClient.java

run-tests:
	java SingleClientTest
	java MultiClientTest
	java EdgeStressTest

test:
	java -jar $(JUNIT) --class-path . --scan-class-path

clean:
	rm -f *.class
