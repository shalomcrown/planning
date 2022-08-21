package com.kirayim.planning;

import com.google.common.collect.Iterables;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

class PlanningProblem {
    String name;

    List<Instance> instances = new ArrayList<>();
    List<Predicate> predicates = new ArrayList<>();
    List<Operator> operators = new ArrayList<>();

    /**
     * List of relevant predicates in start state
     */
    List<Literal> startState = new ArrayList<>();

    State startStateRep;
    State goalStateRep;

    /**
     * List of relevant predicates in goal state
     */
    List<Literal> goalState = new ArrayList<>();

    private TreeSet<Vertex> openVertices = new TreeSet<Vertex>(PlanningProblem::openVerticesComparator);
    MutableLongObjectMap<Vertex> closedVertices = LongObjectMaps.mutable.empty();

    Vertex startVertex;
    Vertex goalVertex;

    public PlanningProblem(String name) {
        this.name = name;
    }

    public void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    public void addPredicates(Predicate... preds) {
        Arrays.stream(preds).forEach(predicates::add);
    }

    public void addOperators(Operator... ops) {
        Arrays.stream(ops).forEach(operators::add);
    }

    public void addOperator(Operator op) {
        operators.add(op);
    }

    public void setStartStatePreds(boolean type, Predicate... conds) {
        startState.removeIf(p -> p.state() == type);

        for (Predicate pred : conds) {
            startState.add(new Literal(pred, type));
        }
    }

    public void setGoalStatePreds(boolean type, Predicate... conds) {
        goalState.removeIf(p -> p.state() == type);

        for (Predicate pred : conds) {
            goalState.add(new Literal(pred, type));
        }
    }


    public static int openVerticesComparator(Vertex v1, Vertex v2) {
        int comparison = Double.compare(v1.value, v2.value);

        if (comparison == 0) {
            comparison = Long.compare(v1.state, v2.state);
        }

        return comparison;
    }


    public State stateFromLiteralsList(List<Literal> literals) {
        long mask = 0;
        long state = 0;

        for (Predicate pred : predicates) {
            Literal lit = Iterables.find(literals, p -> p.predicate() == pred, null);
            mask = mask << 1;
            state = state << 1;

            if (lit != null) {
                mask |= 1;
                state |= lit.state() ? 1 : 0;
            }
        }

        return new State(mask, state);
    }

    // ==============================================================================

    public String humanReadable(Vertex v) {
        StringBuffer buffer = new StringBuffer();


        if (v.howWeGotHere == null) {
            buffer.append("Start --> ");
        } else {
            buffer.append(v.howWeGotHere.name).append(" --> ");
        }

        long mask = 1 << (predicates.size() - 1);
        boolean first = true;

        for (Predicate pred : predicates) {
            if (first == true) {
                first = false;
            } else {
                buffer.append(", ");
            }

            buffer.append(pred.name()).append(":");

            if ((v.state & mask) == 0) {
                buffer.append("false");
            } else {
                buffer.append("true");
            }

            mask >>= 1;
        }

        return buffer.toString();
    }

    // ==============================================================================

    public void setup() {
        for (Operator op : operators) {
            op.preconditionState = stateFromLiteralsList(op.getPrecondition());
            op.changedState = stateFromLiteralsList(op.getNewState());
        }

        startStateRep = stateFromLiteralsList(startState);
        goalStateRep = stateFromLiteralsList(goalState);

        startVertex = new Vertex(startStateRep.state(), null, 0, null);
        goalVertex = new Vertex(goalStateRep.state(), startVertex, Double.MAX_VALUE, null);

        openVertices.add(startVertex);
        openVertices.add(goalVertex);
    }

    // ==============================================================================

    List<Vertex> solveDijkstra() {
        setup();

        while (openVertices.isEmpty() == false) {
            Vertex s = openVertices.pollFirst();

            if (s == goalVertex) {
                break;
            }

            closedVertices.put(s.state, s);

            for (Operator op : operators) {
                if (op.checkPrecondition(s.state)) {
                    long cadidateUpdatedState = op.updatedState(s.state);

                    if (closedVertices.containsKey(cadidateUpdatedState) == false) {
                        Vertex v = new Vertex(cadidateUpdatedState, s, s.value + op.cost, op);

                        var neighbor = Iterables.find(openVertices, p -> p.state == v.state, v);

                        if (v.value < neighbor.value) {
                            neighbor.value = v.value;
                            neighbor.parent = v.parent;
                            neighbor.howWeGotHere = op;
                            openVertices.remove(neighbor);
                        }

                        openVertices.add(neighbor);
                    }
                }
            }
        }


        List<Vertex> results = new ArrayList<>();

        for (Vertex v = goalVertex; v != null; v = v.parent) {
            results.add(0, v);
            v.humanReadable = humanReadable(v);
        }

        return results;
    }
}
