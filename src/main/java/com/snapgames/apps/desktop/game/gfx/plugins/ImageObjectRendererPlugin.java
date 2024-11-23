package com.snapgames.apps.desktop.game.gfx.plugins;

import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ImageObject;

import java.awt.*;

public class ImageObjectRendererPlugin implements RendererPlugin<ImageObject> {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return ImageObject.class;
    }

    @Override
    public void draw(Graphics2D g, Entity e) {
        ImageObject io = (ImageObject) e;
        g.drawImage(
                io.getImage(),
                (int) io.getX(), (int) io.getY(),
                (int) io.getWidth(), (int) io.getHeight(),
                null);
    }
}
