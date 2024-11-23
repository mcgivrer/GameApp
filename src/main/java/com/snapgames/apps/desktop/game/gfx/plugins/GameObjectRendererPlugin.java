package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.GameObject;

import java.awt.*;

public class GameObjectRendererPlugin implements RendererPlugin<GameObject> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return GameObject.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        GameObject go = (GameObject) e;
        switch (go.nature) {
            case RECTANGLE, ELLIPSE, POLYGON -> {
                g.setColor(go.fillColor);
                g.fill(go.shape);
                g.setColor(go.borderColor);
                g.draw(go.shape);
            }
            case DOT -> {
                g.setColor(go.borderColor);
                g.drawLine(
                        (int) go.getX(), (int) go.getY(),
                        (int) go.getX(), (int) go.getY());
            }
            case LINE -> {
                g.setColor(go.borderColor);
                g.drawLine(
                        (int) go.getX(), (int) go.getY(),
                        (int) (go.getX() + go.getWidth()), (int) (go.getY() + go.getHeight()));
            }
        }
    }
}
