package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.ItemObject;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;

import java.awt.*;
import java.util.Optional;

public class ItemObjectRendererPlugin implements RendererPlugin<ItemObject> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return ItemObject.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        ItemObject te = (ItemObject) e;
        if (Optional.ofNullable(te.font).isPresent()) {
            g.setFont(te.font);
        }
        int textWidth = g.getFontMetrics().stringWidth(te.text);
        int textHeight = g.getFontMetrics().getHeight();
        int tx2 = g.getFontMetrics().getDescent();
        int offsetX = 0;
        switch (te.textAlign) {
            case CENTER -> {
                offsetX = (int) (-0.5 * textWidth);
            }
            case LEFT -> {
                offsetX = 0;
            }
            case RIGHT -> {
                offsetX = -textWidth;
            }
        }

        if (te.highlight) {
            g.setColor(UIObject.mousePressedTextColor);
            for (int i = -2; i < 2; i++) {
                for (int j = -2; j < 2; j++) {
                    g.drawString(te.getText(), (int) te.getX() + i + offsetX, (int) te.getY() + j);
                }
            }
        }

        g.setColor(te.textColor);
        te.setSize(textWidth, textHeight);
        g.drawString(te.getText(), (int) te.getX() + offsetX, (int) te.getY());

        drawVisualDebugInformation(g, te, tx2 + offsetX);
    }

}
