package com.snapgames.apps.desktop.game;

import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;
import com.snapgames.apps.desktop.game.gfx.Renderer;
import com.snapgames.apps.desktop.game.physic.Material;
import com.snapgames.apps.desktop.game.physic.World;
import com.snapgames.apps.desktop.game.scenes.PlayScene;
import com.snapgames.apps.desktop.game.scenes.Scene;
import com.snapgames.apps.desktop.game.scenes.TitleScene;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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
     * Filtering debug information output on console based on debug info level.
     */
    private static String loggerFilter = "ERROR,WARN,INFO";

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
     * A Map of all game's {@link Scene}.
     */
    private Map<String, Scene> scenes = new ConcurrentHashMap<>();
    /**
     * current Active scene.
     */
    private Scene currentScene;
    /**
     * Internal buffer for key states
     */
    private boolean[] keys = new boolean[1024];
    /**
     * World default instance to define a play area, a gravity and a Material.
     */
    private World world = new World("earth", 0.981, new Rectangle2D.Double(), Material.DEFAULT);


    private com.snapgames.apps.desktop.game.gfx.Renderer renderer;

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
        createScene();
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

    public void createScene() {
        add(new PlayScene(this, "play"));
        add(new TitleScene(this, "title"));
        activateScene("title");
    }

    /**
     * Add a new {@link Scene} implementation to the Game.
     *
     * @param scene the new {@link Scene}.
     */
    private void add(Scene scene) {

        scenes.put(scene.getName(), scene);
        scene.load(this);
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
            currentScene.deactivate(this);
        }
        setCurrentScene(scenes.get(sceneName));
        currentScene.create(this);
        currentScene.activate(this);
    }

    /**
     * Switch visibility of the {@link Entity} <code>e</code> to the required <code>visible</code> status.
     *
     * <p>The {@link Entity} and its child are set to active, and the corresponding behaviors for the
     * {@link Entity} and all its child will be applied</p>
     * <ul>
     *     <li>{@link Behavior#onActivate(Game, Entity)} if {@link Entity} is set to visible,</li>
     *      <li>{@link Behavior#onDeactivate(Game, Entity)} if visibility of the {@link Entity} is unset.</li>
     * </ul>
     *
     * @param e       the {@link Entity} to set as visible.
     * @param visible if true, the {@link Entity} <code>e</code> will be visible.
     */
    public void setVisible(Entity e, boolean visible) {
        e.setActive(visible);
        e.setChildVisible(visible);
        if (!visible) {
            e.behaviors.forEach(c -> c.onDeactivate(this, e));
            e.child.forEach(c -> c.behaviors.forEach(b -> b.onDeactivate(this, e)));
        } else {
            e.behaviors.forEach(c -> c.onActivate(this, e));
            e.child.forEach(c -> c.behaviors.forEach(b -> b.onActivate(this, e)));
        }
    }


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

    /**
     * Reset current Scene.
     */
    public void resetScene() {
        currentScene.reset();
        createScene();
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
            update(delay);

            renderTime += delay;
            if (renderTime > 1000) {
                currentFPS = renderFrames;
                renderFrames = 0;
                renderTime = 0;
            } else {
                renderFrames++;
            }
            renderer.draw(currentScene, stats);

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

    private static boolean isPause() {
        return pause;
    }

    /**
     * Process all input management on the current scene {@link Entity}'s.
     */
    public void input() {
        currentScene.getEntities().values()
                .stream().filter(Entity::isActive)
                .forEach(this::processInputBehaviorForEntity);
    }

    /**
     * Apply all the {@link Behavior#input()} to the {@link Entity}.
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
     * Update all entities from the current scene
     *
     * <p>It will refresh their status, position, velocity and acceleration, and active state.</p>
     *
     * @param delay The elapsed time since previous call.
     */
    public void update(double delay) {
        // update all entities not stick to activeCamera.
        currentScene.getEntities().values()
                .forEach(e -> {
                    updateEntity(delay, e);
                });
        // update camera position
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().update(delay);
            currentScene.getActiveCamera().behaviors.forEach(b -> {
                b.update(this, currentScene.getActiveCamera(), delay);
            });
        }

        // update camera position
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().update(delay);
            currentScene.getActiveCamera().behaviors.forEach(b -> {
                b.update(this, currentScene.getActiveCamera(), delay);
            });
        }
    }

    /**
     * According to the {@link Entity} state and nature,
     *
     * <p>This {@link Entity} will be fully updated on physics, state and collision.</p>
     *
     * @param delay the elapsed time since previous call (in ms)
     * @param e     the {@link Entity} instance to be updated.
     */
    private void updateEntity(double delay, Entity e) {
        if (!e.isRelativeToCamera() && !isPause()) {
            applyPhysics(delay, e);
            controlPlayAreaBoundaries(e);
        }
        e.update(this, delay);
        e.behaviors.forEach(b -> {
            b.update(this, e, delay);
        });
        // proceed with child entities (if any).
        e.child.forEach(c -> updateEntity(delay, c));
    }

    /**
     * The {@link Game#applyPhysics(double, Entity)} method updates the
     * physics properties of an {@link Entity} object based on the forces acting
     * on it, the delay time, and the {@link Entity}'s material properties.
     *
     * <ul>
     *     <li>Adds gravity to the entity's forces.</li>
     *     <li>Accumulates all forces to update the entity's acceleration.</li>
     *     <li>Limits the acceleration to a maximum of 1.0.</li>
     *     <li>Calculates the velocity based on the acceleration and delay.</li>
     *     <li>Limits the velocity to a maximum of 4.0.</li>
     *     <li>Applies material roughness to the acceleration.</li>
     *     <li>Updates the entity's position based on the velocity and delay.</li>
     *     <li>Clears the forces acting on the entity.</li>
     * </ul>
     *
     * @param delay the elapsed time since previous call.
     * @param e     the Entity to be updated
     */
    public void applyPhysics(double delay, Entity e) {
        // add World's gravity.
        e.forces.add(new Point2D.Double(0, world.gravity * 0.1));
        // apply all forces
        for (Point2D f : e.forces) {
            e.ax += f.getX();
            e.ay += f.getY();
        }

        // compute resulting acceleration
        e.ax = Math.abs(e.ax) > 1.0 ? Math.signum(e.ax) : e.ax;
        e.ay = Math.abs(e.ay) > 1.0 ? Math.signum(e.ay) : e.ay;

        //compute resulting velocity
        e.dx = e.ax / delay;
        e.dy = e.ay * e.mass / delay;
        e.dx = Math.abs(e.dx) > 4.0 ? Math.signum(e.dx) : e.dx;
        e.dy = Math.abs(e.dy) > 4.0 ? Math.signum(e.dy) : e.dy;

        // apply possible material characteristics on acceleration
        e.ax *= e.material.roughness;
        e.ay *= e.material.roughness;

        // compute new position.
        e.x += e.dx * delay;
        e.y += (e.dy) * delay;

        // reset forces applied to the object.
        e.forces.clear();
    }

    /**
     * The controlPlayAreaBoundaries method ensures that an {@link Entity} remains
     * within the defined play area boundaries.
     * If the entity moves outside the play area, its position and velocity are adjusted
     * to keep it within bounds, applying elasticity and roughness properties
     * of the entity's {@link Material}.
     *
     * @param e the Entity to be checked and corrected.
     */
    public void controlPlayAreaBoundaries(Entity e) {
        if (!world.playArea.contains(e)) {
            if (e.x < 0.0) {
                e.x = 0.0;
                e.dx = -e.dx * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ax = -e.ax * e.material.elasticity * world.material.roughness * world.material.elasticity;
            }
            if (e.y < 0.0) {
                e.y = 0.0;
                e.dy = -e.dy * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ay = -e.ay * e.material.elasticity * world.material.roughness * world.material.elasticity;
            }
            if (e.x > world.playArea.getWidth() - e.width) {
                e.x = world.playArea.getWidth() - e.width;
                e.dx = -e.dx * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ax = -e.ax * e.material.elasticity * world.material.roughness * world.material.elasticity;
            }
            if (e.y > world.playArea.getHeight() - e.height) {
                e.y = world.playArea.getHeight() - e.height;
                e.dy = -e.dy * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ay = -e.ay;
            }
        }
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

    public static void log(String level, String message, Object... args) {
        if (loggerFilter.contains(level)) {
            String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            System.out.printf(dateFormatted + "|" + level + "|" + message + "%n", args);
        }
    }

    public static void debug(String message, Object... args) {
        log("DEBUG", message, args);
    }

    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    public static void error(String message, Object... args) {
        log("ERROR", message, args);
    }


    /*----- manage keys input -----*/
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent k) {
        keys[k.getKeyCode()] = true;
        currentScene.getEntities().values().stream()
                .filter(Entity::isActive)
                .filter(e -> !e.behaviors.isEmpty())
                .forEach(e -> {
                    e.behaviors.forEach(b -> {
                        b.onKeyPressed(this, e, k);
                    });
                });
    }

    @Override
    public void keyReleased(KeyEvent k) {
        keys[k.getKeyCode()] = false;
        currentScene.getEntities().values().stream()
                .filter(Entity::isActive)
                .filter(e -> !e.behaviors.isEmpty())
                .forEach(e -> {
                    e.behaviors.forEach(b -> {
                        b.onKeyReleased(this, e, k);
                    });
                });
        currentScene.getBehaviors().forEach(b -> b.onKeyReleased(this, null, k));
        switch (k.getKeyCode()) {
            // reset the scene on CTRL+Z
            case KeyEvent.VK_Z -> {
                if (k.isControlDown()) {
                    resetScene();
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

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    /*----- Mouse event management -----*/

    @Override
    public void mouseClicked(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            debug("Entity %s has been clicked", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMouseClick(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    private Optional<Entity> getEntityUnderMouse(double mouseX, double mouseY) {
        Optional<Entity> entityClicked = currentScene.getEntities().values().stream()
                .filter(entity -> Arrays.stream(entity.getClass().getInterfaces()).filter(i -> i.equals(UIObject.class)).findFirst().isPresent()
                        && entity.isActive()
                        && entity.contains(mouseX, mouseY)).sorted((a, b) -> Integer.compare(b.priority, a.priority)).findFirst();

        return entityClicked;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            debug("Entity %s has been pressed", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMousePressed(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

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
    public Properties getConfig() {
        return config;
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

    public World getWorld() {
        return this.world;
    }

    public void setExitRequest(boolean x) {
        exit = x;
    }

    public static void setPause(boolean p) {
        pause = p;
    }
}