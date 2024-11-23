package com.snapgames.apps.desktop.game.entity;

import com.snapgames.apps.desktop.game.entity.animation.Animations;

import java.awt.image.BufferedImage;

/**
 * A new Entity supporting Animations.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class AnimatedObject extends Entity {

    private Animations animations = new Animations();

    /**
     * Create a brand new {@link AnimatedObject} with its name.
     *
     * @param name name of this new {@link AnimatedObject}.
     */
    public AnimatedObject(String name) {
        super(name);
    }

    public Animations getAnimations() {
        return animations;
    }

    public BufferedImage getImage() {
        return animations.getImage();
    }

    public void update(double elapsed) {
        animations.update(elapsed);
    }
}
