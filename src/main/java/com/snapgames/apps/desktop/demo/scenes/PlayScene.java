package com.snapgames.apps.desktop.demo.scenes;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.*;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.DialogBox;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;
import com.snapgames.apps.desktop.game.entity.util.Align;
import com.snapgames.apps.desktop.game.physic.Material;
import com.snapgames.apps.desktop.game.physic.PhysicNature;
import com.snapgames.apps.desktop.game.scene.AbstractScene;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import static com.snapgames.apps.desktop.game.Game.*;

public class PlayScene extends AbstractScene {


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

    public PlayScene(Game app, String name) {
        super(app, name);
    }

    @Override
    public void load(Game app) {
        scoreFont = getResource("/fonts/upheavtt.ttf");
        textFont = getResource("/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create(Game app) {

        Font scoreFont = getResource("/fonts/upheavtt.ttf");
        Font textFont = getResource("/fonts/Minecraftia-Regular.ttf");

        add(new ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(app.getWorld().getPlayArea().getWidth(), app.getWorld().getPlayArea().getHeight())
                .setCollisionActive(false)
                .setPhysicNature(PhysicNature.STATIC)
        );

        add(new TextObject("score")
                .setText("%05d")
                .setValue(score)
                .setFont(scoreFont.deriveFont(18.0f))
                .setPosition(20, 16)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
                .add(new Behavior() {
                    @Override
                    public void input(Game app, Entity e) {
                        ((TextObject) e).setValue(score);
                    }
                })
        );
        add(new ImageObject("heart")
                .setImage(getResource("/images/tiles01.png|0,96,16,16"))
                .setPosition(app.getBuffer().getWidth() - 40, 3)
                .setSize(16, 16)
                .setRelativeToCamera(true)
        );

        add(new TextObject("Life")
                .setText("%01d")
                .setValue(lifeCount)
                .setFont(textFont.deriveFont(8.0f))
                .setPosition(app.getBuffer().getWidth() - 32, 16)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
                .add(new Behavior() {
                    @Override
                    public void input(Game app, Entity e) {
                        ((TextObject) e).setValue(lifeCount);
                    }
                })
        );

        generateEntities(app, "enemy_", 20);

        GameObject player = (GameObject) new GameObject("player")
                .setNature(GameObjectNature.RECTANGLE)
                .setPosition(app.getWorld().playArea.getWidth() * 0.5,
                        app.getWorld().playArea.getHeight() * 0.5)
                .setSize(16, 16).setPriority(200)
                .setMaterial(new Material("Player_MAT", 1.0, 0.998, 0.98))
                .setMass(10.0)
                .add(new Behavior() {
                    @Override
                    public void input(Game app, Entity player) {
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

        setActiveCamera((Camera)
                new Camera("cam01")
                        .setTarget(player)
                        .setTweenFactor(0.01)
                        .setSize(app.getBuffer().getWidth(), app.getBuffer().getHeight())
                        .add(new Behavior() {
                            @Override
                            public void draw(Game app, Entity e, Graphics2D g) {
                                if (app.isDebugAtLeast(2)) {
                                    g.setColor(Color.ORANGE);
                                    g.setFont(textFont.deriveFont(8.0f));
                                    String camName = Game.messages.getString("app.camera.name");
                                    g.getFontMetrics().stringWidth(camName);
                                    g.drawString(
                                            Game.messages.getString("app.camera.name"),
                                            (int) app.getBuffer().getWidth() - g.getFontMetrics().stringWidth(camName)-10,
                                            (int) app.getBuffer().getHeight() - 10);
                                    Stroke s = g.getStroke();
                                    g.setStroke(new BasicStroke(0.5f));
                                    g.drawRect(10, 10, app.getBuffer().getWidth() - 20, app.getBuffer().getHeight() - 20);
                                    g.setStroke(s);
                                }
                            }
                        }));

        DialogBox exitConfirmation = (DialogBox) new DialogBox("exitConfirmBox")
                .setText(app.messages.getString("app.dialog.exit.message"))
                .setFont(textFont.deriveFont(8.0f))
                .setTextColor(Color.WHITE)
                .setSize(140, 40)
                .setFillColor(Color.DARK_GRAY)
                .setBorderColor(Color.BLACK)
                .setActive(false)
                .setPosition((app.getBuffer().getWidth() - 140) * 0.5, (app.getBuffer().getHeight() - 40) * 0.5)
                .setPriority(10)
                .add(new Behavior() {
                    @Override
                    public void onActivate(Game app, Entity e) {
                        setPause(true);
                    }

                    @Override
                    public void onDeactivate(Game app, Entity e) {
                        setPause(false);
                    }
                })
                .add(new UIObject() {
                    @Override
                    public void onKeyReleased(Game app, Entity e, KeyEvent k) {
                        if (k.getKeyCode() == KeyEvent.VK_Y || k.getKeyCode() == KeyEvent.VK_SPACE) {
                            app.setExitRequest(true);
                        }
                        if (k.getKeyCode() == KeyEvent.VK_N || k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            app.setExitRequest(false);
                            app.getPhysicEngine().setVisible(e, false);
                        }
                    }
                });
        add((Entity) exitConfirmation);

        // Add the required button OK
        Entity okButton = (Button) new Button("OK")
                .setAlign(Align.RIGHT)
                .setTextAlign(Align.CENTER)
                .setText(app.messages.getString("app.dialog.button.ok"))
                .setTextColor(Color.WHITE)
                .setFillColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new UIObject() {
                    @Override
                    public void onMouseClick(Game app, Entity e, double mouseX, double mouseY, int buttonId) {
                        app.setExitRequest(true);
                        e.setFillColor(Color.CYAN);
                    }
                });

        // Add the required button Cancel
        Entity cancelButton = new Button("Cancel")
                .setAlign(Align.LEFT)
                .setText(app.messages.getString("app.dialog.button.cancel"))
                .setTextAlign(Align.CENTER)
                .setTextColor(Color.WHITE)
                .setFillColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new UIObject() {
                    @Override
                    public void onMouseClick(Game app, Entity e, double mouseX, double mouseY, int buttonId) {
                        app.setExitRequest(false);
                        DialogBox db = (DialogBox) getEntity("exitConfirmBox");
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

        add(new Behavior() {
            @Override
            public void onKeyReleased(Game app, Entity e, KeyEvent k) {
                switch (k.getKeyCode()) {
                    // exit application on ESCAPE
                    case KeyEvent.VK_ESCAPE -> {
                        DialogBox db = (DialogBox) getEntity("exitConfirmBox");
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
                        app.getSceneManager().activateScene("title");
                    }
                    default -> {
                        // no action !
                    }
                }
            }
        });
    }


    private void generateEntities(Game app, String rootName, int nbEntities) {
        for (int i = 0; i < nbEntities; i++) {
            add(new GameObject(rootName + Entity.index)
                    .setNature(GameObjectNature.ELLIPSE)
                    .setPosition(app.getWorld().playArea.getWidth() * Math.random(),
                            app.getWorld().playArea.getHeight() * Math.random())
                    .setSize(8, 8)
                    .setPriority(100 + i)
                    .setFillColor(Color.RED)
                    .setAcceleration(0.25 - (Math.random() * 0.5), 0.25 - (Math.random() * 0.5))
                    .setMaterial(new Material("Enemy_MAT", 1.0, 0.96, 0.98))
                    .setMass(2.0 + (5.0 * Math.random())));
        }
    }
}
