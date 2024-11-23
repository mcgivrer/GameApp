package com.snapgames.apps.desktop.game.entity.ui;

import com.snapgames.apps.desktop.game.entity.TextObject;

/**
 * An {@link ItemObject} is an item in a {@link MenuObject}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class ItemObject extends TextObject implements UIObject {

    public boolean highlight = false;

    public ItemObject(String name) {
        super(name);
        setRelativeToCamera(true);
        setActive(true);
    }

    public ItemObject setValue(String value) {
        this.value = value;
        return this;
    }

    public void setHighLight(boolean b) {
        this.highlight = b;
    }
}
