package com.snapgames.apps.desktop.game.behaviors;

import com.snapgames.apps.desktop.game.GameApp;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.TextObject;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;
import com.snapgames.apps.desktop.game.entity.util.Align;

/**
 * This {@link Behavior} implementation is used to automatically move child {@link TextObject} and {@link Button}
 * to their new position according to the {@link Align} attribute.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class AlignBehavior implements Behavior {
    @Override
    public void update(GameApp app, Entity e, double elapsed) {
        e.child.forEach(c -> {
            switch (c.getClass().getSimpleName()) {
                case "Button", "TextBox" -> {
                    switch (((Button) c).align) {
                        case LEFT -> {
                            c.x = e.x + UIObject.margin + UIObject.padding;
                            c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                        }
                        case RIGHT -> {
                            c.x = (e.x + e.width) - (c.width + UIObject.margin + UIObject.padding);
                            c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                        }
                        case CENTER -> {
                            c.x = (e.x + (e.width * 0.5)) - (UIObject.margin + UIObject.padding);
                            c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                        }
                        default -> {
                            // processing TOP,BOTTOM will come later...
                        }
                    }
                }
                default -> {
                    // nothing to do thaaaaaa....
                }
            }
        });
    }
}
