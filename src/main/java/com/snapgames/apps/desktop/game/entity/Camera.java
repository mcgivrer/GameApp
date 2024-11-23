package com.snapgames.apps.desktop.game.entity;

import java.awt.geom.Rectangle2D;
import java.util.Optional;

/**
 * <p>The {@link Camera} object will be used to track a {@link Camera#target}</p>
 * <p>
 * The targeted {@link Entity} will be keep on the display center according the {@link Camera#tweenFactor}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Camera extends Entity {
    private Entity target;
    private double tweenFactor;
    private final Rectangle2D viewport = new Double();

    public Camera(String name) {
        super(name);
    }

    public Camera setTarget(Entity target) {
        this.target = target;
        return this;
    }

    public Camera setTweenFactor(double tf) {
        this.tweenFactor = tf;
        return this;
    }

    public void update(double dt) {
        if (Optional.ofNullable(target).isPresent()) {
            this.x += Math.ceil(
                    (target.getX() + (target.getWidth() * 0.5) - ((viewport.getWidth()) * 0.5) - this.getX())
                            * tweenFactor * Math.min(dt, 1));
            this.y += Math.ceil(
                    (target.getY() + (target.getHeight() * 0.5) - ((viewport.getHeight()) * 0.5) - this.getY())
                            * tweenFactor * Math.min(dt, 1));
            this.viewport.setRect(this);
        }
    }
}
