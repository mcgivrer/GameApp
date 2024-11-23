package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.GameApp;
import com.snapgames.apps.desktop.game.entity.Entity;

import java.awt.*;

/**
 * The rendering plugin architecture use RendererPlugin implemntation to draw any object on screen.
 * Any renderer will implment the 2 required methods from the interface:
 *
 * <ul>
 *     <li><code>getEntityClass()</code> that will return the class signature of the drawn object,</li>
 *     <li><code>draw(Graphics2D,Entity)</code> the corresponding implementation of the
 * drawing process for the corresponding Entity class.</li>
 * </ul>
 *
 * @param <T> the parametrized class  corresponding to the entity inherited implementation to be drawn.
 * @author Frédéric Delorme
 * @see com.snapgames.apps.desktop.game.gfx.Renderer
 * @since 1.0.0
 */
public interface RendererPlugin<T> {

    /**
     * return the  class of the drawn object inheriting from {@link Entity}.
     *
     * @return the Class of the Entity this plugin will draw.
     */
    Class<? extends Entity> getEntityClass();

    /**
     * Implementation of the draw process for this {@link Entity}.
     *
     * @param g the {@link Graphics2D} API instance to use to draw.
     * @param e the {@link Entity} instance to be drawn.
     */
    void draw(Graphics2D g, Entity e);

    default void drawVisualDebugInformation(Graphics2D g, Entity te, int offsetX) {
        if (GameApp.debug > 2) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(8.0f));
            g.drawString("#" + te.id + ":" + te.name, (int) te.getX() - 10, (int) te.getY() - 10);
            g.drawRect(
                    (int) te.getX() + offsetX, (int) (te.getY() - te.getHeight()),
                    (int) te.getWidth(), (int) te.getHeight());
        }
    }
}
