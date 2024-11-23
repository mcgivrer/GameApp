package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.DialogBox;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;
import com.snapgames.apps.desktop.game.gfx.Renderer;

import java.awt.*;
import java.util.Optional;

public class DialogBoxRendererPlugin implements RendererPlugin<DialogBox> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return DialogBox.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        DialogBox db = (DialogBox) e;

        if (Optional.ofNullable(db.font).isPresent()) {
            g.setFont(db.font);
        }
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(db.getText());

        Renderer.drawEdgeRectangle(g, db);

        g.setColor(db.textColor);

        g.drawString(db.getText(), (int) (db.getX() + (db.getWidth() - textWidth) * 0.5 - UIObject.margin * 2),
                (int) (db.getY() + (db.getHeight() * 0.30) + UIObject.margin + UIObject.padding));
    }
}
