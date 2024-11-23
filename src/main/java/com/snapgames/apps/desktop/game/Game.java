package com.snapgames.apps.desktop.game;

import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.DialogBox;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;
import com.snapgames.apps.desktop.game.gfx.Renderer;
import com.snapgames.apps.desktop.game.physic.*;
import com.snapgames.apps.desktop.demo.scenes.PlayScene;
import com.snapgames.apps.desktop.game.scene.Scene;
import com.snapgames.apps.desktop.demo.scenes.TitleScene;
import com.snapgames.apps.desktop.game.scene.SceneManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.snapgames.apps.desktop.game.gfx.Renderer.buffer;
import static com.snapgames.apps.desktop.game.utils.Log.*;

/**
 * Main class for Project {@link Game}
 *
 * <p>This class is the main class for a java game template.
 * It initialize default components and services
 * to make a basic 2D game with a standard game loop:
 * <ul>
 *     <li><code>input</code> to capture and process gamer inputs,</li>
 *     <li><code>update</code> to compute {@link Entity} moves and {@link Behavior}'s
 *     into the game {@link World}</li>
 *     <li><code>render</code> to draw ann {@link Entity}'son internal buffer,
 *     then sync on the {@link javax.swing.JFrame} with a buffer strategy</li>
 * </ul></p>
 *
 * <p>It also provide some subclasses as components:
 * <ul>
 *     <li>{@link Entity} is the basic default Entity for a game object, and a bunch of inherited other entities come
 *     along the tutorial steps,</li>
 *     <li>{@link World} defines the Game context with  its play area and gravity,</li>
 *     <li>{@link Material} set physics constants for a specific material
 *     behavior assign to an {@link Entity},</li>
 *     <li>{@link Behavior} is an interface API to enhance {@link Entity}
 *     default processing with new specific behavior,</li>
 *      <li>{@link Scene} is defining a game play type for the game itself.</li>
 * </ul></p>
 *
 * <p>Interaction are implemented to support input keys and mouse,thanks to the JDK {@link KeyListener}
 * and {@link MouseListener}.</p>
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Game implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

    /*------ Application properties -----*/

    /**
     * Translated messages to display text on console and or on screen.
     */
    public static ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    /**
     * Configuration properties file.
     */
    private final Properties config = new Properties();

    /**
     * Default file path for configuration properties
     */
    private String configFilePath = "/config.properties";


    /**
     * Flag set to true when game exit is required.
     */
    private static boolean exit = false;
    /**
     * Flag set to true when non-relative to camera {@link Entity} needs processing to be set on pause.
     */
    private static boolean pause = false;
    /**
     * Internal debug level output to console.
     */
    public static int debug = 0;
    /**
     * (No used) Internal debug filtering on {@link Entity}'s name.
     */
    private static String debugFilter = "";


    /**
     * Frame Per Second rate
     */
    private int FPS = 60;
    /**
     * Update Per Second rate
     */
    private int UPS = 120;
    /**
     * Mouse horizontal position on buffer
     */
    public double mouseX = 0;
    /**
     * Mouse vertical position on buffer
     */
    public double mouseY = 0;
    /**
     * Mouse horizontal position on the window
     */
    public int realMouseX;
    /**
     * Mouse vertical position on the window
     */
    public int realMouseY;
    /**
     * Previously focused {@link Entity} by mouse cursor.
     */
    private static Entity previousEntity = null;

    /**
     * Internal buffer for key states
     */
    private boolean[] keys = new boolean[1024];
    /**
     * World default instance to define a play area, a gravity and a Material.
     */
    private World world = new World("earth", 0.981, new Rectangle2D.Double(), Material.DEFAULT);


    private Renderer renderer;
    private PhysicEngine physicEngine;
    private SceneManager sceneManager;
    private CollisionManager collisionManager;
    private SpacePartition spacePartition;

    /**
     * Create the {@link Game} instance and detect the current java context.
     */
    public Game() {
        info("Initialization application %s (%s); running on JDK %s; at %s; with classpath = %s",
                messages.getString("app.name"),
                messages.getString("app.version"),
                System.getProperty("java.version"),
                System.getProperty("java.home"),
                System.getProperty("java.class.path"));
    }

    public void run(String[] args) {
        init(args);
        renderer.prepareDisplay();
        sceneManager.createScene();
        loop();
        dispose();
    }

    /*----- Initialization and configuration -----*/

    /**
     * Initialization based on CLI argument and configuration file.
     *
     * @param args list of String arguments from Java command line.
     */
    private void init(String[] args) {
        parseCliArguments(args);
        loadConfiguration(configFilePath);
        parseConfiguration();
        info("Configuration applied: %s", config.stringPropertyNames().stream()
                .map(key -> key + "=" + config.getProperty(key))
                .collect(Collectors.joining(", ")));

        renderer = new Renderer(this);
        renderer.init(this);
        physicEngine = new PhysicEngine(this);
        sceneManager = new SceneManager(this);
        collisionManager = new CollisionManager(this);
    }

    /**
     * Parse all the arguments from <code>args</code> and set default values into {@link Game#config} as a configuration set.
     *
     * @param args the list of arguments to parse and set as default in the {@link Game#config}.
     */
    private void parseCliArguments(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        lArgs.forEach(s -> {
            info(String.format("Configuration|Argument: %s", s));
            String[] keyVal = s.split("=");
            switch (keyVal[0]) {
                case "window", "w" -> {
                    config.setProperty("app.window.size", keyVal[1]);
                    info("Window size is set to %s", keyVal[1]);
                }
                case "buffer", "b" -> {
                    config.setProperty("app.render.buffer", keyVal[1]);
                    info("Rendering buffer size is set to %s", keyVal[1]);
                }
                case "title", "t" -> {
                    config.setProperty("app.window.title", keyVal[1]);
                    info("Window title is set to %s", keyVal[1]);

                }
                case "exit", "x" -> {
                    config.setProperty("app.exit", keyVal[1]);
                    info("The auto-exit flag is set to %s", keyVal[1]);
                }
                case "debug", "d" -> {
                    config.setProperty("app.debug.level", keyVal[1]);
                    info("The debug level is set to %s", keyVal[1]);
                }
                case "debugFilter", "df" -> {
                    config.setProperty("app.debug.filter", keyVal[1]);
                    info("The debug filter is set to %s", keyVal[1]);
                }
                case "ups" -> {
                    config.setProperty("app.update.ups", keyVal[1]);
                    info("The Update-Per-Second rate is set to %s", keyVal[1]);
                }
                case "fps" -> {
                    config.setProperty("app.render.fps", keyVal[1]);
                    info("The Frame-Per-Second rate is set to %s", keyVal[1]);
                }
                case "config" -> {
                    configFilePath = keyVal[1];
                }
                default -> {
                    warn("This argument %s is unknown, it is ignored.", s);
                }
            }
        });
    }

    public void parseConfiguration() {
        // set the default FPS for the game
        FPS = Integer.parseInt(config.getProperty("app.render.fps", "60"));
        // set the default processing update pace for the game
        UPS = Integer.parseInt(config.getProperty("app.update.ups", "60"));
        // is exit because of test mode requested ?
        exit = Boolean.parseBoolean(config.getProperty("app.exit", "false"));
        // define debug output level, on console.
        debug = Integer.parseInt(config.getProperty("app.debug.level", "0"));
        // Retrieve debug filtering configuration. Only listed status will be sent to console output.
        debugFilter = config.getProperty("app.debug.level", "WARN,ERROR");
        // world size
        world.playArea = new Rectangle2D.Double(0, 0,
                Integer.parseInt(config.getProperty("app.world.play.area.width", "320")),
                Integer.parseInt(config.getProperty("app.world.play.area.height", "240"))
        );
        // world gravity
        world.gravity = Double.parseDouble(config.getProperty("app.world.gravity", "0.0981"));
        // full screen mode active or not.
    }

    /**
     * Read the configuration file from the configFilePath in the JAR or for Test,
     * or directly from the JAR side external file configFilePath.
     *
     * @param configFilePath path to the configuration file to be loaded
     */
    public void loadConfiguration(String configFilePath) {
        try {
            Path rootPath = Paths.get(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            File propertyFile = new File(rootPath.toFile(), configFilePath);
            if (propertyFile.exists()) {
                try (InputStream input = new FileInputStream(propertyFile)) {
                    config.load(input);
                    info("Reading configuration from file %s at %s", configFilePath, rootPath.getFileName().toUri());
                }
            } else {
                config.load(this.getClass().getResourceAsStream(configFilePath));
                info("Reading JAR contained configuration from file %s", configFilePath);
            }
        } catch (IOException | URISyntaxException ioe) {
            error("Unable to read configuration file %s : %s", configFilePath, ioe.getMessage());
        }
    }

    /*----- Manage current Scene -----*/


    /**
     * Retrieve a resource from a path.
     * <p>
     * It can be a Font (ttf) or an image (jpg, png)
     *
     * <p>if an image is loaded you can add slicing information on path:
     * <code>path-to-/my-image.png|x,y,w,h</code> where: </p>
     * <ul>
     *     <li><code>x,y</code> are position in the image</li>
     *     <li><code>w,h</code> are width and height of the sliced image</li>
     * </ul>
     *
     * @param path path to the resource to be loaded.
     * @param <T>  the type of the resource.
     * @return the corresponding resource. It can be a {@link Font} or a {@link BufferedImage}.
     */
    public static <T> T getResource(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            if (path.contains("|")) {
                ext = path.substring(path.lastIndexOf(".") + 1, path.lastIndexOf("|"));
            }
            switch (ext) {
                case "ttf" -> {
                    return (T) Font.createFont(
                            Font.TRUETYPE_FONT,
                            Game.class.getResourceAsStream(path));
                }
                case "png", "jpg" -> {
                    if (path.contains("|")) {
                        String filePath = path.substring(0, path.lastIndexOf("|"));
                        BufferedImage img = ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream(filePath)));
                        String slice = path.substring(path.lastIndexOf("|") + 1);
                        String[] slices = slice.split(",");
                        return (T) img.getSubimage(
                                Integer.parseInt(slices[0]),
                                Integer.parseInt(slices[1]),
                                Integer.parseInt(slices[2]),
                                Integer.parseInt(slices[3])
                        );
                    } else {
                        return (T) ImageIO.read(Game.class.getResourceAsStream(path));
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (FontFormatException | IOException e) {
            error("Unable to read font file:%s", e.getMessage());
        }
        return null;
    }


    public void activateEntity(Entity e, boolean a) {
        e.setActive(a);
        e.behaviors.forEach(b -> b.onActivate(this, e));
        e.child.forEach(c -> {
            activateEntity(c, a);
        });
    }

    /*----- Game loop -----*/

    public void loop() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        long delay = 1;

        long updateFrames = 0;
        long updateTime = 0;
        long currentUPS = 0;

        long renderTime = 0;
        long renderFrames = 0;
        long currentFPS = 0;

        Map<String, Object> stats = new ConcurrentHashMap<>();
        do {
            input();
            updateTime += delay;
            if (updateTime > 1000) {
                currentUPS = updateFrames;
                updateFrames = 0;
                updateTime = 0;
            } else {
                updateFrames++;

            }
            physicEngine.update(sceneManager.getCurrentScene(), delay);

            renderTime += delay;
            if (renderTime > 1000) {
                currentFPS = renderFrames;
                renderFrames = 0;
                renderTime = 0;
            } else {
                renderFrames++;
            }
            renderer.draw(sceneManager.getCurrentScene(), stats);

            try {
                Thread.sleep(delay > 1000 / UPS ? 1 : 1000 / UPS - delay);
            } catch (IllegalArgumentException iae) {
                error("Unable to wait for a negative number of ms !");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stats.put("fps", currentFPS);
            stats.put("ups", currentUPS);
            stats.put("ft", delay);

            endTime = System.currentTimeMillis();
            delay = endTime - startTime;
            startTime = endTime;

        } while (!exit);
    }

    public static boolean isPause() {
        return pause;
    }

    /**
     * Process all input management on the current scene {@link Entity}'s.
     */
    public void input() {
        sceneManager.getCurrentScene().getEntities().values()
                .stream().filter(Entity::isActive)
                .forEach(this::processInputBehaviorForEntity);
    }

    /**
     * Apply all the {@link Behavior#input(Game, Entity)} ()} to the {@link Entity}.
     *
     * @param e the {@link Entity} to be processed abut input management.
     */
    private void processInputBehaviorForEntity(Entity e) {
        e.behaviors.forEach(b -> {
            b.input(this, e);
        });
        e.child.forEach(this::processInputBehaviorForEntity);
    }


    /**
     * Detects if debug level is greater than the required one
     *
     * @param debugLevel the minimum required debug level
     * @return true if the required level is reached.
     */
    public boolean isDebugAtLeast(int debugLevel) {
        return debug >= debugLevel;
    }

    /*----- objects rendering -----*/

    /*----- releasing objects and resources -----*/

    public void dispose() {
        renderer.dispose();
        info("End of application ");
    }

    /*----- Game start entry point -----*/

    public static void main(String[] argc) {
        Game app = new Game();
        app.run(argc);
    }


    /*----- Logger API -----*/


    /*----- manage keys input -----*/
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles the event when a key is pressed. Updates the key state, processes behaviors of active entities,
     * and triggers behaviors associated with the entities.
     *
     * @param k the KeyEvent triggered when a key is pressed
     */
    @Override
    public void keyPressed(KeyEvent k) {
        keys[k.getKeyCode()] = true;
        sceneManager.getCurrentScene().getEntities().values().stream()
                .filter(Entity::isActive)
                .filter(e -> !e.behaviors.isEmpty())
                .forEach(e -> {
                    e.behaviors.forEach(b -> {
                        b.onKeyPressed(this, e, k);
                    });
                });
    }

    /**
     * Handles the event when a key is released. Updates the key state, processes behaviors of active entities,
     * and executes global scene behaviors. Also checks for specific key combinations to trigger certain game actions.
     *
     * @param k the KeyEvent triggered when a key is released
     */
    @Override
    public void keyReleased(KeyEvent k) {
        keys[k.getKeyCode()] = false;
        sceneManager.getCurrentScene().getEntities().values().stream()
                .filter(Entity::isActive)
                .filter(e -> !e.behaviors.isEmpty())
                .forEach(e -> {
                    e.behaviors.forEach(b -> {
                        b.onKeyReleased(this, e, k);
                    });
                });
        sceneManager.getCurrentScene().getBehaviors().forEach(b -> b.onKeyReleased(this, null, k));
        switch (k.getKeyCode()) {
            // reset the scene on CTRL+Z
            case KeyEvent.VK_Z -> {
                if (k.isControlDown()) {
                    sceneManager.resetScene();
                }
            }

            case KeyEvent.VK_D -> {
                if (k.isControlDown()) {
                    debug = (debug < 5) ? debug + 1 : 0;
                }
            }
            case KeyEvent.VK_P, KeyEvent.VK_PAUSE -> {
                setPause(!isPause());
            }
            case KeyEvent.VK_F11 -> {
                setPause(true);
                renderer.switchFullScreen();
                setPause(false);
            }
            default -> {
                // Nothing to do here.
            }
        }

    }

    /**
     * Checks if a specific key is pressed based on the provided key code.
     *
     * @param keyCode the code of the key to check
     * @return true if the specified key is pressed, false otherwise
     */
    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    /*----- Mouse event management -----*/

    /**
     * Handles the mouse clicked event. If an entity is detected under the current mouse coordinates,
     * it triggers the onMouseClick behavior for that entity and logs the entity click event.
     *
     * @param e the MouseEvent triggered when the mouse button is clicked
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            debug("Entity %s has been clicked", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMouseClick(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    /**
     * Determines if there is any entity under the given mouse coordinates and returns the entity
     * with the highest priority.
     *
     * @param mouseX the X coordinate of the mouse cursor
     * @param mouseY the Y coordinate of the mouse cursor
     * @return an Optional containing the entity under the mouse cursor if present, otherwise an empty Optional
     */
    private Optional<Entity> getEntityUnderMouse(double mouseX, double mouseY) {
        Optional<Entity> entityClicked = sceneManager.getCurrentScene().getEntities().values().stream()
                .filter(entity -> Arrays.stream(entity.getClass().getInterfaces()).filter(i -> i.equals(UIObject.class)).findFirst().isPresent()
                        && entity.isActive()
                        && entity.contains(mouseX, mouseY)).sorted((a, b) -> Integer.compare(b.priority, a.priority)).findFirst();

        return entityClicked;
    }

    /**
     * Handles the event when a mouse button is pressed. If an entity is detected under the current mouse coordinates,
     * triggers the onMousePressed behavior for that entity and logs the entity press event.
     *
     * @param e the MouseEvent triggered when the mouse button is pressed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            debug("Entity %s has been pressed", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMousePressed(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * If an entity is detected under the mouse at the current coordinates,
     * it triggers the onMouseReleased behavior for that entity.
     *
     * @param e the MouseEvent triggered when the mouse button is released
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            debug("Entity %s has been released", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMouseReleased(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Handles the mouse moved event, updating the current mouse coordinates,
     * detecting the entity under the mouse, and triggering appropriate mouse-in and mouse-out behaviors.
     *
     * @param e the MouseEvent triggered when the mouse is moved
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        JFrame window = renderer.getWindow();
        this.realMouseX = e.getX();
        this.realMouseY = e.getY() - window.getInsets().top;
        this.mouseX = (realMouseX * ((double) buffer.getWidth() / window.getWidth()));
        this.mouseY = (realMouseY * ((double) buffer.getHeight() / (window.getHeight() - window.getInsets().top)));

        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            //reset previously highlighted UIObject
            if (previousEntity != null && !previousEntity.equals(entityClicked)) {
                if (previousEntity instanceof com.snapgames.apps.desktop.game.entity.ui.Button) {
                    previousEntity.borderColor = UIObject.mouseOutBorderColor;
                    previousEntity.fillColor = UIObject.mouseOutColor;

                    debug("Mouse is out of the entity  %s (%s)", previousEntity.name, previousEntity.getClass());
                    previousEntity.setAttribute("mouse_hover", false);
                    previousEntity.behaviors
                            .forEach(b -> b.onMouseOut(this, previousEntity, mouseX, mouseY));
                }
            }
            previousEntity = entityClicked;
            if (entityClicked instanceof Button) {
                entityClicked.behaviors
                        .forEach(b -> b.onMouseIn(this, entityClicked, mouseX, mouseY));
                entityClicked.setAttribute("mouse_hover", true);
            }
            debug("Mouse enter over the entity  %s (%s)", entityClicked.name, entityClicked.getClass());

        }
    }

    /*----- getters and setters -----*/

    /**
     * Retrieves the configuration properties for the game.
     *
     * @return the configuration properties as a {@link Properties} object.
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * Retrieves the current buffer image.
     *
     * @return the current {@link BufferedImage} used in the game.
     */
    public BufferedImage getBuffer() {
        return buffer;
    }

    /**
     * Retrieves the current World instance associated with the game.
     *
     * @return the World instance managing the game's context, including play area and gravity.
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Sets the exit request flag for the game.
     *
     * @param x a boolean value representing whether an exit has been requested
     */
    public void setExitRequest(boolean x) {
        exit = x;
    }

    /**
     * Sets the pause state for the game.
     *
     * @param p a boolean flag indicating whether the game should be paused (true) or not (false)
     */
    public static void setPause(boolean p) {
        pause = p;
    }

    /**
     * Retrieves the SceneManager instance associated with the game.
     *
     * @return the SceneManager instance managing the game's scenes
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Retrieves the PhysicEngine instance associated with the game.
     *
     * @return the PhysicEngine instance managing the game's physics.
     */
    public PhysicEngine getPhysicEngine() {
        return this.physicEngine;
    }

    public SpacePartition getSpacePartition() {
        return spacePartition
    }
}