package com.snapgames.apps.desktop.game.scenes;

import com.snapgames.apps.desktop.game.Game;
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

    default void load(Game app) {
    }

    void create(Game app);

    default void initialize(Game app) {
    }

    void activate(Game app);

    default void input(Game app) {
    }

    default void update(Game app, double elapsed) {
    }

    default void draw(Game app, Graphics2D g) {
    }

    void deactivate(Game app);

    void dispose(Game app);

    List<Behavior> getBehaviors();

    Map<String, Entity> getEntities();

    <T extends Entity> T getEntity(String name);

    void add(Behavior behavior);

    void add(Entity entity);

    void reset();

    Camera getActiveCamera();

    String getName();
}
