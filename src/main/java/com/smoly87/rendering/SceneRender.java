package com.smoly87.rendering;



import java.awt.*;
import java.util.List;

public interface SceneRender {
    public void renderBodies(List<Body> bodiesList, Color color);
    public void drawLabels(List<CanvasPointWithLabel> pointsList) ;
    public void redraw();
    public void clear();
}
