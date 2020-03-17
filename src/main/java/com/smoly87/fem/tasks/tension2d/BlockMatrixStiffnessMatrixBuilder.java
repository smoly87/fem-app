package com.smoly87.fem.tasks.tension2d;

import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.KlmFunction;
import com.smoly87.fem.core.Mesh;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class BlockMatrixStiffnessMatrixBuilder {

    public static RealMatrix fillGlobalStiffness(Mesh mesh, RealMatrix M, SystemBlockMatrix S) {
        ArrayList<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);

            M = arrangeInGlobalStiffnessElementMatrix(elem, M, S, elem.getNodesList());
        }

        return M;
    }

    protected static RealMatrix arrangeInGlobalStiffnessElementMatrix(Element elem, RealMatrix M, SystemBlockMatrix S,
                                                               List<Integer> numsList) {
        int N = numsList.size();
        for(int l = 0; l < N; l++){
            for(int m = 0; m < N; m++){
                RealMatrix SLoc = S.calculateOnElement(elem, l, m);
                double[][] KLoc = SLoc.getData();
                int i = numsList.get(l) * KLoc.length;
                int j = numsList.get(m) * KLoc[0].length;
                M.setSubMatrix(KLoc, i, j);
            }
        }
        return M;
    }
}
