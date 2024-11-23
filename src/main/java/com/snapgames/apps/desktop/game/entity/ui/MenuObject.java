package com.snapgames.apps.desktop.game.entity.ui;

import com.snapgames.apps.desktop.game.GameApp;
import com.snapgames.apps.desktop.game.behaviors.AlignBehavior;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.TextObject;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.snapgames.apps.desktop.game.gfx.Renderer.buffer;

/**
 * A {@link MenuObject} is a choice selector between multiple items.
 * {@link Entity#child} list will be menu entry item {@link ItemObject}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class MenuObject extends TextObject implements UIObject {

    public Color backgroundColor;
    private int itemIndex = 0;
    private Object selectedValue = null;

    /**
     * Create a new {@link MenuObject} with a name.
     *
     * @param name the Name of this new {@link MenuObject}.
     */
    public MenuObject(String name) {
        super(name);
        setBackgroundColor(null);
        setSize(100, 48);
        setPosition((buffer.getWidth() - this.width) * 0.5, (buffer.getHeight() - this.height) * 0.5);
        setRelativeToCamera(true);
        setActive(true);
        setFillColor(Color.BLUE);
        setBorderColor(Color.CYAN);
        setTextColor(Color.WHITE);
        setText("");
        add(new AlignBehavior());
        add(new Behavior() {
            @Override
            public void onKeyReleased(GameApp app, Entity e, KeyEvent k) {
                MenuObject mo = (MenuObject) e;
                switch (k.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> {
                        mo.itemIndex = Math.min(itemIndex + 1, child.size() - 1);
                    }
                    case KeyEvent.VK_UP -> {
                        mo.itemIndex = Math.max(itemIndex - 1, 0);
                    }
                    case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                        ItemObject io = ((ItemObject) child.get(itemIndex));
                        mo.selectedValue = io.getValue();

                        mo.behaviors.forEach(b -> b.onSelected(app, mo));
                    }
                }
            }
        });
    }

    public MenuObject setBackgroundColor(Color bckColor) {
        this.backgroundColor = bckColor;
        return this;
    }

    public MenuObject addItem(ItemObject item) {
        item.setPosition(
                this.x + UIObject.padding + UIObject.margin,
                this.y + UIObject.padding + UIObject.margin + (child.size() + 1.25f) * 14);
        item.setFont(getFont());
        child.add(item);
        return this;
    }

    @Override
    public void update(GameApp app, double elapsed) {
        super.update(app, elapsed);
        for (int i = 0; i < child.size(); i++) {
            ((ItemObject) child.get(i)).setHighLight(i == itemIndex);
        }
    }

    public int getItemIndex() {
        return itemIndex;
    }
}
