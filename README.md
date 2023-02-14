# Static Analysis for the JWhile Lang with Java

## Introduction
This is my implementation of the challenge proposed by  [rbonifacio](https://github.com/rbonifacio/) during the lectures about Algorithms and Complexity.
I used ANTLR4 to specify and parse the JWhile language which is very similiar to the While language proposed in book Principles Of Program Analysis, by Flemming Nielson et al. This is the summary of what has been implemented
- The monotone framework 
- Live Variable Analysis 
- Very Busy Expressions Analysis
- Reaching Definitions
- Available Expressions
- Interpreter for the jwhile

## JWhile
JWhile is just While with semicolons, braces and literal just as in java, but the variable type is not necessary. 
See the example shown below.
```java
    x=2;
    y=4;
    x=1;
    if(y>x){
        z=y;
    }else{
        z=y*y;
    };
    x=z;
```

## Monotone Framework
The following picture shows the monotone framework just as presented in the book Principles of Program Analysis.
![The Monotone Framework](images/mf.JPG)

The basic idea of this java implementation is that each algorithm should be parameterized based on the following items.
- The base class for all algorithms is the following class
  - ```java
      abstract class Algorithm<T extends AnalysisInformation<? extends Comparable<?>>> 
      ```
- Each algorithm should extend the base class specifing the focus of analysis (AnalysisInformation.java)
  - ```java
      //this is how ReachingDefinitions declares its focus of analysis
      class ReachingDefinitions extends Algorithm<AnalysisInformation<Pair<Label, String>>>
    ```
- Each algorithm must declare its general terms, by concrete implementations of the following methods
  - ```java
      // the initialization set
      public abstract Set<T> getInitSet();
      
      // enuns CONCERNS_ENTRY_CONDITIONS or CONCERNS_EXIT_CONDITIONS
      public abstract ConcernType getConcernType();
      
      // enuns FORWARD or BACKWARD
      public abstract FlowType getFlowType();
      
      public abstract Set<T> gen(Node n);
      public abstract Set<T> kill(Node n);
      
      // enuns MAY or MUST
      public abstract MeetOperator getMeetOperator();
      
      // sometimes init(S*) or someimes final(S*) - see picture below
      public abstract List<Node> getExtremeEdges();
      
      // sometimes, the superset is necessery so that it can be used to construct 
      // the so called "least solution"
      public abstract Set<T> findSuperset();
    ```
    
## Persistence
I've used a very different strategy than the one that is found in similar works.
After the Abstract Syntax Tree is provided by ANTLR4, a Graph is persisted into Neo4J database.
I named this graph as "Enriched Control Flow Graph", since it has more information than the usal control flow graph presented in the book.
In the following picture you can see that it is possible to find in the graph references to Identifiers, Literals and even to Trivial and Non Trivial Expressions.
The idea is that for larger projects, it shouldn't be enough to trust RAM memory as the backend for the Static Analysis.
Notice that if the analyzer crashes, everything is lost. Also, if you would want to analyze a software again, each time, the hole process should be taken.
By this, my motivation is to consider the possbility of using a backend as a repository for Analysis Information.

![Enriched CFG](images/ecfg.png)
