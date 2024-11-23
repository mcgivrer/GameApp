package com.snapgames.apps.desktop.game.behaviors;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.physic.CollisionEvent;
import com.snapgames.apps.desktop.game.scene.Scene;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * <p>The {@link Behavior} interface allows new behaviors processing to any Entity when defining the Scene.</p>
 *
 * <p>Each behavior linked to an Entity will be processed during the 4 steps of the game loop:
 * <code>create</code>, <code>input</code>, <code>update</code> and <code>render</code>.</p>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public interface Behavior<T extends Entity> {
    /**
     * Create will help you customize the Entity creation.
     *
     * @param app
     */
    default void create(Game app, T e) {
    }

    /**
     * On a specific {@link Entity}, you can add input processing.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     */
    default void input(Game app, T e) {
    }

    /**
     * On a specific {@link Entity}, you can add enhance default update.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     */
    default void update(Game app, T e, double elapsed) {
    }

    /**
     * On a specific {@link Entity}, you can enhance the draw processing.
     *
     * @param app the parent application.
     * @param e   the concerned {@link Entity}.
     * @param g   the {@link Graphics2D} API to use.
     */
    default void draw(Game app, T e, Graphics2D g) {
    }

    /**
     * On a specific {@link Entity}, you can add key pressed processing.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     * @param k   the {@link KeyEvent} to be processed.
     */
    default void onKeyPressed(Game app, T e, KeyEvent k) {
    }

    /**
     * On a specific {@link Entity}, you can add key released processing.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     * @param k   the {@link KeyEvent} to be processed.
     */
    default void onKeyReleased(Game app, T e, KeyEvent k) {
    }

    /**
     * On activation of the {@link Entity}, this Behavior is processed.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     */
    default void onActivate(Game app, T e) {
    }

    /**
     * On deactivation of the {@link Entity}, this Behavior is processed.
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     */
    default void onDeactivate(Game app, T e) {
    }

    /**
     * On mouse entering the {@link Entity} area, this Behavior is processed.
     *
     * @param app    the parent application
     * @param e      the concerned {@link Entity}
     * @param mouseX mouse X position
     * @param mouseY mouse y position
     */
    default void onMouseIn(Game app, T e, double mouseX, double mouseY) {
    }

    /**
     * On mouse moving out of the {@link Entity} area, this Behavior is processed.
     *
     * @param app    the parent application
     * @param e      the concerned {@link Entity}
     * @param mouseX mouse X position
     * @param mouseY mouse y position
     */
    default void onMouseOut(Game app, T e, double mouseX, double mouseY) {
    }

    /**
     * On the mouse button clicked on the {@link Entity} area, this Behavior is processed.
     *
     * @param app      the parent application
     * @param e        the concerned {@link Entity}
     * @param mouseX   mouse X position
     * @param mouseY   mouse y position
     * @param buttonId the button number that has been clicked.
     */
    default void onMouseClick(Game app, T e, double mouseX, double mouseY, int buttonId) {
    }

    /**
     * On the mouse button pressed on the {@link Entity} area, this Behavior is processed.
     *
     * @param app      the parent application
     * @param e        the concerned {@link Entity}
     * @param mouseX   mouse X position
     * @param mouseY   mouse y position
     * @param buttonId the button number that has been clicked.
     */
    default void onMousePressed(Game app, T e, double mouseX, double mouseY, int buttonId) {
    }

    /**
     * On the mouse button released on the {@link Entity} area, this Behavior is processed.
     *
     * @param app      the parent application
     * @param e        the concerned {@link Entity}
     * @param mouseX   mouse X position
     * @param mouseY   mouse y position
     * @param buttonId the button number that has been clicked.
     */
    default void onMouseReleased(Game app, T e, double mouseX, double mouseY, int buttonId) {
    }

    /**
     * If the Entity is selected (e.g. ItemObject in a MenuObject)
     *
     * @param app the parent application
     * @param e   the concerned {@link Entity}
     */
    default void onSelected(Game app, T e) {
    }

    /**
     * This method is called when a collision occurs.
     *
     * @param app   the parent application
     * @param scene the current scene
     * @param other the other entity involved in the collision
     * @param ce    the collision event details
     */
    default void onCollide(Game app, Scene scene, T other, CollisionEvent ce) {
    }
}
