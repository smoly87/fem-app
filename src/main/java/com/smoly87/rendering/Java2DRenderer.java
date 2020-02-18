package com.smoly87.rendering;

import com.google.inject.Inject;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;


public class Java2DRenderer implements SceneRender {

    double xToViewPortCoof;
    double yToViewPortCoof;
    RealMatrix viewPortMatrix;
    protected long t;
    protected long startT;
    protected CustomCanvas customCanvas;
    protected final ScreenConfig config;
    protected Graphics g;
    protected Image image;

    public Image getImage() {
        return image;
    }

    protected Integer[] worldCoordsToViewPort(Vector2D vec) {
      Vector3D viewPortCoord = new Vector3D(viewPortMatrix.multiply(MatrixUtils.vecToRealMatrix(MatrixUtils.createFrom2D(vec, 1d))).getColumn(0));
      return new Integer[]{(int)viewPortCoord.getX(), (int)viewPortCoord.getY()};
    }

    protected RealMatrix calculateViewPortMatrix() {
        xToViewPortCoof = (config.screenWidth()/(config.xMax() - config.xMin()));
        yToViewPortCoof = (config.screenHeight()/(config.yMax() - config.yMin()));
        RealMatrix A = MatrixUtils.diagRealMatrix(new double[]{xToViewPortCoof, -yToViewPortCoof, 0d });
        A.setEntry(0, 2, - config.xMin() * xToViewPortCoof);
        A.setEntry(1, 2, - config.yMin() * yToViewPortCoof);
        return A;
    }

    protected Polygon createPolygonFromBody(Body body) {
        List<Integer[]> vertList = body
                .getVertexes()
                .stream()
                .map(vert -> body.getAbsCoord(vert))
                .map(vertAbsCoords -> worldCoordsToViewPort(vertAbsCoords))
                .collect(Collectors.toList());
        return new Polygon(
                vertList.stream().map(viewPortCoords -> viewPortCoords[0]).mapToInt(x -> x).toArray(),
                vertList.stream().map(viewPortCoords -> viewPortCoords[1]).mapToInt(y -> y).toArray(),
                vertList.size());
    }

    public void renderBodies(List<Body> bodiesList, Color color) {
        g.setColor(color);
        for(Body body : bodiesList) {
            Polygon polygon = createPolygonFromBody(body);
            g.drawPolygon(polygon);
        }
    }

    public void drawLabels(List<CanvasPointWithLabel> pointsList) {
        int d = 6;
        for(CanvasPointWithLabel canvasPointWithLabel:pointsList) {
            Integer[] p = worldCoordsToViewPort(canvasPointWithLabel.getPosition());
            g.setColor(canvasPointWithLabel.getColor());
            g.fillOval(
                    (int)(p[0] - d/2),
                    (int)(p[1] -d/2)
                    ,d,d );
        }

    }

    public void clear() {
       image = new BufferedImage(config.screenWidth(), config.screenHeight(),BufferedImage.TYPE_INT_ARGB);
       g = image.getGraphics();
    }


    public void redraw() {
        g.setColor(Color.green);
        customCanvas.repaint();
    }

    @Inject
    public Java2DRenderer(ScreenConfig config) {

        this.config = config;
        viewPortMatrix = calculateViewPortMatrix();

        this.customCanvas =  new CustomCanvas(this);
        JFrame f = new JFrame("Panel Example");
        customCanvas.setBounds(0,0, config.screenWidth(),config.screenHeight());
        customCanvas.setBackground(Color.black);
        customCanvas.setVisible(true);
        f.add(customCanvas);
        f.setSize( config.screenWidth(), config.screenHeight());
        f.setLayout(null);
        f.setVisible(true);
    }
}
