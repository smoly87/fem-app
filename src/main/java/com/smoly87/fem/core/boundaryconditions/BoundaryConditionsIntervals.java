package com.smoly87.fem.core.boundaryconditions;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
// TODO: block size and the list of the conditions
public class BoundaryConditionsIntervals {
    public static double[][] copy2dArray(double[][] A, List<IntervalQuery> intervalQueryList, int blockSize, int condCount) {
        int startInd = 0;
        int size = A.length - blockSize * condCount;
        double[][] B = new double[size][size];
        for(IntervalQuery query : intervalQueryList) {
            for(int i = query.getStart(); i < query.getEnd(); i++) {
                B[startInd] = copyRow(A[i], intervalQueryList, blockSize, condCount);
                startInd++;
            }
        }
        return B;
    }

    public static double[] copyRow(double[] row, List<IntervalQuery> intervalQueryList, int blockSize, int condCount) {
        final int size = row.length - blockSize * condCount;
        double[] res = new double[size];
        int startInd = 0;
        for(IntervalQuery query : intervalQueryList) {
            System.arraycopy(row, query.getStart(), res, startInd, query.getLen());
            startInd += query.getLen();
        }
        return res;
    }

    public static List<IntervalQuery> createIntervalPairsRequests(BoundaryConditions boundaryConditions, int matrixSize) {
        ArrayList<Pair<Integer, Integer>> intervalsListPairs = createIntervalPairs(boundaryConditions, matrixSize);
        List<IntervalQuery> intervalQueryList = transformIntervalPairsToRequests(intervalsListPairs);
        return intervalQueryList;
    }

    public static List<IntervalQuery> transformIntervalPairsToRequests(ArrayList<Pair<Integer, Integer>> intervalsListPairs) {
        List<IntervalQuery> intervalQueriesList = new ArrayList<>();
        for(int i = 0; i < intervalsListPairs.size(); i++) {
            int from = intervalsListPairs.get(i).getFirst();
            int to = intervalsListPairs.get(i).getSecond();
            int len = to - from;

            if (i > 0) {
                from++;
            }

            if (i == intervalsListPairs.size() - 1) {
                len++;
            }
            intervalQueriesList.add(new IntervalQuery(from, to, len));
        }
        return intervalQueriesList;
    }

    public static ArrayList<Pair<Integer, Integer>> createIntervalPairs(BoundaryConditions boundaryConditions,
                                                                        int nodesCount) {
        int B = boundaryConditions.getBoundaryNodesCount();
        int N = nodesCount;

        List<Integer> intervalsList = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> intervalsListPairs = new ArrayList<>();
        intervalsList.add(0);
        for(int k = 0; k < B; k++){
            intervalsList.add(boundaryConditions.getBoundIndexAbs(k, 0));
        }
        intervalsList.add(N);

        for(int i = 0; i < B - 1; i++) {
            int start = intervalsList.get(i);
            int end = intervalsList.get(i + 1);
            if (start == end) continue;
            intervalsListPairs.add(Pair.create(start, end));
        }
        return intervalsListPairs;
    }
}
