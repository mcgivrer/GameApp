package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;

import java.awt.*;
import java.util.Optional;

public class ButtonRendererPlugin implements RendererPlugin<Button> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return Button.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        Button te = (Button) e;
        int x = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getX() + te.getX())
                : te.getX());

        int y = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getY() + te.getY())
                : te.getY());

        if (Optional.ofNullable(te.font).isPresent()) {
            g.setFont(te.font);
        }

        int fontHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(te.getText());
        int yOffset = g.getFontMetrics().getDescent();

        te.setSize(te.getWidth(), fontHeight + 2 * UIObject.margin);

        com.snapgames.apps.desktop.game.gfx.Renderer.drawEdgeRectangle(g, te);

        g.setColor(te.textColor);
        g.drawString(
                te.getText(),
                x + (int) ((te.getWidth() - textWidth) * 0.5) + UIObject.margin,
                y + UIObject.margin + fontHeight - yOffset);
    }
}
