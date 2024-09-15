package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.GameApp;

import java.awt.*;

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
                .setSize(app.getWorld().getPlayArea().getWidth(), app.getWorld().getPlayArea().getHeight())
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
                .setFont(textFont.deriveFont(Font.ITALIC, 8.0f))
                .setTextAlign(GameApp.Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.75)
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
        GameApp.MenuObject mo = (GameApp.MenuObject) new GameApp.MenuObject("menu")
                .setFont(textFont.deriveFont(10.0f))
                .setText(messages.getString("app.scene.title.menu.choose"))
                .setPosition(app.getBuffer().getWidth() * 0.3, app.getBuffer().getHeight() * 0.45);

        mo.addItem((GameApp.ItemObject) new GameApp.ItemObject("item1")
                .setValue(1)
                .setText(messages.getString("app.scene.title.menu.option.start"))
        );

        mo.addItem((GameApp.ItemObject) new GameApp.ItemObject("item3")
                .setValue(2)
                .setText(messages.getString("app.scene.title.menu.option.quit"))
        );

        mo.add(new GameApp.Behavior<GameApp.MenuObject>() {
            @Override
            public void onSelected(GameApp app, GameApp.MenuObject e) {
                int vio = (int) ((GameApp.ItemObject) e.child.get(e.getItemIndex())).getValue();
                if (vio == 1) {
                    app.activateScene("play");
                } else if (vio == 2) {
                    app.setExitRequest(true);
                }
            }
        });
        add((GameApp.Entity) mo);
    }
}
