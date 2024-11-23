package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.TextObject;

import java.awt.*;
import java.util.Optional;

public class TextObjectRendererPlugin implements RendererPlugin<TextObject> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return TextObject.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        TextObject te = (TextObject) e;
        g.setColor(te.textColor);

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
        te.setSize(textWidth, textHeight);
        g.drawString(te.getText(), (int) te.getX() + offsetX, (int) te.getY());

        drawVisualDebugInformation(g, te, tx2 + offsetX);
    }
}
