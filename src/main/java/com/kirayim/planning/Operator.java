package com.kirayim.planning;

import java.util.ArrayList;
import java.util.List;

class Operator {
    String name;

    double cost;

    List<Literal> precondition = new ArrayList<>();

    /**
     * List of altered predicates in new state;
     */
    List<Literal> newState = new ArrayList<>();

    public State preconditionState;
    public State changedState;

    public Operator(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public void setPreconditions(boolean type, Predicate... conds) {
        precondition.removeIf(p -> p.state() == type);

        for (Predicate pred : conds) {
            precondition.add(new Literal(pred, type));
        }
    }

    public void setStatePreds(boolean type, Predicate... conds) {
        newState.removeIf(p -> p.state() == type);

        for (Predicate pred : conds) {
            newState.add(new Literal(pred, type));
        }
    }

    public List<Literal> getPrecondition() {
        return precondition;
    }

    public void setPrecondition(List<Literal> precondition) {
        this.precondition = precondition;
    }

    public List<Literal> getNewState() {
        return newState;
    }

    public void setNewState(List<Literal> newState) {
        this.newState = newState;
    }

    public boolean checkPrecondition(long state) {
        return (state & preconditionState.mask()) == preconditionState.state();
    }

    public long updatedState(long initialState) {
        return (initialState & ~changedState.mask()) | (changedState.state() & changedState.mask());
    }


    @Override
    public String toString() {
        return "Operator [" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", changedState=" + changedState +
                ']';
    }
}
