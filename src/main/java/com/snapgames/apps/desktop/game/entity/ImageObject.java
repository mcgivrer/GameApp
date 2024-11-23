package com.snapgames.apps.desktop.game.entity;

import java.awt.image.BufferedImage;

/**
 * An {@link ImageObject} is basically an {@link Entity} supporting {@link BufferedImage} drawing.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class ImageObject extends Entity {

    public BufferedImage image;

    /**
     * Create a brand new {@link ImageObject} with its name.
     *
     * @param name name of this new {@link ImageObject}.
     */
    public ImageObject(String name) {
        super(name);
    }

    public ImageObject setImage(BufferedImage img) {
        this.image = img;
        return this;
    }

    public BufferedImage getImage() {
        return this.image;
    }
}
