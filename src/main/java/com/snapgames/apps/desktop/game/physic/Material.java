package com.snapgames.apps.desktop.game.physic;

import com.snapgames.apps.desktop.game.entity.Entity;

/**
 * <p>The {@link Material} object is used to set the material's constants used by an {@link Entity} during te physic processing.</p>
 *
 * <p>The material define first a name for a material, the density, the elasticity and the roughness for contact.</p>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Material {
    public static Material DEFAULT = new Material("default", 1.0, 1.0, 1.0);
    public String name;
    public double density;
    public double elasticity;
    public double roughness;

    public Material(String name, double d, double e, double r) {
        this.name = name;
        this.density = d;
        this.elasticity = e;
        this.roughness = r;
    }
}
