package com.smoly87.fem.core.boundaryconditions;
import static com.smoly87.fem.core.boundaryconditions.BoundaryConditionsIntervals.copy2dArray;
import static com.smoly87.fem.core.boundaryconditions.BoundaryConditionsIntervals.createIntervalRequests;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.stream.Collectors;

public class BoundaryConditionsProcessor {
    protected final BoundaryConditions boundaryCondition;

    public BoundaryConditionsProcessor(BoundaryConditions boundaryCondition) {
        this.boundaryCondition = boundaryCondition;
    }

    public RealMatrix applyBoundaryConditionsToLeftPart(RealMatrix KG) {
        int N = KG.getRowDimension();
        int B = boundaryCondition.getBoundaryNodesCount();
        double[][] K = KG.getData();

        List<IntervalQuery> intervalQueryList = createIntervalRequests(boundaryCondition, N);
        double[][] RData = copy2dArray(K, intervalQueryList, boundaryCondition.getDimCount(), B);
        return new Array2DRowRealMatrix(RData);
    }

    public RealVector applyBoundaryConditionsToRightPart(RealMatrix KG,
                                                            RealVector FG) {
        int N = FG.getDimension();
        final int rowCount = KG.getRowDimension();
        int B = boundaryCondition.getBoundaryNodesCount();

        Map<Integer, Double> fDelta = new HashMap<>();
        for(int i = 0; i < B; i++) {
            double[] variableInPointValues = boundaryCondition.getBoundValues(i);
            List<Integer> variableIndexesAbs = boundaryCondition.getBoundIndexAbs(i);
            for(int j = 0; j < boundaryCondition.getDimCount(); j++) {
                int bInd = variableIndexesAbs.get(j);
                for(int r = 0; r < rowCount; r++) {
                    fDelta.computeIfAbsent(r, (v) -> 0d);
                    fDelta.put(r, fDelta.get(r) - KG.getEntry(r, bInd) * variableInPointValues[j]) ;
                }
            }
        }

        Set<Integer> boundaryIndexes = boundaryCondition.getBoundIndexesAbs().stream().collect(Collectors.toSet());
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

    public double[] restoreBoundaryConditions(double[] Y) {
        int blockSize = boundaryCondition.getDimCount();
        int nodesCount = Y.length / blockSize;
        int boundaryNodesCount = boundaryCondition.getBoundaryNodesCount();
        int N = nodesCount + boundaryNodesCount;
        double[] R = new double[N];
        List<IntervalQuery> intervalQueryList = createIntervalRequests(boundaryCondition, N);

        double[] res = new double[N];
        int startInd = 0;
        int boundaryPointInd = 0;
        for(IntervalQuery query : intervalQueryList) {
            System.arraycopy(Y, query.getStart(), res, startInd, query.getLen());
            startInd += query.getLen();
            double[] curPointBoundaryValues = boundaryCondition.getBoundValues(boundaryPointInd);
            System.arraycopy(curPointBoundaryValues, 0, res, startInd, blockSize);
            startInd += blockSize;
            boundaryPointInd++;
        }
        return R;
    }

}
