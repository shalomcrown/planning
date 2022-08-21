package com.kirayim.planning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

public class TestTorchProblem {

    @Test
    public void testSimpleTorchProblem() {
        PlanningProblem problem = new PlanningProblem("Torch problem");

        Predicate battery1in = new Predicate("Battery 1 in");
        Predicate battery2in = new Predicate("Battery 2 in");
        Predicate capOn = new Predicate("Cap on");
        Predicate switchOn = new Predicate("Switch on");

        problem.addPredicates(battery1in, battery2in, capOn, switchOn);
        problem.setStartStatePreds(true, capOn);
        problem.setStartStatePreds(false, battery1in, battery2in);

        problem.setGoalStatePreds(true, capOn, battery1in, battery2in);

        Operator removeCap = new Operator("Remove cap", 1);
        removeCap.setPreconditions(true,  capOn);
        removeCap.setStatePreds(false, capOn);

        Operator closeCap = new Operator("Close cap", 1);
        closeCap.setPreconditions(false, capOn);
        closeCap.setStatePreds(true, capOn);

        Operator insertBatt1 = new Operator("Insert battery 1", 1);
        insertBatt1.setPreconditions(false, capOn, battery1in);
        insertBatt1.setStatePreds(true, battery1in);

        Operator insertBatt2 = new Operator("Insert battery 2", 1);
        insertBatt2.setPreconditions(false, capOn, battery2in);
        insertBatt2.setStatePreds(true, battery2in);

        Operator removeBatt1 = new Operator("Remove battery 1", 1);
        removeBatt1.setPreconditions(false, capOn);
        removeBatt1.setPreconditions(true, battery1in);
        removeBatt1.setStatePreds(false, battery1in);

        Operator removeBatt2 = new Operator("Remove battery 2", 1);
        removeBatt2.setPreconditions(false, capOn);
        removeBatt2.setPreconditions(true, battery2in);
        removeBatt2.setStatePreds(false, battery2in);

        Operator turnSwitchOn = new Operator("Switch on", 1);
        turnSwitchOn.setPreconditions(false, switchOn);
        turnSwitchOn.setStatePreds(true, switchOn);

        Operator turnSwitchOff = new Operator("Switch off", 1);
        turnSwitchOff.setPreconditions(true, switchOn);
        turnSwitchOff.setStatePreds(false, switchOn);

        problem.addOperators(removeCap, closeCap, insertBatt1, insertBatt2, removeBatt1, removeBatt2, turnSwitchOn, turnSwitchOff);

        long startTime = System.nanoTime();
        List<Vertex> results = problem.solveDijkstra();
        long finishTime = System.nanoTime();

        System.out.printf("Elapsed time: %f milliseconds\n", (finishTime - startTime) / 1.0E6);

        for (var v : results) {
            System.out.println(v.humanReadable);
        }

        assertEquals(5, results.size());
    }
}
