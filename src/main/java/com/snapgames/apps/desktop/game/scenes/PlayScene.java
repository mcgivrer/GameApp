package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.GameApp;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import static com.snapgames.apps.desktop.game.GameApp.getResource;
import static com.snapgames.apps.desktop.game.GameApp.setPause;

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
                .add(new GameApp.Behavior() {
                    @Override
                    public void input(GameApp app, GameApp.Entity e) {
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
                .add(new GameApp.Behavior() {
                    @Override
                    public void input(GameApp app, GameApp.Entity e) {
                        ((GameApp.TextObject) e).setValue(lifeCount);
                    }
                })
        );

        generateEntities(app, "enemy_", 20);

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

        generateEntities(app, "enemy_", 20);

        setActiveCamera((GameApp.Camera)
                new GameApp.Camera("cam01")
                        .setTarget(player)
                        .setTweenFactor(0.01)
                        .setSize(app.getBuffer().getWidth(), app.getBuffer().getHeight())
                        .add(new GameApp.Behavior() {
                            @Override
                            public void draw(GameApp app, GameApp.Entity e, Graphics2D g) {
                                g.setColor(Color.WHITE);
                                g.setFont(textFont.deriveFont(8.0f));
                                g.drawString(
                                        app.messages.getString("app.camera.name"),
                                        (int) app.getWorld().playArea.getHeight(), (int) app.getWorld().playArea.getHeight() - 20);
                            }
                        }));

        GameApp.DialogBox exitConfirmation = (GameApp.DialogBox) new GameApp.DialogBox("exitConfirmBox")
                .setText(app.messages.getString("app.dialog.exit.message"))
                .setFont(textFont.deriveFont(8.0f))
                .setTextColor(Color.WHITE)
                .setSize(140, 40)
                .setFillColor(Color.DARK_GRAY)
                .setBorderColor(Color.BLACK)
                .setActive(false)
                .setPosition((app.getBuffer().getWidth() - 140) * 0.5, (app.getBuffer().getHeight() - 40) * 0.5)
                .setPriority(10)
                .add(new GameApp.Behavior() {
                    @Override
                    public void onActivate(GameApp app, GameApp.Entity e) {
                        setPause(true);
                    }

                    @Override
                    public void onDeactivate(GameApp app, GameApp.Entity e) {
                        setPause(false);
                    }
                })
                .add(new GameApp.UIObject() {
                    @Override
                    public void onKeyReleased(GameApp app, GameApp.Entity e, KeyEvent k) {
                        if (k.getKeyCode() == KeyEvent.VK_Y || k.getKeyCode() == KeyEvent.VK_SPACE) {
                            app.setExit(true);
                        }
                        if (k.getKeyCode() == KeyEvent.VK_N || k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            app.setExit(false);
                            app.setVisible(e, false);
                        }
                    }
                });
        add((GameApp.Entity) exitConfirmation);

        // Add the required button OK
        GameApp.Entity okButton = (GameApp.Button) new GameApp.Button("OK")
                .setAlign(GameApp.Align.RIGHT)
                .setTextAlign(GameApp.Align.CENTER)
                .setText(app.messages.getString("app.dialog.button.ok"))
                .setTextColor(Color.WHITE)
                .setFillColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new GameApp.UIObject() {
                    @Override
                    public void onMouseClick(GameApp app, GameApp.Entity e, double mouseX, double mouseY, int buttonId) {
                        app.setExit(true);
                        e.setFillColor(Color.CYAN);
                    }
                });

        // Add the required button Cancel
        GameApp.Entity cancelButton = new GameApp.Button("Cancel")
                .setAlign(GameApp.Align.LEFT)
                .setText(app.messages.getString("app.dialog.button.cancel"))
                .setTextAlign(GameApp.Align.CENTER)
                .setTextColor(Color.WHITE)
                .setFillColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new GameApp.UIObject() {
                    @Override
                    public void onMouseClick(GameApp app, GameApp.Entity e, double mouseX, double mouseY, int buttonId) {
                        app.setExit(false);
                        GameApp.DialogBox db = (GameApp.DialogBox) getEntity("exitConfirmBox");
                        db.setVisible(false);
                        setPause(false);
                        e.setFillColor(Color.CYAN);
                    }
                });
        add(okButton);
        add(cancelButton);
        // add the button to the dialog box.
        exitConfirmation.add(okButton);
        exitConfirmation.add(cancelButton);

        add(new GameApp.Behavior() {
            @Override
            public void onKeyReleased(GameApp app, GameApp.Entity e, KeyEvent k) {
                switch (k.getKeyCode()) {
                    // exit application on ESCAPE
                    case KeyEvent.VK_ESCAPE -> {
                        GameApp.DialogBox db = (GameApp.DialogBox) getEntity("exitConfirmBox");
                        app.activateEntity(db, true);
                    }
                    case KeyEvent.VK_PAGE_UP -> {
                        generateEntities(app, "enemy_", 10);
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


    private void generateEntities(GameApp app, String rootName, int nbEntities) {
        for (int i = 0; i < nbEntities; i++) {
            add(new GameApp.Entity(rootName + GameApp.Entity.index)
                    .setPosition(app.getWorld().playArea.getWidth() * Math.random(),
                            app.getWorld().playArea.getHeight() * Math.random())
                    .setSize(8, 8)
                    .setPriority(100 + i)
                    .setFillColor(Color.RED)
                    .setAcceleration(0.25 - (Math.random() * 0.5), 0.25 - (Math.random() * 0.5))
                    .setMaterial(new GameApp.Material("Enemy_MAT", 1.0, 0.98, 1.0))
                    .setMass(2.0 + (5.0 * Math.random())));
        }
    }
}
