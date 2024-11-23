package com.snapgames.apps.desktop.game.entity.ui;

import com.snapgames.apps.desktop.game.behaviors.AlignBehavior;
import com.snapgames.apps.desktop.game.entity.TextObject;

import java.awt.*;

import static com.snapgames.apps.desktop.game.gfx.Renderer.buffer;

/**
 * A {@link DialogBox} entity will create a dialog with a text.
 * Adding child {@link Button} will add new operations
 * to activate some processing.
 * <p>
 * By default, a DialogBox is not active; it must be activated to be displayed.
 */
public class DialogBox extends TextObject implements UIObject {

    public DialogBox(String name) {
        super(name);
        setSize(100, 48);
        setPosition((buffer.getWidth() - this.width) * 0.5, (buffer.getHeight() - this.height) * 0.5);
        setVisible(false);
        setRelativeToCamera(true);
        setFillColor(Color.BLUE);
        setBorderColor(Color.CYAN);
        setTextColor(Color.WHITE);
        add(new AlignBehavior());
    }

    public void setVisible(boolean visible) {
        setActive(visible);
        setChildVisible(visible);
    }
}
