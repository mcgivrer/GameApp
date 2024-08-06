package com.snapgames.apps.scenes;

import com.snapgames.apps.Demo01Frame;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.snapgames.apps.Demo01Frame.getResource;
import static com.snapgames.apps.Demo01Frame.messages;

public class TitleScene extends Demo01Frame.AbstractScene {


    Font scoreFont;
    Font textFont;

    /**
     * Create a new {@link TitleScene} with a <code>name</code> and a parent <code>app</code>.
     *
     * @param app  the parent application {@link Demo01Frame}
     * @param name thename opf this new {@link TitleScene}
     */
    public TitleScene(Demo01Frame app, String name) {
        super(app, name);
    }

    @Override
    public void load(Demo01Frame app) {
        scoreFont = getResource("/fonts/upheavtt.ttf");
        textFont = getResource("/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create(Demo01Frame app) {

        add(new Demo01Frame.ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(app.getWorld().playArea.getWidth(), app.getWorld().playArea.getHeight())
        );
        add(new Demo01Frame.TextObject("game-title")
                .setText(messages.getString("app.scene.title.game.title"))
                .setFont(scoreFont.deriveFont(18.0f))
                .setTextAlign(Demo01Frame.Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.15)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        add(new Demo01Frame.TextObject("welcome-msg")
                .setText(messages.getString("app.scene.title.welcome.message"))
                .setFont(textFont.deriveFont(12.0f))
                .setTextAlign(Demo01Frame.Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.65)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );

        add(new Demo01Frame.TextObject("copyright-msg")
                .setText(messages.getString("app.scene.title.copyright.message"))
                .setFont(textFont.deriveFont(6.0f))
                .setTextAlign(Demo01Frame.Align.RIGHT)
                .setPosition(app.getBuffer().getWidth() - 10, app.getBuffer().getHeight() - 20)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        add(new Demo01Frame.Behavior() {
            @Override
            public void onKeyReleased(Demo01Frame app, Demo01Frame.Entity e, KeyEvent k) {
                switch (k.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        app.activateScene("play");
                    }
                    default -> {
                        // nothing to do there
                    }
                }
            }
        });

    }

    @Override
    public void update(Demo01Frame app, double elapsed) {
        super.update(app, elapsed);
    }
}
