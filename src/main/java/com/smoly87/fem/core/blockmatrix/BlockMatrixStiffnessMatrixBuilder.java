package com.smoly87.fem.core.blockmatrix;

import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.Mesh;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class BlockMatrixStiffnessMatrixBuilder {

    public static RealMatrix fillGlobalStiffness(Mesh mesh,  SystemBlockMatrix S) {
        int N = mesh.getNodesCount() * S.getRowCount();
        RealMatrix M = new Array2DRowRealMatrix(N, N);
        ArrayList<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);
            M = arrangeInGlobalStiffnessElementMatrix(elem, M, S);
        }
        return M;
    }

    protected static RealMatrix arrangeInGlobalStiffnessElementMatrix(Element elem, RealMatrix M, SystemBlockMatrix S
                                                               ) {
        List<Integer> numsList = elem.getNodesList();
        int N = numsList.size();
        for(int l = 0; l < N; l++){
            for(int m = 0; m < N; m++){
                RealMatrix SLoc = S.calculateOnElement(elem, l, m);
                double[][] KLoc = SLoc.getData();
                int blockSize = KLoc.length;
                int i = numsList.get(l) * blockSize;
                int j = numsList.get(m) * blockSize;
                //M.setSubMatrix(KLoc, i, j);
                allocateBlock(M, KLoc, i, j);
            }
        }
        return M;
    }

    protected static void allocateBlock(RealMatrix M, double[][] KLoc, int startRow, int startCol) {
        int blockSize = KLoc.length;
        for (int i = 0 ; i <  blockSize; i++) {
            for (int j = 0; j <  blockSize; j++) {
                int r = i + startRow;
                int c = j + startCol;
                M.addToEntry(r, c, KLoc[i][j]);
            }
        }
    }
}
