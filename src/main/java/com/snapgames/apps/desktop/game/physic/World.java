package com.snapgames.apps.desktop.game.physic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * <p>The {@link World} object helps define the context where all the Entity's instances will evolve during loop.</p>
 *
 * <p>It defines te play area and the current gravity to be applied to all objects contained by the play area.</p>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class World {
    public String name = "default_world";
    public Rectangle2D playArea = new Rectangle2D.Double(0, 0, 640, 480);
    public double gravity = 0.981;
    public Material material = Material.DEFAULT;
    public Color playAreaColor = new Color(0.0f, 0.0f, 0.3f);


    public World(String name) {
        this.name = name;
    }

    public World(String name, double gravity, Rectangle2D.Double playArea, Material playAreaLimitMaterial) {
        this.name = name;
        this.gravity = gravity;
        this.playArea = playArea;
        this.material = playAreaLimitMaterial;
    }
}
