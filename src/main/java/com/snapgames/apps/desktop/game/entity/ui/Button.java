package com.snapgames.apps.desktop.game.entity.ui;

import com.snapgames.apps.desktop.game.behaviors.AlignBehavior;
import com.snapgames.apps.desktop.game.entity.TextObject;
import com.snapgames.apps.desktop.game.entity.util.Align;

/**
 * A {@link Button} is a {@link UIObject} to capture mouse click action from the user.
 * <p>
 * It can be a child of a {@link DialogBox} and the DialogBox defined {@link AlignBehavior} will be applied on it
 * to define its position.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Button extends TextObject implements UIObject {
    public Align align;

    /**
     * Create a new {@link Button} named name with a default <code>align</code> set to LEFT.
     *
     * @param name the name for this new {@link Button} instance
     */
    public Button(String name) {
        super(name);
        setRelativeToCamera(true);
        setAlign(Align.LEFT);
    }

    /**
     * Set the Align value.
     *
     * @param a the new {@link Align} required for this {@link Button}.
     * @return the updated {@link Button} instance.
     */
    public Button setAlign(Align a) {
        this.align = a;
        return this;
    }

}
