package com.snapgames.apps.desktop.game.entity;

import com.snapgames.apps.desktop.game.Game;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A {@link GameObject} is an {@link Entity} with a specific {@link Shape}
 * according to its {@link GameObjectNature}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class GameObject extends Entity {

    public GameObjectNature nature = GameObjectNature.RECTANGLE;

    /**
     * Create a brand new {@link GameObject} with its name.
     *
     * @param name name of this new {@link GameObject}.
     */
    public GameObject(String name) {
        super(name);
    }

    public GameObject setNature(GameObjectNature n) {
        this.nature = n;

        return this;
    }

    @Override
    public Entity setPosition(double x, double y) {
        super.setPosition(x, y);
        setSize(width, height);
        return this;
    }

    @Override
    public void update(Game app, double elapsed) {
        super.update(app, elapsed);
        switch (nature) {
            case ELLIPSE -> {
                shape = new Ellipse2D.Double(x, y, width, height);
            }
            default -> {
                shape = new Double(x, y, width, height);
            }
        }
    }
}
