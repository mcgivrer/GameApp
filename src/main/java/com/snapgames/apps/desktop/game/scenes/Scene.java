package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.GameApp;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Camera;
import com.snapgames.apps.desktop.game.entity.Entity;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * A Scene is defining a full gameplay and integrate all the lifecycle operation.
 *
 * @author Frederic Delorme
 * @since 1.0.0
 */
public interface Scene {

    default void load(GameApp app) {
    }

    void create(GameApp app);

    default void initialize(GameApp app) {
    }

    void activate(GameApp app);

    default void input(GameApp app) {
    }

    default void update(GameApp app, double elapsed) {
    }

    default void draw(GameApp app, Graphics2D g) {
    }

    void deactivate(GameApp app);

    void dispose(GameApp app);

    List<Behavior> getBehaviors();

    Map<String, Entity> getEntities();

    <T extends Entity> T getEntity(String name);

    void add(Behavior behavior);

    void add(Entity entity);

    void reset();

    Camera getActiveCamera();

    String getName();
}
