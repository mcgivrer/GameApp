package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.GameApp;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import static com.snapgames.apps.desktop.game.GameApp.*;

public class PlayScene extends GameApp.AbstractScene {


    /**
     * Internal scene Score value for HUD.
     */
    private int score = 0;
    /**
     * Internal life counter value for HUD.
     */
    private int lifeCount = 3;

    Font scoreFont;

    Font textFont;

    public PlayScene(GameApp app, String name) {
        super(app, name);
    }

    @Override
    public void load(GameApp app) {
        scoreFont = getResource("/fonts/upheavtt.ttf");
        textFont = getResource("/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create(GameApp app) {

        Font scoreFont = getResource("/fonts/upheavtt.ttf");
        Font textFont = getResource("/fonts/Minecraftia-Regular.ttf");

        add(new GameApp.ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(app.getWorld().playArea.getWidth(), app.getWorld().playArea.getHeight())
        );

        add(new GameApp.TextObject("score")
                .setText("%05d")
                .setValue(score)
                .setFont(scoreFont.deriveFont(18.0f))
                .setPosition(20, 16)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
                .add(new GameApp.Behavior<TextObject>() {
                    @Override
                    public void input(GameApp app, TextObject e) {
                        ((GameApp.TextObject) e).setValue(score);
                    }
                })
        );
        add(new GameApp.ImageObject("heart")
                .setImage(getResource("/images/tiles01.png|0,96,16,16"))
                .setPosition(app.getBuffer().getWidth() - 40, 3)
                .setSize(16, 16)
                .setRelativeToCamera(true)
        );

        add(new GameApp.TextObject("Life")
                .setText("%01d")
                .setValue(lifeCount)
                .setFont(textFont.deriveFont(8.0f))
                .setPosition(app.getBuffer().getWidth() - 32, 16)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
                .add(new GameApp.Behavior<TextObject>() {
                    @Override
                    public void update(GameApp app, TextObject e, double elapsed) {
                        e.setValue(lifeCount);
                    }
                })
        );

        GameApp.GameObject player = (GameApp.GameObject) new GameApp.GameObject("player")
                .setNature(GameApp.GameObjectNature.RECTANGLE)
                .setPosition(app.getWorld().playArea.getWidth() * 0.5,
                        app.getWorld().playArea.getHeight() * 0.5)
                .setSize(16, 16).setPriority(200)
                .setMaterial(new GameApp.Material("Player_MAT", 1.0, 0.998, 0.98))
                .setMass(10.0)
                .add(new GameApp.Behavior() {
                    @Override
                    public void input(GameApp app, GameApp.Entity player) {
                        double speed = 0.025;
                        if (app.isKeyPressed(KeyEvent.VK_UP)) {
                            player.forces.add(new Point2D.Double(0, -(speed * 2.0)));
                        }
                        if (app.isKeyPressed(KeyEvent.VK_DOWN)) {
                            player.forces.add(new Point2D.Double(0, speed));
                        }
                        if (app.isKeyPressed(KeyEvent.VK_LEFT)) {
                            player.forces.add(new Point2D.Double(-speed, 0));
                        }
                        if (app.isKeyPressed(KeyEvent.VK_RIGHT)) {
                            player.forces.add(new Point2D.Double(speed, 0));
                        }
                    }
                });
        add(player);

        generateEntities(app, "enemy_", 10, 10, Color.RED);
        generateEntities(app, "energy_", 10, 6, Color.GREEN);

        setActiveCamera((GameApp.Camera)
                new GameApp.Camera("cam01")
                        .setTarget(player)
                        .setTweenFactor(0.01)
                        .setSize(app.getBuffer().getWidth(), app.getBuffer().getHeight())
                        .add(new GameApp.Behavior() {
                            @Override
                            public void draw(GameApp app, GameApp.Entity e, Graphics2D g) {
                                if (app.isDebugAtLeast(2)) {
                                    g.setColor(Color.ORANGE);
                                    g.setFont(textFont.deriveFont(8.0f));
                                    String camName = GameApp.messages.getString("app.camera.name");
                                    g.getFontMetrics().stringWidth(camName);
                                    g.drawString(
                                            GameApp.messages.getString("app.camera.name"),
                                            (int) app.getBuffer().getWidth() - g.getFontMetrics().stringWidth(camName) - 10,
                                            (int) app.getBuffer().getHeight() - 10);
                                    Stroke s = g.getStroke();
                                    g.setStroke(new BasicStroke(0.5f));
                                    g.drawRect(10, 10, app.getBuffer().getWidth() - 20, app.getBuffer().getHeight() - 20);
                                    g.setStroke(s);
                                }
                            }
                        }));

        GameApp.DialogBox exitConfirmation = (ConfirmDialogBox) new ConfirmDialogBox("exitConfirmBox")
                .addConfirm(new GameApp.UIObject() {
                    @Override
                    public void onMouseClick(GameApp app, GameApp.Entity e, double mouseX, double mouseY, int buttonId) {
                        UIObject.super.onMouseClick(app, e, mouseX, mouseY, buttonId);
                        app.setExitRequest(true);
                    }
                })
                .addCancel(new GameApp.UIObject() {
                    @Override
                    public void onMouseClick(GameApp app, GameApp.Entity e, double mouseX, double mouseY, int buttonId) {
                        UIObject.super.onMouseClick(app, e, mouseX, mouseY, buttonId);
                        GameApp.DialogBox db = (GameApp.DialogBox) e.getParent();
                        db.setVisible(false);
                        setPause(false);
                        app.setExitRequest(false);
                    }
                })
                .setText(app.messages.getString("app.dialog.exit.message"))
                .setFont(textFont.deriveFont(8.0f))
                .setTextColor(Color.WHITE)
                .setSize(140, 40)
                .setActive(false)
                .setPosition((app.getBuffer().getWidth() - 140) * 0.5, (app.getBuffer().getHeight() - 40) * 0.5)
                .setPriority(10)
                .add(new GameApp.Behavior<ConfirmDialogBox>() {
                    @Override
                    public void onActivate(GameApp app, ConfirmDialogBox e) {
                        setPause(true);
                    }

                    @Override
                    public void onDeactivate(GameApp app, ConfirmDialogBox e) {
                        setPause(false);
                    }
                })
                .add(new GameApp.UIObject() {
                    @Override
                    public void onKeyReleased(GameApp app, GameApp.Entity e, KeyEvent k) {
                        if (k.getKeyCode() == KeyEvent.VK_Y || k.getKeyCode() == KeyEvent.VK_SPACE) {
                            app.setExitRequest(true);
                        }
                        if (k.getKeyCode() == KeyEvent.VK_N || k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            app.setExitRequest(false);
                            app.setVisible(e, false);
                        }
                    }
                });
        add((GameApp.Entity) exitConfirmation);

        // add Scene specific behavior.
        add(new GameApp.Behavior() {
            @Override
            public void onKeyReleased(GameApp app, Entity e, KeyEvent k) {
                switch (k.getKeyCode()) {
                    // exit application on ESCAPE
                    case KeyEvent.VK_ESCAPE -> {
                        GameApp.DialogBox db = (GameApp.DialogBox) getEntity("exitConfirmBox");
                        app.activateEntity(db, true);
                    }
                    case KeyEvent.VK_PAGE_UP -> {
                        generateEntities(app, "enemy_", 10, 8,Color.RED);
                    }
                    case KeyEvent.VK_G -> {
                        if (k.isControlDown()) {
                            app.getWorld().gravity *= -1;
                        }
                    }
                    case KeyEvent.VK_F12 -> {
                        app.activateScene("title");
                    }
                    default -> {
                        // no action !
                    }
                }
            }
        });
    }


    private void generateEntities(GameApp app, String rootName, int nbEntities, int size, Color c) {
        for (int i = 0; i < nbEntities; i++) {
            add(new GameApp.GameObject(rootName + GameApp.Entity.index)
                    .setNature(GameApp.GameObjectNature.ELLIPSE)
                    .setPosition(app.getWorld().playArea.getWidth() * Math.random(),
                            app.getWorld().playArea.getHeight() * Math.random())
                    .setSize(size, size)
                    .setPriority(100 + i)
                    .setFillColor(c)
                    .setAcceleration(0.25 - (Math.random() * 0.5), 0.25 - (Math.random() * 0.5))
                    .setMaterial(new GameApp.Material("Enemy_MAT", 1.0, 0.96, 0.98))
                    .setMass(2.0 + (5.0 * Math.random())));
        }
    }
}
