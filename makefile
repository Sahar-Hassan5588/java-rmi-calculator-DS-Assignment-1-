# Simple Makefile for Java RMI Calculator

all:
	javac *.java

run-server:
	java CalculatorServer

run-client:
	java CalculatorClient

run-tests:
	java SingleClientTest
	java MultiClientTest
	java EdgeStressTest

clean:
	rm -f *.class
