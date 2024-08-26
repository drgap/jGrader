# jGrader
Program to facilitate automated grading of Java programs.

To use this program see the instructions in the [javadoc](https://cs.valdosta.edu/~dgibson/jGrader/doc/) for the TestEngine class. Instructions for writing tests are found in the TestSuite class.

[javadoc](https://cs.valdosta.edu/~dgibson/jGrader/doc_all/) for developer - all members

Notes:
1. The try/catch approach in the TestSuite class is tedious. It would be better to elevate it to the TestEngine class. However, the problem is that if the client code throws an exception (either by design or inadvertently) we lose the results arraylist that contains the test description and points. That in turn causes the grading method to bomb because that data is not present.
