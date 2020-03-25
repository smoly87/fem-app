package com.smoly87.fem.core.boundaryconditions;
import static com.smoly87.fem.core.boundaryconditions.BoundaryConditionsIntervals.copy2dArray;
import static com.smoly87.fem.core.boundaryconditions.BoundaryConditionsIntervals.createIntervalPairsRequests;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.stream.Collectors;

public class BoundaryConditionsProcessor {

    public RealMatrix applyBoundaryConditionsToLeftPart(RealMatrix KG,
                                                           BoundaryConditions boundaryCondition) {
        int N = KG.getRowDimension();
        int B = boundaryCondition.getBoundaryNodesCount();
        int size = N - B * boundaryCondition.getDimCount();
        double[][] K = KG.getData();

        List<IntervalQuery> intervalQueryList = createIntervalPairsRequests(boundaryCondition, N);
        double[][] RData = copy2dArray(K, intervalQueryList, boundaryCondition.getDimCount(), B);
        return new Array2DRowRealMatrix(RData);
    }

    protected RealVector applyBoundaryConditionsToRightPart(RealMatrix KG,
                                                            RealVector FG,
                                                            BoundaryConditions boundaryConditions) {
        int N = FG.getDimension();
        final int rowCount = KG.getRowDimension();
        int B = boundaryConditions.getBoundaryNodesCount();

        Map<Integer, Double> fDelta = new HashMap<>();
        for(int i = 0; i < B; i++) {
            double[] variableInPointValues = boundaryConditions.getBoundValues(i);
            List<Integer> variableIndexesAbs = boundaryConditions.getBoundIndexAbs(i);
            for(int j = 0; j < boundaryConditions.getDimCount(); j++) {
                int bInd = variableIndexesAbs.get(j);
                for(int r = 0; r < rowCount; r++) {
                    fDelta.computeIfAbsent(r, (v) -> 0d);
                    fDelta.put(r, fDelta.get(r) - KG.getEntry(r, bInd) * variableInPointValues[j]) ;
                }
            }
        }

        Set<Integer> boundaryIndexes = boundaryConditions.getBoundIndexesAbs().stream().collect(Collectors.toSet());
        double[] FN = new double[N - B];
        int r = 0;
        for(int i = 0; i < N; i++) {
            if (boundaryIndexes.contains(i)) continue;
            if (fDelta.containsKey(i)) {
                FN[r] = FG.getEntry(i) + fDelta.get(i);
            } else {
                FN[r] = FG.getEntry(i);

            }
            r++;
        }
        return new ArrayRealVector(FN);
    }

    public double[] removeBoundaryPoints(RealVector FG, BoundaryConditions boundaryConditions) {
        Set<Integer> boundaryIndexes = boundaryConditions.getBoundIndexesAbs().stream().collect(Collectors.toSet());
        int N = FG.getDimension();
        int B = boundaryConditions.getBoundaryNodesCount();
        double[] FN = new double[N - B];
        int r = 0;
        for(int i = 0; i < N; i++) {
            if (boundaryIndexes.contains(i)) continue;
            FN[r] = FG.getEntry(i);
            r++;
        }
        return FN;
    }

    public double[] restoreBoundaryConditions(double[] Y,
                                                BoundaryConditions boundaryConditions) {
        int blockSize = boundaryConditions.getDimCount();
        int nodesCount = Y.length / blockSize;
        int boundaryNodesCount = boundaryConditions.getBoundaryNodesCount();
        int N = nodesCount + boundaryNodesCount;
        double[] R = new double[N];
        List<IntervalQuery> intervalQueryList = createIntervalPairsRequests(boundaryConditions, N);

        double[] res = new double[N];
        int startInd = 0;
        int boundaryPointInd = 0;
        for(IntervalQuery query : intervalQueryList) {
            System.arraycopy(Y, query.getStart(), res, startInd, query.getLen());
            startInd += query.getLen();
            double[] curPointBoundaryValues = boundaryConditions.getBoundValues(boundaryPointInd);
            System.arraycopy(curPointBoundaryValues, 0, res, startInd, blockSize);
            startInd += blockSize;
            boundaryPointInd++;
        }
        return R;
    }

}
