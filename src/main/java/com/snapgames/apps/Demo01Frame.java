package com.snapgames.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class for Project {@link Demo01Frame}
 *
 * <p>This class is the main class for a java game template. It initialize default components and services
 * to make a basic 2D game with a standard game loop:
 * <ul>
 *     <li><code>input</code> to capture and process gamer inputs,</li>
 *     <li><code>update</code> to compute {@link Entity} moves and {@link Behavior}'s into the game {@link World}</li>
 *     <li><code>render</code> to draw ann {@link Entity}'son internal buffer, then sync on the {@link JFrame} with a buffer strategy</li>
 * </ul></p>
 *
 * <p>It also provide some subclasses as components:
 * <ul>
 *     <li>{@link Entity} is the basic default Entity for a game object,</li>
 *     <li>{@link World} defines the Game context with  its play area and gravity,</li>
 *     <li>{@link Material} set physics constants for a specific material behavior assign to an {@link Entity},</li>
 *     <li>{@link Behavior} is an interface API to enhance {@link Entity} default processing with new specific behavior.</li>
 * </ul></p>
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Demo01Frame extends JPanel implements KeyListener {

    /**
     * <p>The {@link Entity} class is the Core object for any Scene.</p>
     *
     * <p>Each on-screen moving (or not) object is
     * an Entity.  the game loop will take care of it and update its position accordingly to physical
     * constraints applied on.</p>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Entity extends Rectangle2D.Double {
        private static int index = 0;
        public int id = index++;
        public String name = "entity_" + id;

        // velocity
        public double dx, dy;

        public int priority = 0;
        public boolean active = true;

        public Color borderColor = Color.BLACK;
        public Color fillColor = Color.BLUE;

        // acceleration
        public double ax, ay;
        // forces
        public List<Point2D> forces = new ArrayList<>();
        // Material
        public Material material = Material.DEFAULT;
        // mass
        public double mass = 1.0;

        public boolean stickToCamera = false;

        public List<Behavior> behaviors = new ArrayList<>();

        private Map<String, Object> attributes = new HashMap<>();

        public Shape shape = new Rectangle2D.Double();

        public Entity(String name) {
            this.name = name;
        }

        public Entity setPosition(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Entity setSize(double w, double h) {
            this.width = w;
            this.height = h;
            return this;
        }

        public Entity setVelocity(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
            return this;
        }

        public Entity setAcceleration(double ax, double ay) {
            this.ax = ax;
            this.ay = ay;
            return this;
        }

        public Entity setPriority(int p) {
            this.priority = p;
            return this;
        }

        public Entity setStickToCamera(boolean s) {
            this.stickToCamera = s;
            return this;
        }

        public Entity setActive(boolean a) {
            this.active = a;
            return this;
        }

        public Entity setBorderColor(Color c) {
            this.borderColor = c;
            return this;
        }

        public Entity setFillColor(Color c) {
            this.fillColor = c;
            return this;
        }

        public boolean isActive() {
            return active;
        }

        public boolean isStickToCamera() {
            return this.stickToCamera;
        }

        public Entity setMaterial(Material m) {
            this.material = m;
            return this;
        }

        public Entity setMass(double m) {
            this.mass = m;
            return this;
        }

        public <T> void setAttribute(String attrName, T attrValue) {
            attributes.put(attrName, attrValue);
        }

        public <T> T getAttribute(String attrName, T defaultValue) {
            return (T) attributes.getOrDefault(attrName, defaultValue);
        }

        public boolean isAttribute(String attrName) {
            return attributes.containsKey(attrName);
        }

        public void removeAttribute(String attrName) {
            attributes.remove(attrName);
        }

    }

    /**
     * <p>The {@link Behavior} interface allows new behaviors processing to any Entity when defining the Scene.</p>
     *
     * <p>Each behavior linked to an Entity will be processed during the 4 steps of the game loop:
     * <code>create</code>, <code>input</code>, <code>update</code> and <code>render</code>.</p>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public interface Behavior {
        void create(Demo01Frame app);

        void input(Demo01Frame app, Entity e);

        void update(Demo01Frame app, Entity e, double elapsed);

        void draw(Demo01Frame app, Entity e, Graphics2D g);
    }

    /**
     * <p>The {@link World} object helps define the context where all the Entity's instances will evolve during loop.</p>
     *
     * <p>It defines te play area and the current gravity to be applied to all objects contained by the play area.</p>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class World {
        public String name = "default_world";
        public Rectangle2D playArea = new Rectangle2D.Double(0, 0, 640, 480);
        public double gravity = 0.981;
        public Material material = Material.DEFAULT;
        public Color playAreaColor = new Color(0.0f, 0.0f, 0.3f);


        public World(String name) {
            this.name = name;
        }

        public World(String name, double gravity, Rectangle2D.Double playArea, Material playAreaLimitMaterial) {
            this.name = name;
            this.gravity = gravity;
            this.playArea = playArea;
            this.material = playAreaLimitMaterial;
        }
    }

    /**
     * <p>The {@link Material} object is used to set the material's constants used by an {@link Entity} during te physic processing.</p>
     *
     * <p>The material define first a name for a material, the density, the elasticity and the roughness for contact.</p>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Material {
        public static Material DEFAULT = new Material("default", 1.0, 1.0, 1.0);
        public String name;
        public double density;
        public double elasticity;
        public double roughness;

        public Material(String name, double d, double e, double r) {
            this.name = name;
            this.density = d;
            this.elasticity = e;
            this.roughness = r;
        }
    }

    /**
     * <p>The {@link TextObject} is an enhanced {@link Entity} used to display Text on screen.</p>
     *
     * <p>It adds a <code>text</code> and a <code>value</code> to the {@link Entity} Attributes to be displayed on screen.
     * A simple text can be used &nd it will be directly drawn on screen. If a value object is set, the text must
     * contain the {@link String#format(String, Object...)} conversion operation to be applied on to be displayed.</p>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class TextObject extends Entity {
        public String text;
        public Object value;
        public Font font;

        public TextObject(String name) {
            super(name);
        }

        public TextObject setText(String t) {
            this.text = t;
            return this;
        }

        public TextObject setValue(Object t) {
            this.value = t;
            return this;
        }

        public String getText() {
            if (text.contains("%") && value != null) {
                return String.format(text, value);
            }
            return text;
        }

        public TextObject setFont(Font f) {
            this.font = f;
            return this;
        }

    }

    /**
     * The {@link Camera} object will be used to track a {@link Camera#target}
     * The targeted {@link Entity} will be keep on the display center according the {@link Camera#tweenFactor}.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Camera extends Entity {
        private Entity target;
        private double tweenFactor;
        private Rectangle2D viewport = new Rectangle2D.Double();

        public Camera(String name) {
            super(name);
        }

        public Camera setTarget(Entity target) {
            this.target = target;
            return this;
        }

        public Camera setTweenFactor(double tf) {
            this.tweenFactor = tf;
            return this;
        }

        public void update(double dt) {
            if (Optional.ofNullable(target).isPresent()) {
                this.x += Math
                    .ceil((target.x + (target.width * 0.5) - ((viewport.getWidth()) * 0.5) - this.x)
                        * tweenFactor * Math.min(dt, 10));
                this.y += Math
                    .ceil((target.y + (target.height * 0.5) - ((viewport.getHeight()) * 0.5) - this.y)
                        * tweenFactor * Math.min(dt, 10));

                this.viewport.setRect(this.x, this.y, this.getWidth(),
                    this.getHeight());
            }
        }
    }

    private ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private Properties config = new Properties();
    private static boolean exit = false;
    private static int debug = 0;
    private static String debugFilter = "";
    private static String loggerFilter = "ERR,WARN,INFO";

    private JFrame window;
    private BufferedImage buffer;
    private int FPS = 60;

    private boolean[] keys = new boolean[1024];

    private World world = new World("earth", 0.981, new Rectangle2D.Double(), Material.DEFAULT);

    private Map<String, Entity> entities = new ConcurrentHashMap<>();
    private Camera activeCamera;

    private Color backGroundColor = Color.BLACK;

    /**
     * Create the Demo01Frame class and identify the current java context.
     */
    public Demo01Frame() {
        info("Initialization application %s (%s) %n- running on JDK %s %n- at %s %n- with classpath = %s%n",
            messages.getString("app.name"),
            messages.getString("app.version"),
            System.getProperty("java.version"),
            System.getProperty("java.home"),
            System.getProperty("java.class.path"));
    }

    public void run(String[] args) {
        init(args);
        initializeDisplay();
        createScene();
        loop();
        dispose();
    }

    /*----- Initialization and configuration -----*/

    public void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        lArgs.forEach(s -> {
            info(String.format("Configuration|Argument: %s", s));
            String[] keyVal = s.split("=");
            switch (keyVal[0]) {
                case "window", "w" -> {
                    config.setProperty("app.window.size", keyVal[1]);
                }
                case "buffer", "b" -> {
                    config.setProperty("app.render.buffer", keyVal[1]);
                }
                case "title", "t" -> {
                    config.setProperty("app.window.title", keyVal[1]);
                }
                case "exit", "x" -> {
                    config.setProperty("app.exit", keyVal[1]);

                }
            }
        });
        try {
            loadConfiguration();
            parseConfiguration();
        } catch (IOException e) {
            info("Configuration|Unable to read configuration file: %s", e.getMessage());
        }
    }

    public void parseConfiguration() {
        // set the default FPS for the game
        FPS = Integer.parseInt(config.getProperty("app.render.fps", "60"));
        // is exit because of test mode requested ?
        exit = Boolean.parseBoolean(config.getProperty("app.exit", "false"));
        debug = Integer.parseInt(config.getProperty("app.debug.level", "0"));
        // create the window
        window = new JFrame(config.getProperty("app.window.title", "Demo01"));
        window.setPreferredSize(new Dimension(
            Integer.parseInt(config.getProperty("app.window.width", "640")),
            Integer.parseInt(config.getProperty("app.window.height", "480"))
        ));
        // create the drawing buffer
        buffer = new BufferedImage(
            Integer.parseInt(config.getProperty("app.render.buffer.width", "320")),
            Integer.parseInt(config.getProperty("app.render.buffer.height", "240")),
            BufferedImage.TYPE_INT_ARGB
        );
        // world size
        world.playArea = new Rectangle2D.Double(0, 0,
            Integer.parseInt(config.getProperty("app.world.play.area.width", "320")),
            Integer.parseInt(config.getProperty("app.world.play.area.height", "240"))
        );
        // world gravity
        world.gravity = Double.parseDouble(config.getProperty("app.world.gravity", "0.0981"));
    }

    public void loadConfiguration() throws IOException {
        config.load(this.getClass().getResourceAsStream("/config.properties"));
        config.forEach((k, v) -> {
            info("Configuration| key [%s]=[%s]", k, v);
        });
    }

    public void initializeDisplay() {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(this);
        window.pack();
        window.createBufferStrategy(3);
        window.addKeyListener(this);
        window.setVisible(true);
    }

    /*----- Manage current Scene -----*/

    public void createScene() {
        Font textFont = null;
        try {
            textFont = Font.createFont(
                Font.TRUETYPE_FONT,
                this.getClass().getResourceAsStream("/fonts/upheavtt.ttf"));

        } catch (FontFormatException | IOException e) {
            error("Unable to read font file:%s", e.getMessage());
        }
        add(new TextObject("score")
            .setText("%05d")
            .setValue(0)
            .setFont(textFont.deriveFont(18.0f))
            .setPosition(20, 32)
            .setBorderColor(Color.WHITE)
            .setStickToCamera(true)
        );

        Entity player = new Entity("player")
            .setPosition(world.playArea.getWidth() * 0.5,
                world.playArea.getHeight() * 0.5)
            .setSize(16, 16)
            .setPriority(200)
            .setMaterial(new Material("Player_MAT", 1.0, 0.998, 0.998))
            .setMass(80.0);
        add(player);

        for (int i = 0; i < 100; i++) {
            add(new Entity("enemy_" + i)
                .setPosition(world.playArea.getWidth() * Math.random(),
                    world.playArea.getHeight() * Math.random())
                .setSize(8, 8)
                .setPriority(100 + i)
                .setFillColor(Color.RED)
                .setAcceleration(0.025 - (Math.random() * 0.05), 0.025 - (Math.random() * 0.05))
                .setMaterial(new Material("Enemy_MAT", 1.0, 1.0, 1.0))
                .setMass(2.0 + (5.0 * Math.random())));
        }
        setActiveCamera((Camera)
            new Camera("cam01")
                .setTarget(player)
                .setTweenFactor(0.002)
                .setSize(320, 240));
    }

    /**
     * Define the current active {@link Camera}.
     *
     * @param cam the new {@link Camera} to activate.
     */
    private void setActiveCamera(Camera cam) {
        this.activeCamera = cam;

    }

    /**
     * Reset current Scene.
     */
    public void resetScene() {
        entities.clear();
        createScene();
    }

    public void add(Entity entity) {
        entities.put(entity.name, entity);
    }

    /*----- Game loop -----*/

    public void loop() {
        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        long delay = 1;

        long updateFrames = 0;
        long updateTime = 0;
        long currentUPS = 0;

        long renderTime = 0;
        long renderFrames = 0;
        long currentFPS = 0;

        Map<String, Object> stats = new ConcurrentHashMap<>();
        while (!exit) {
            startTime = System.currentTimeMillis();
            input();
            delay = startTime - previousTime;
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
            render(stats);
            try {
                Thread.sleep(delay > 1000 / FPS ? 1 : 1000 / FPS - delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            previousTime = startTime;
            stats.put("fps", currentFPS);
            stats.put("ups", currentUPS);
            stats.put("frame", delay);
        }
    }

    public void input() {
        Entity player = entities.get("player");
        double speed = 0.025;
        if (isKeyPressed(KeyEvent.VK_UP)) {
            player.forces.add(new Point2D.Double(0, -(speed * 2.0)));
        }
        if (isKeyPressed(KeyEvent.VK_DOWN)) {
            player.forces.add(new Point2D.Double(0, speed));
        }
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            player.forces.add(new Point2D.Double(-speed, 0));
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.forces.add(new Point2D.Double(speed, 0));
        }
    }

    public void update(double delay) {
        // update all entities not stick to activeCamera.
        entities.values().stream()
            .filter(e -> !e.isStickToCamera())
            .forEach(e -> {
                applyPhysics(delay, e);
                controlPlayAreaBoundaries(e);
            });
        // update camera position
        if (Optional.ofNullable(activeCamera).isPresent()) {
            activeCamera.update(delay);
        }
    }

    /**
     * The applyPhysics method updates the physics properties of an {@link Entity}
     * object based on the forces acting on it, the delay time,
     * and the {@link Entity}'s material properties.
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
        e.forces.add(new Point2D.Double(0, world.gravity * 0.1));
        for (Point2D f : e.forces) {
            e.ax += f.getX();
            e.ay += f.getY();
        }
        e.ax = Math.abs(e.ax) > 1.0 ? Math.signum(e.ax) : e.ax;
        e.ay = Math.abs(e.ay) > 1.0 ? Math.signum(e.ay) : e.ay;

        e.dx = e.ax / delay;
        e.dy = e.ay * e.mass / delay;

        e.dx = Math.abs(e.dx) > 4.0 ? Math.signum(e.dx) : e.dx;
        e.dy = Math.abs(e.dy) > 4.0 ? Math.signum(e.dy) : e.dy;

        e.ax *= e.material.roughness;
        e.ay *= e.material.roughness;

        e.x += e.dx * delay;
        e.y += (e.dy) * delay;

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
                e.ax = 0.0;
            }
            if (e.y < 0.0) {
                e.y = 0.0;
                e.dy = -e.dy * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ay = 0.0;
            }
            if (e.x > world.playArea.getWidth() - e.width) {
                e.x = world.playArea.getWidth() - e.width;
                e.dx = -e.dx * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ax = 0.0;
            }
            if (e.y > world.playArea.getHeight() - e.height) {
                e.y = world.playArea.getHeight() - e.height;
                e.dy = -e.dy * e.material.elasticity * world.material.roughness * world.material.elasticity;
                e.ay = 0.0;
            }
        }
    }

    /**
     * The render method is responsible for drawing the current state of
     * the game onto the screen. It prepares the graphics context,
     * applies rendering hints, clears the screen, and then draws all active entities,
     * adjusting for camera position and ensuring proper layering.
     * <ul>
     *     <li>Create a Graphics2D object from the buffer and set rendering hints.</li>
     *     <li>Clear the screen with the background color.</li>
     *     <li>Adjust the graphics context to center the player entity.</li>
     *     <li>Draw the play area boundaries.</li>
     *     <li>Draw all active entities that are not fixed to the camera.</li>
     *     <li>Reset the graphics context and draw entities fixed to the camera.</li>
     *     <li>Draw the buffer to the window and display it.</li>
     * </ul>
     */
    public void render(Map<String, Object> stats) {
        Graphics2D g = buffer.createGraphics();
        g.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        g.setBackground(backGroundColor);
        g.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
        // move Camera
        Entity player = entities.get("player");
        if (Optional.ofNullable(activeCamera).isPresent()) {
            g.translate(-activeCamera.x, -activeCamera.y);
        }
        // draw play area limits
        if (debug > 0) {
            g.setColor(Color.GRAY);
            g.drawRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());
        }
        g.setColor(world.playAreaColor);
        g.fillRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());

        //draw everything
        entities.values().stream().filter(e -> e.isActive() && !e.isStickToCamera())
            .sorted(Comparator.comparingInt(a -> a.priority))
            .forEach(e -> {
                drawEntity(e, g);
            });
        if (Optional.ofNullable(activeCamera).isPresent()) {
            g.translate(activeCamera.x, activeCamera.y);
        }
        // draw all objects stick to the Camera.
        entities.values().stream().filter(e -> e.isActive() && e.isStickToCamera())
            .sorted(Comparator.comparingInt(a -> a.priority))
            .forEach(e -> {
                drawEntity(e, g);
            });
        g.dispose();

        Graphics g2s = window.getBufferStrategy().getDrawGraphics();
        g2s.drawImage(buffer, 0, 0, window.getWidth(), window.getHeight(),
            0, 0, buffer.getWidth(), buffer.getHeight(), null);
        if (debug > 0) {
            g2s.setColor(Color.ORANGE);
            g2s.drawString(String.format("[ fps:%d / ups:%d / e:%d / nbObj:%d / active:%d]",
                    stats.get("fps"),
                    stats.get("ups"),
                    stats.get("delay"),
                    (long) entities.values().size(),
                    (long) entities.values().stream().filter(Entity::isActive).count()),
                10, window.getHeight() - 10
            );
        }
        g2s.dispose();
        window.getBufferStrategy().show();
    }

    /*----- objects rendering -----*/

    public void drawEntity(Entity e, Graphics2D g) {
        switch (e.getClass().getSimpleName()) {
            case "Entity" -> {
                g.setColor(e.fillColor);
                g.fill(e);
                g.setColor(e.borderColor);
                g.draw(e);
            }
            case "TextObject" -> {
                TextObject te = (TextObject) e;
                g.setColor(te.borderColor);
                g.setFont(te.font);
                g.drawString(te.getText(), (int) te.getX(), (int) te.getY());
            }
        }
    }

    /*----- releasing objects and resources -----*/

    public void dispose() {
        window.dispose();
        info("End of application ");
    }

    /*----- Game start entry point -----*/

    public static void main(String[] argc) {
        Demo01Frame app = new Demo01Frame();
        app.run(argc);
    }

    /*----- Logger API -----*/

    public static void log(String level, String message, Object... args) {
        if (loggerFilter.contains(level)) {
            String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            System.out.printf(dateFormatted + "|" + level + "|" + message + "%n", args);
        }
    }

    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    public static void error(String message, Object... args) {
        log("ERR", message, args);
    }


    /*----- manage keys input -----*/
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        switch (e.getKeyCode()) {
            // exit application on ESCAPE
            case KeyEvent.VK_ESCAPE -> {
                exit = true;
            }
            // reset the scene on CTRL+Z
            case KeyEvent.VK_Z -> {
                if (e.isControlDown()) {
                    resetScene();
                }
            }
            case KeyEvent.VK_G -> {
                if (e.isControlDown()) {
                    world.gravity *= -1;
                }
            }
            default -> {
                // Nothing to do here.
            }
        }

    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

}