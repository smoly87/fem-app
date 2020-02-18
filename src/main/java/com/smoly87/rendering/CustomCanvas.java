package com.smoly87.rendering;

import javax.swing.*;
import java.awt.*;

public class CustomCanvas extends JPanel {
    protected Java2DRenderer sceneRender;

    public CustomCanvas(Java2DRenderer sceneRender) {
        this.sceneRender = sceneRender;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(sceneRender.getImage() ,0,0,null);
    }

}