# Planning

This is a project to explore planning algorithms.

See [^1] for the background and some of the ideas used here and thanks to Airobotics Ltd., for lending me the book.

## Discrete planning problems

### Implementation
The initial implementation is of a 'discrete' type of planning problem. This is a problem where the state
of the world can be expressed as a set of boolean conditions (called predicates).

The world is considered to consist of a number of objects called Instances. These are not so important for the 
planning of a discrete problem with no functions.

The initial and goal states are set up with sets of true and false predicates (a predicate with a true/false state 
is called a Literal).

Operators perform some action on the world. They can only apply if a precondition (a set of literals) holds. 
The operator changes the state of some of the predicates in the world.

The various states are expressed as numbers where each bit represents the stat of a particular predicate. 
The predicates are stored in an ordered list to ensure proper translation from lists of predicates to these
numbers.

A structure called State contains a mask and a state word. This is used for expressing preconditions and changes
to the world.

This makes it easy to check preconditions and compare states. 
In this implementation Java 'longs' are used to store the states, thus limiting us to 64 predicates 
(or maybe 63 to prevent sign problems). However Java's BigInteger could conceivably be used, removing limits.

As suggested in [^1], a Dijkstra-like algorithm is used to find the shortest path through the state graph.
The state graph is expressed as vertices, each of which contains the relevant world state, the last operator which 
was used to reach this vertex, and the vertex on which the operator was run (Parent).

The graph is discovered incrementally while running the algorithm, by trying all the operators on the vertex 
currently being examined and seeing which match the preconditions.

Look at [^2] for similar ideas. I implemented the Theta* algorithm mentioned there for Airobotics, also in Java,
and I have adopted the experience gained in doing that for the implementation here. It was found that Java
is very efficient in running these kinds of algorithms probably due to the JIT compiler. The test problem here is 
not sufficient to exercise that.

In this implementation the sets of literals are set for each item, by two calls, one setting the true predicates
and one the false. These are stored as useful lists of literals, and later (when setting up the algorithm), 
they are converted to the binary representations.

For instance to set up the start state for the torch problem:
```java
        problem.addPredicates(battery1in, battery2in, capOn, switchOn);
        problem.setStartStatePreds(true, capOn);
        problem.setStartStatePreds(false, battery1in, battery2in);
```

### Test problem
The test problem "Torch problem" also comes from [^1]. In this problem we have a torch and two batteries.
The cap of the torch can be removed or replaced and the batteries can be removed or replaced. To add a litte
complexity, I also added an on-off switch (which is irrelevant to the problem)

We start with the cap on an batteries outside. The goal is to have the batteries inside and cap on.

The ttorch problem can be seen in the test tree in TestTorchProblem.java.

[^1]: Steven M. LaValle, Planning Algorithms, Cambridge University Press, 2006.

[^2]: Alex Nash, Sven Keonig, Maxim Likachev; Incremental Phi*: 
Incremental Any-Angle Path Planning on Grids; Lab Papers (GRASP); 2009-07-11
