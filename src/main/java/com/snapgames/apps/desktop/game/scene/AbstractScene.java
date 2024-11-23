package com.snapgames.apps.desktop.game.scene;

import com.snapgames.apps.desktop.game.GameApp;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Camera;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.scenes.Scene;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default abstract scene implementation for {@link Entity} and {@link Scene} management.
 *
 * @author Frederic Delorme
 * @since 1.0.0
 */
public abstract class AbstractScene implements Scene {
    private static long index = 0;
    private final long id = index++;
    private final GameApp app;
    private String name = "scene_" + id;
    private List<Behavior> behaviors = new CopyOnWriteArrayList<>();

    /**
     * Internal map of {@link Entity} for the active scene.
     */
    private Map<String, Entity> entities = new ConcurrentHashMap<>();
    /**
     * The current active {@link Camera} (is any).
     */
    private Camera activeCamera;

    /**
     * Create a new {@link AbstractScene} with a <code>name</code> and a parent <code>app</code>.
     *
     * @param app  the parent application {@link GameApp}
     * @param name thename opf this new {@link AbstractScene}
     */
    public AbstractScene(GameApp app, String name) {
        this.name = name;
        this.app = app;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Add an {@link Entity} to the current scene.
     *
     * @param entity the new {@link Entity} to be added to the current scene.
     */
    public void add(Entity entity) {
        entity.behaviors.forEach(b -> {
            b.create(app, entity);
        });
        entities.put(entity.name, entity);
    }

    @Override
    public void add(Behavior behavior) {
        behaviors.add(behavior);
    }

    @Override
    public Map<String, Entity> getEntities() {
        return entities;
    }

    public <T extends Entity> T getEntity(String name) {
        return (T) entities.get(name);
    }

    @Override
    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    @Override
    public void reset() {
        entities.clear();
        behaviors.clear();
        activeCamera = null;
    }

    /**
     * Define the current active {@link Camera}.
     * <p>
     * The defined {@link Camera}'s targeted {@link Entity} will be tracked on center of the Camera viewport
     * corresponding to the window center.
     *
     * @param cam the new {@link Camera} to activate.
     */
    protected void setActiveCamera(Camera cam) {
        this.activeCamera = cam;

    }

    @Override
    public Camera getActiveCamera() {
        return activeCamera;
    }

    @Override
    public void activate(GameApp app) {
        // nothing to do by default.
    }

    @Override
    public void deactivate(GameApp app) {
        getEntities().values().forEach(e -> e.setActive(false));
    }

    @Override
    public void dispose(GameApp app) {

    }
}
