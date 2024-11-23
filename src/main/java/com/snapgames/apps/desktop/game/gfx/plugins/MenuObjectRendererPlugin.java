package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.MenuObject;
import com.snapgames.apps.desktop.game.gfx.Renderer;

import java.awt.*;
import java.util.Optional;

public class MenuObjectRendererPlugin implements RendererPlugin<MenuObject> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return MenuObject.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        MenuObject mo = (MenuObject) e;

        if (Optional.ofNullable(mo.font).isPresent()) {
            g.setFont(mo.font);
        }
        int textHeight = g.getFontMetrics().getHeight();
        g.setColor(mo.textColor);
        g.drawString(mo.getText(), (int) mo.getX(), (int) mo.getY());

        if (mo.backgroundColor != null) {
            g.setColor(mo.textColor);
            Renderer.drawEdgeRectangle(g, mo, mo.backgroundColor);
        }

        drawVisualDebugInformation(g, mo, mo.child.size() * mo.getFont().getSize());
    }
}
