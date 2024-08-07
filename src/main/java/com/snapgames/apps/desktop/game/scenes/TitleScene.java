package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.GameApp;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.snapgames.apps.desktop.game.GameApp.getResource;
import static com.snapgames.apps.desktop.game.GameApp.messages;

public class TitleScene extends GameApp.AbstractScene {


    Font scoreFont;
    Font textFont;

    /**
     * Create a new {@link TitleScene} with a <code>name</code> and a parent <code>app</code>.
     *
     * @param app  the parent application {@link GameApp}
     * @param name thename opf this new {@link TitleScene}
     */
    public TitleScene(GameApp app, String name) {
        super(app, name);
    }

    @Override
    public void load(GameApp app) {
        scoreFont = getResource("/fonts/upheavtt.ttf");
        textFont = getResource("/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create(GameApp app) {

        add(new GameApp.ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(app.getWorld().playArea.getWidth(), app.getWorld().playArea.getHeight())
        );
        add(new GameApp.TextObject("game-title")
                .setText(messages.getString("app.scene.title.game.title"))
                .setFont(scoreFont.deriveFont(18.0f))
                .setTextAlign(GameApp.Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.15)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        add(new GameApp.TextObject("welcome-msg")
                .setText(messages.getString("app.scene.title.welcome.message"))
                .setFont(textFont.deriveFont(12.0f))
                .setTextAlign(GameApp.Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.65)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );

        add(new GameApp.TextObject("copyright-msg")
                .setText(messages.getString("app.scene.title.copyright.message"))
                .setFont(textFont.deriveFont(6.0f))
                .setTextAlign(GameApp.Align.RIGHT)
                .setPosition(app.getBuffer().getWidth() - 10, app.getBuffer().getHeight() - 20)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        add(new GameApp.Behavior() {
            @Override
            public void onKeyReleased(GameApp app, GameApp.Entity e, KeyEvent k) {
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
    public void update(GameApp app, double elapsed) {
        super.update(app, elapsed);
    }
}
