package com.snapgames.apps.desktop.game.scene;

import com.snapgames.apps.desktop.demo.scenes.PlayScene;
import com.snapgames.apps.desktop.demo.scenes.TitleScene;
import com.snapgames.apps.desktop.game.Game;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The SceneManager class is responsible for managing different scenes in a game application.
 * It allows adding, activating, and resetting scenes within the game.
 *
 * @author Frédéric Delorme
 * @since 1.0.2
 */
public class SceneManager {
    private final Game app;
    /**
     * A Map of all game's {@link Scene}.
     */
    private Map<String, Scene> scenes = new ConcurrentHashMap<>();
    /**
     * current Active scene.
     */
    private Scene currentScene;


    /**
     * Initializes a new instance of the SceneManager with the specified game application.
     *
     * @param app the game application that this scene manager will manage scenes for.
     */
    public SceneManager(Game app) {
        this.app = app;
    }

    public void createScene() {
        add(new PlayScene(app, "play"));
        add(new TitleScene(app, "title"));
        activateScene("title");
    }

    /**
     * Add a new {@link Scene} implementation to the Game.
     *
     * @param scene the new {@link Scene}.
     */
    private void add(Scene scene) {

        scenes.put(scene.getName(), scene);
        scene.load(app);
    }

    /**
     * set the current active {@link Scene}.
     *
     * @param scene the implementation {@link Scene}.
     */
    private void setCurrentScene(Scene scene) {
        this.currentScene = scene;
    }

    /**
     * Activate the {@link Scene} named <code>sceneName</code>.
     *
     * @param sceneName the name of the {@link Scene} to be activated.
     */
    public void activateScene(String sceneName) {
        if (Optional.ofNullable(currentScene).isPresent()) {
            currentScene.deactivate(app);
        }
        setCurrentScene(scenes.get(sceneName));
        currentScene.create(app);
        currentScene.activate(app);
    }


    /**
     * Reset current Scene.
     */
    public void resetScene() {
        currentScene.reset();
        createScene();
    }

    /**
     * Retrieves the currently active Scene.
     *
     * @return the current active Scene
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
}
