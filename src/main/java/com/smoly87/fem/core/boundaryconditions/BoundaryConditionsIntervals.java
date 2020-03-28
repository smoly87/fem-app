package com.smoly87.fem.core.boundaryconditions;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static  List<IntervalQuery>  createIntervalRequests(BoundaryConditions boundaryConditions,
                                                                        int nodesCount) {
        List<IntervalQuery> queryList = new ArrayList<>();
        int N = nodesCount / boundaryConditions.getDimCount();
        boolean prevNodeInBound = true;
        int start = -1;
        Set<Integer> pointsIndInBound = boundaryConditions
                .getBoundIndexes()
                .stream()
                .collect(Collectors.toSet());

        int blockSize = boundaryConditions.getDimCount();
        for(int i = 0 ; i < N; i++) {
            boolean curNodeInBound = pointsIndInBound.contains(i);
            // Start of the interval
            if (prevNodeInBound && !curNodeInBound) {
                start = i;
            }
            // End of interval, type of interval includes begin and - math notation []
            if (curNodeInBound && !prevNodeInBound) {
                IntervalQuery query = createAbsIntervalQuery(start, i , blockSize); // TODO:test i - 1 ?
                queryList.add(query);
            }
            prevNodeInBound = curNodeInBound;
        }
        // Need to fix
        if (!pointsIndInBound.contains(N - 1)) {
            queryList.add(createAbsIntervalQuery(start, N - 1, blockSize));
        }
        return queryList;
    }

    private static IntervalQuery createAbsIntervalQuery(int startPoint, int endPoint, int blockSize) {
        IntervalQuery query = new IntervalQuery(startPoint * blockSize, (endPoint ) * blockSize);
        return query;
    }
}
