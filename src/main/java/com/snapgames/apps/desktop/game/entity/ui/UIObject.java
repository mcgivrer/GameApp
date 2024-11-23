package com.snapgames.apps.desktop.game.entity.ui;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;

import java.awt.*;

/**
 * This interface defines internals parameters for any User Interface objects on screen.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public interface UIObject extends Behavior {
    /**
     * Default margin used for position and drawing of any UIObject
     */
    int margin = 2;
    /**
     * Default padding used for position and drawing of any UIObject
     */
    int padding = 2;

    /**
     * When mouse goes over a {@link UIObject}, the fill color for this hover object is set.
     */
    Color mouseOnColor = Color.LIGHT_GRAY;
    /**
     * When mouse goes over a {@link UIObject}, the border color for this hover object is set.
     */
    Color mouseOnBorderColor = Color.WHITE;

    /**
     * When a mouse button is pressed on a {@link UIObject}, the fill color for this object is set.
     */
    Color mousePressedColor = Color.CYAN;
    /**
     * When a mouse button is pressed on a {@link UIObject}, the text color for this object is set.
     */
    Color mousePressedTextColor = Color.BLUE;
    /**
     * When the mouse goes out of a {@link UIObject}, the fill color for this object is set.
     */
    Color mouseOutColor = Color.GRAY;
    /**
     * When the mouse goes out of a {@link UIObject}, the border color for this object is set.
     */
    Color mouseOutBorderColor = new Color(0.1f, 0.1f, 0.1f);

    /**
     * When a mouse button is released on a {@link UIObject}, the fill color for this object is set.
     */
    Color mouseReleasedColor = mouseOutColor;
    /**
     * When a mouse button is released on a {@link UIObject}, the text color for this object is set.
     */
    Color mouseReleasedTextColor = Color.WHITE;

    @Override
    default void onMousePressed(Game app, Entity e, double mouseX, double mouseY, int buttonId) {
        e.setFillColor(mousePressedColor);
        if (e instanceof Button || e instanceof ItemObject) {
            Button bt = (Button) e;
            bt.setTextColor(mousePressedTextColor);
        }
    }

    @Override
    default void onMouseReleased(Game app, Entity e, double mouseX, double mouseY, int buttonId) {
        e.setFillColor(mouseReleasedColor);
        if (e instanceof Button || e instanceof ItemObject) {
            Button bt = (Button) e;
            bt.setTextColor(mouseReleasedTextColor);
        }
    }

    @Override
    default void onMouseIn(Game app, Entity e, double mouseX, double mouseY) {
        e.setFillColor(mouseOnColor);
        e.setBorderColor(mouseOnBorderColor);
    }

    @Override
    default void onMouseOut(Game app, Entity e, double mouseX, double mouseY) {
        e.setFillColor(mouseOutColor);
        e.setBorderColor(mouseOutBorderColor);
    }
}
