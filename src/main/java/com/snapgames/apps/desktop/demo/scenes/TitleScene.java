package com.snapgames.apps.desktop.demo.scenes;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ImageObject;
import com.snapgames.apps.desktop.game.entity.TextObject;
import com.snapgames.apps.desktop.game.entity.ui.ItemObject;
import com.snapgames.apps.desktop.game.entity.ui.MenuObject;
import com.snapgames.apps.desktop.game.entity.util.Align;
import com.snapgames.apps.desktop.game.scene.AbstractScene;

import java.awt.*;

import static com.snapgames.apps.desktop.game.Game.getResource;
import static com.snapgames.apps.desktop.game.Game.messages;

public class TitleScene extends AbstractScene {

    Font scoreFont;
    Font textFont;

    /**
     * Create a new {@link TitleScene} with a <code>name</code> and a parent <code>app</code>.
     *
     * @param app  the parent application {@link Game}
     * @param name thename opf this new {@link TitleScene}
     */
    public TitleScene(Game app, String name) {
        super(app, name);
    }

    @Override
    public void load(Game app) {
        scoreFont = getResource("/fonts/upheavtt.ttf");
        textFont = getResource("/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create(Game app) {

        add(new ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(app.getWorld().playArea.getWidth(), app.getWorld().playArea.getHeight())
        );
        add(new TextObject("game-title")
                .setText(messages.getString("app.scene.title.game.title"))
                .setFont(scoreFont.deriveFont(18.0f))
                .setTextAlign(Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.15)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        add(new TextObject("welcome-msg")
                .setText(messages.getString("app.scene.title.welcome.message"))
                .setFont(textFont.deriveFont(Font.ITALIC, 8.0f))
                .setTextAlign(Align.CENTER)
                .setPosition(app.getBuffer().getWidth() * 0.5, app.getBuffer().getHeight() * 0.75)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );

        add(new TextObject("copyright-msg")
                .setText(messages.getString("app.scene.title.copyright.message"))
                .setFont(textFont.deriveFont(6.0f))
                .setTextAlign(Align.RIGHT)
                .setPosition(app.getBuffer().getWidth() - 10, app.getBuffer().getHeight() - 20)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
        );
        MenuObject mo = (MenuObject) new MenuObject("menu")
                .setFont(textFont.deriveFont(10.0f))
                .setText(messages.getString("app.scene.title.menu.choose"))
                .setPosition(app.getBuffer().getWidth() * 0.3, app.getBuffer().getHeight() * 0.45);

        mo.addItem((ItemObject) new ItemObject("item1")
                .setValue(1)
                .setText(messages.getString("app.scene.title.menu.option.start"))
        );

        mo.addItem((ItemObject) new ItemObject("item3")
                .setValue(2)
                .setText(messages.getString("app.scene.title.menu.option.quit"))
        );

        mo.add(new Behavior<MenuObject>() {
            @Override
            public void onSelected(Game app, MenuObject e) {
                int vio = (int) ((ItemObject) e.child.get(e.getItemIndex())).getValue();
                if (vio == 1) {
                    app.activateScene("play");
                } else if (vio == 2) {
                    app.setExitRequest(true);
                }
            }
        });
        add((Entity) mo);
    }
}
