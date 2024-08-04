package com.snapgames.apps;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Main class for Project {@link Demo01Frame}
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
 *     <li>{@link Entity} is the basic default Entity for a game object,</li>
 *     <li>{@link World} defines the Game context with  its play area and gravity,</li>
 *     <li>{@link Material} set physics constants for a specific material
 *     behavior assign to an {@link Entity},</li>
 *     <li>{@link Behavior} is an interface API to enhance {@link Entity}
 *     default processing with new specific behavior.</li>
 * </ul></p>
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Demo01Frame implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {


    /**
     * <p>The {@link Entity} class is the Core object for any Scene.</p>
     *
     * <p>Each on-screen moving (or not) object is an {@link Entity}. The game loop will take care of it.</p>
     *
     * <p>The {@link Entity}'s attributes like position (<code>x,y</code>), velocity (<code>dx,dy</code>)
     * and acceleration (<code>ax,ay</code>) are updated by the main game loop physic computation, according
     * to the <code>mass</code> and assigned {@link Material} and applied <code>forces</code>.
     * </p>
     *
     * <p>The {@link Entity} is drawn by the {@link Demo01Frame#render(Map)} and more precisely by
     * the {@link Demo01Frame#drawEntity(Entity, Graphics2D)}  operation.</p>
     *
     * <p>you can add som {@link Behavior} on the entity to enhance the different phases of the entity processing:</p>
     * <ul>
     *     <li><code>create</code> to enhance the just created {@link Entity}, you can reuse Behavior on different entities,</li>
     *     <li><code>input</code> to define specific input processing on keys for this {@link Entity},</li>
     *     <li><code>update</code> to add new processing on the standard {@link Entity} update operation,</li>
     *     <li><code>draw</code> will enhance the existing default rendering with additional draw operations,</li>
     *     <li><code>onKeyPressed</code> to add processing on key pressed event, </li>
     *     <li><code>onKeyReleased</code> to add processing on key released event.</li>
     * </ul>
     *
     * @author Frédéric Delorme
     * @see Behavior
     * @see Demo01Frame#update(double)
     * @see Demo01Frame#render(Map)
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

        // this Entity will be relative to camera viewport.
        public boolean relativeToCamera = false;

        // use for child entity for update/rendering operation.
        public boolean relativeToParent = false;

        // Enhance Entity with behaviors
        public List<Behavior> behaviors = new ArrayList<>();

        // add any attribute object to this entity.
        private Map<String, Object> attributes = new HashMap<>();

        public Shape shape = new Rectangle2D.Double();

        // this entity has children!
        public List<Entity> child = new ArrayList<>();
        private Entity parent;

        /**
         * Create a brand new {@link Entity} with its name.
         *
         * @param name name of this new {@link Entity}.
         */
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

        public Entity setRelativeToCamera(boolean s) {
            this.relativeToCamera = s;
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

        public boolean isRelativeToCamera() {
            return this.relativeToCamera;
        }

        public Entity setMaterial(Material m) {
            this.material = m;
            return this;
        }

        public Entity setMass(double m) {
            this.mass = m;
            return this;
        }

        public <T extends Object> void setAttribute(String attrName, T attrValue) {
            attributes.put(attrName, attrValue);
        }

        public <T extends Object> T getAttribute(String attrName, T defaultValue) {
            return (T) attributes.getOrDefault(attrName, defaultValue);
        }

        public boolean isAttribute(String attrName) {
            return attributes.containsKey(attrName);
        }

        public void removeAttribute(String attrName) {
            attributes.remove(attrName);
        }

        /**
         * Add a {@link Behavior} to this {@link Entity}.
         *
         * @param behavior the specific {@link Behavior} to be added.
         * @return the updated {@link Entity}.
         */
        public Entity add(Behavior behavior) {
            behaviors.add(behavior);
            return this;
        }

        /**
         * Add a child {@link Entity}
         *
         * @param c the child {@link Entity} to be added to.
         * @return the updated parent {@link Entity}.
         */
        public Entity add(Entity c) {
            child.add(c);
            c.setParent(this);
            return this;
        }

        private Entity setParent(Entity p) {
            this.parent = p;
            return this;
        }

        public Entity getParent() {
            return parent;
        }

        public boolean isRelativeToParent() {
            return this.relativeToParent;
        }

        public void setChildVisible(boolean b) {
            child.forEach(c -> c.setActive(b));
        }
    }

    /**
     * Possible {@link GameObject} nature
     *
     * <p>it can be one of the following nature per {@link GameObject}:
     * <ul>
     *     <li><code>DOT</code> a simple dot,</li>
     *     <li><code>LINE</code> a line,</li>
     *     <li><code>RECTANGLE</code> a rectangle,</li>
     *     <li><code>ELLIPSE</code> an ellipse,</li>
     *     <li><code>POLYGON</code> a polygone.</li>
     * </ul></p>
     */
    public enum GameObjectNature {
        /**
         * A simple dot at <code>(x,y)</code> of size <code>w</code> where <code>h=w</code>
         */
        DOT,
        /**
         * A line from <code>(x,y)</code> to <code>(x+w,y+h)</code>.
         */
        LINE,
        /**
         * A {@link Rectangle2D} at <code>(x,y)</code> of size <code>(w,h)</code>.
         */
        RECTANGLE,
        /**
         * An {@link java.awt.geom.Ellipse2D} at <code>(x,y)</code> with radius of <code>(rx=w,ry=h)</code>.
         */
        ELLIPSE,
        /**
         * A {@link Polygon} build of lines at <code>(x,y)</code>.Its size <code>(w,h)</code>
         * is computed at first display time.
         */
        POLYGON;
    }

    /**
     * A {@link GameObject} is an {@link Entity} with a specific {@link Shape}
     * according to its {@link GameObjectNature}.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class GameObject extends Entity {

        public GameObjectNature nature = GameObjectNature.RECTANGLE;

        /**
         * Create a brand new {@link GameObject} with its name.
         *
         * @param name name of this new {@link GameObject}.
         */
        public GameObject(String name) {
            super(name);
        }

        public GameObject setNature(GameObjectNature n) {
            this.nature = n;
            return this;
        }
    }

    /**
     * An {@link ImageObject} is basically an {@link Entity} supporting {@link BufferedImage} drawing.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class ImageObject extends Entity {

        public BufferedImage image;

        /**
         * Create a brand new {@link ImageObject} with its name.
         *
         * @param name name of this new {@link ImageObject}.
         */
        public ImageObject(String name) {
            super(name);
        }

        public ImageObject setImage(BufferedImage img) {
            this.image = img;
            return this;
        }

        public BufferedImage getImage() {
            return this.image;
        }
    }

    /**
     * {@link AnimationFrames} is a list of frames to animate a Sprite (coming soon) or an {@link ImageObject}.
     *
     * <p>It defines frames for each step of the animation with their display duration.</p>
     *
     * @author Frédéric Delorme
     * @see Animations
     * @since 1.0.0
     */
    public static class AnimationFrames {
        List<BufferedImage> frames = new ArrayList<>();
        List<Integer> timeFrames = new ArrayList<>();
        double elapsedTime = 0;
        int currentFrame = 0;

        AnimationFrames(List<BufferedImage> frames, List<Integer> timeFrames) {
            this.frames = frames;
            this.timeFrames = timeFrames;
        }

        /**
         * Update the currentFrame according to elapsed time since previous call.
         *
         * @param delay elapsed time since previous call.
         */
        public void update(double delay) {
            elapsedTime += delay;
            if (elapsedTime > timeFrames.get(currentFrame)) {
                currentFrame = (currentFrame + 1 < frames.size() ? currentFrame + 1 : 0);
                elapsedTime = 0;
            }
        }

        /**
         * Return the current active frame for this AnimationFrames.
         *
         * <p>According to elapsed time, it returns the corresponding frame for the {@link AnimationFrames}.</p>
         *
         * @return BufferedImage corresponding to the current active frame.
         */
        public BufferedImage getImage() {
            return this.frames.get(currentFrame);
        }


        /**
         * Reset this {@link AnimationFrames} to its first frame.
         */
        public void reset() {
            currentFrame = 0;
        }

        /**
         * Load all the frames from a broader image by slicing each frame from it.
         *
         * <p></p>source is image source and table is list of integer structured like [x,y,w,h,t] for each frame
         * to slice from the source image where
         * <ul>
         *     <li><code>(x,y)</code> is position of the frame in the source image,</li>
         *     <li><code>(w,h)</code> is the size of the extracted frame,</li>
         *     <li><code>(t)</code> is the duration for that frame.</li>
         * </ul>
         *
         * @param source source for images
         * @param table  list of [x,y,w,h,t] structures.
         * @return a brand new AnimationFrames instance.
         */
        public static AnimationFrames load(BufferedImage source, int[] table) {
            if (source != null) {
                List<BufferedImage> images = new ArrayList<>();
                List<Integer> timeFrames = new ArrayList<>();
                for (int idx = 0; idx < table.length; idx += 5) {
                    int x = table[idx];
                    int y = table[idx + 1];
                    int w = table[idx + 2];
                    int h = table[idx + 3];
                    int timeFrame = table[idx + 4];
                    images.add(source.getSubimage(x, y, w, h));
                    timeFrames.add(timeFrame);
                }
                return new AnimationFrames(images, timeFrames);
            }
            return null;
        }
    }

    /**
     * {@link Animations} are a set
     * of {@link AnimationFrames} indexed
     * with a readable name as <code>animationKey.</code>
     * <p>
     * <ul>
     *     <li>The {@link Animations#update(double)} method manages the frame cycling for the active {@link AnimationFrames}.</li>
     *     <li>{@link Animations#getImage()} will return the current active frame.</li>
     * </ul>
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Animations {
        Map<String, AnimationFrames> animationsFrames = new HashMap<>();
        private String activeAnimationKey = "default";
        private int currentImage;

        /**
         * Update the current active {@link AnimationFrames} according to elapsed time.
         *
         * @param elapsed the elapsed time since previous call.
         * @return the updated {@link Animations} instance.
         */
        public Animations update(double elapsed) {
            if (Optional.ofNullable(animationsFrames).isPresent()
                    && Optional.ofNullable(activeAnimationKey).isPresent()) {
                AnimationFrames anim = animationsFrames.get(activeAnimationKey);
                anim.update(elapsed);
            }
            return this;
        }

        /**
         * Define the current active animation.
         *
         * @param activeAnimationKey the key name for the {@link AnimationFrames} to be activated.
         */
        public void setActiveAnimation(String activeAnimationKey) {
            this.activeAnimationKey = activeAnimationKey;
        }

        /**
         * Return the current frame from the active AnimationFrames.
         *
         * @return the BufferedImage corresponding to the current active animation.
         */
        public BufferedImage getImage() {
            return this.animationsFrames.get(activeAnimationKey).getImage();
        }

        /**
         * Reset the current active {@link AnimationFrames} to its first frame.
         */
        public void reset() {
            if (Optional.ofNullable(animationsFrames).isPresent()
                    && Optional.ofNullable(activeAnimationKey).isPresent()) {
                this.animationsFrames.get(activeAnimationKey).reset();
            }
        }
    }

    /**
     * A new Entity supporting Animations.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class AnimatedObject extends Entity {

        private Animations animations = new Animations();

        /**
         * Create a brand new {@link AnimatedObject} with its name.
         *
         * @param name name of this new {@link AnimatedObject}.
         */
        public AnimatedObject(String name) {
            super(name);
        }

        public Animations getAnimations() {
            return animations;
        }

        public BufferedImage getImage() {
            return animations.getImage();
        }

        public void update(double elapsed) {
            animations.update(elapsed);
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
        /**
         * Create will help you customize the Entity creation.
         *
         * @param app
         */
        default void create(Demo01Frame app, Entity e) {
        }

        /**
         * On a specific {@link Entity}, you can add input processing.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         */
        default void input(Demo01Frame app, Entity e) {
        }

        /**
         * On a specific {@link Entity}, you can add enhance default update.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         */
        default void update(Demo01Frame app, Entity e, double elapsed) {
        }

        /**
         * On a specific {@link Entity}, you can enhance the draw processing.
         *
         * @param app the parent application.
         * @param e   the concerned {@link Entity}.
         * @param g   the {@link Graphics2D} API to use.
         */
        default void draw(Demo01Frame app, Entity e, Graphics2D g) {
        }

        /**
         * On a specific {@link Entity}, you can add key pressed processing.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         * @param k   the {@link KeyEvent} to be processed.
         */
        default void onKeyPressed(Demo01Frame app, Entity e, KeyEvent k) {
        }

        /**
         * On a specific {@link Entity}, you can add key released processing.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         * @param k   the {@link KeyEvent} to be processed.
         */
        default void onKeyReleased(Demo01Frame app, Entity e, KeyEvent k) {
        }

        /**
         * On activation of the {@link Entity}, this Behavior is processed.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         */
        default void onActivate(Demo01Frame app, Entity e) {
        }

        /**
         * On deactivation of the {@link Entity}, this Behavior is processed.
         *
         * @param app the parent application
         * @param e   the concerned {@link Entity}
         */
        default void onDeactivate(Demo01Frame app, Entity e) {
        }

        /**
         * On mouse entering the {@link Entity} area, this Behavior is processed.
         *
         * @param app    the parent application
         * @param e      the concerned {@link Entity}
         * @param mouseX mouse X position
         * @param mouseY mouse y position
         */
        default void onMouseIn(Demo01Frame app, Entity e, double mouseX, double mouseY) {
        }

        /**
         * On mouse moving out of the {@link Entity} area, this Behavior is processed.
         *
         * @param app    the parent application
         * @param e      the concerned {@link Entity}
         * @param mouseX mouse X position
         * @param mouseY mouse y position
         */
        default void onMouseOut(Demo01Frame app, Entity e, double mouseX, double mouseY) {
        }

        /**
         * On the mouse button clicked on the {@link Entity} area, this Behavior is processed.
         *
         * @param app      the parent application
         * @param e        the concerned {@link Entity}
         * @param mouseX   mouse X position
         * @param mouseY   mouse y position
         * @param buttonId the button number that has been clicked.
         */
        default void onMouseClick(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
        }

        /**
         * On the mouse button pressed on the {@link Entity} area, this Behavior is processed.
         *
         * @param app      the parent application
         * @param e        the concerned {@link Entity}
         * @param mouseX   mouse X position
         * @param mouseY   mouse y position
         * @param buttonId the button number that has been clicked.
         */
        default void onMousePressed(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
        }

        /**
         * On the mouse button released on the {@link Entity} area, this Behavior is processed.
         *
         * @param app      the parent application
         * @param e        the concerned {@link Entity}
         * @param mouseX   mouse X position
         * @param mouseY   mouse y position
         * @param buttonId the button number that has been clicked.
         */
        default void onMouseReleased(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
        }
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
        public Color textColor = Color.WHITE;

        public Align textAlign;

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

        public TextObject setTextColor(Color tc) {
            this.textColor = tc;
            return this;
        }

        public TextObject setTextAlign(Align a) {
            this.textAlign = a;
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
     * <p>The {@link Camera} object will be used to track a {@link Camera#target}</p>
     * <p>
     * The targeted {@link Entity} will be keep on the display center according the {@link Camera#tweenFactor}.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Camera extends Entity {
        private Entity target;
        private double tweenFactor;
        private Rectangle2D viewport = new Double();

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
                this.x += Math.ceil(
                        (target.getX() + (target.getWidth() * 0.5) - ((viewport.getWidth()) * 0.5) - this.getX())
                                * tweenFactor * Math.min(dt, 1));
                this.y += Math.ceil(
                        (target.getY() + (target.getHeight() * 0.5) - ((viewport.getHeight()) * 0.5) - this.getY())
                                * tweenFactor * Math.min(dt, 1));
                this.viewport.setRect(this);
            }
        }
    }

    /**
     * Define the alignment for the affected {@link Entity} relative to its <code>parent</code> one.
     *
     * <p>Align enumeration is used to set automatic position relatively to the
     * <code>parent</code> {@link Entity} with {@link AlignBehavior}</p>
     * <p>Example:
     * <pre>
     *     Button bt = new Button("MyButton")
     *       .setParent(myDialogBox)
     *       .setAlign(Align.LEFT);
     * </pre>
     * </p>
     * <p>This created button will be align on the internal left of the parent Entity,
     * taking care of {@link UIObject#margin} and {@link UIObject#padding}.</p>
     *
     * @see TextObject
     * @see Button
     * @see AlignBehavior
     */
    public enum Align {
        LEFT,
        RIGHT,
        CENTER,
        TOP,
        BOTTOM;
    }

    /**
     * This {@link Behavior} implementation is used to automatically move child {@link TextObject} and {@link Button}
     * to their new position according to the {@link Align} attribute.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class AlignBehavior implements Behavior {
        @Override
        public void update(Demo01Frame app, Entity e, double elapsed) {
            e.child.forEach(c -> {
                switch (c.getClass().getSimpleName()) {
                    case "Button", "TextBox" -> {
                        switch (((Button) c).align) {
                            case LEFT -> {
                                c.x = e.x + UIObject.margin + UIObject.padding;
                                c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                            }
                            case RIGHT -> {
                                c.x = (e.x + e.width) - (c.width + UIObject.margin + UIObject.padding);
                                c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                            }
                            case CENTER -> {
                                c.x = (e.x + (e.width * 0.5)) - (UIObject.margin + UIObject.padding);
                                c.y = e.y + e.height - (c.height + UIObject.margin + UIObject.padding);
                            }
                            default -> {
                                // processing TOP,BOTTOM will come later...
                            }
                        }
                    }
                    default -> {
                        // nothing to do thaaaaaa....
                    }
                }
            });
        }
    }

    /**
     * This interface defines internals parameters for any User Interface objects on screen.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public interface UIObject extends Behavior {
        /**
         * Default margin used for position and drawing of any UIObject
         */
        int margin = 2;
        /**
         * Default padding used for position and drawing of any UIObject
         */
        int padding = 2;

        /**
         * When mouse goes over a {@link UIObject}, the fill color for this hover object is set.
         */
        Color mouseOnColor = Color.LIGHT_GRAY;
        /**
         * When mouse goes over a {@link UIObject}, the border color for this hover object is set.
         */
        Color mouseOnBorderColor = Color.WHITE;

        /**
         * When a mouse button is pressed on a {@link UIObject}, the fill color for this object is set.
         */
        Color mousePressedColor = Color.CYAN;
        /**
         * When a mouse button is pressed on a {@link UIObject}, the text color for this object is set.
         */
        Color mousePressedTextColor = Color.BLUE;
        /**
         * When the mouse goes out of a {@link UIObject}, the fill color for this object is set.
         */
        Color mouseOutColor = Color.GRAY;
        /**
         * When the mouse goes out of a {@link UIObject}, the border color for this object is set.
         */
        Color mouseOutBorderColor = new Color(0.1f, 0.1f, 0.1f);

        /**
         * When a mouse button is released on a {@link UIObject}, the fill color for this object is set.
         */
        Color mouseReleasedColor = mouseOutColor;
        /**
         * When a mouse button is released on a {@link UIObject}, the text color for this object is set.
         */
        Color mouseReleasedTextColor = Color.WHITE;

        @Override
        default void onMousePressed(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
            e.setFillColor(mousePressedColor);
            if (e instanceof Button) {
                Button bt = (Button) e;
                bt.setTextColor(mousePressedTextColor);
            }
        }

        @Override
        default void onMouseReleased(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
            e.setFillColor(mouseReleasedColor);
            if (e instanceof Button) {
                Button bt = (Button) e;
                bt.setTextColor(mouseReleasedTextColor);
            }
        }

        @Override
        default void onMouseIn(Demo01Frame app, Entity e, double mouseX, double mouseY) {
            e.setFillColor(mouseOnColor);
            e.setBorderColor(mouseOnBorderColor);
        }

        @Override
        default void onMouseOut(Demo01Frame app, Entity e, double mouseX, double mouseY) {
            e.setFillColor(mouseOutColor);
            e.setBorderColor(mouseOutBorderColor);
        }
    }

    /**
     * A {@link DialogBox} entity will create a dialog with a text.
     * Adding child {@link Button} will add new operations
     * to activate some processing.
     * <p>
     * By default, a DialogBox is not active; it must be activated to be displayed.
     */
    public static class DialogBox extends TextObject implements UIObject {

        public DialogBox(String name) {
            super(name);
            setSize(100, 48);
            setPosition((buffer.getWidth() - this.width) * 0.5, (buffer.getHeight() - this.height) * 0.5);
            setVisible(false);
            setRelativeToCamera(true);
            setFillColor(Color.BLUE);
            setBorderColor(Color.CYAN);
            setTextColor(Color.WHITE);
            add(new AlignBehavior());
        }

        public void setVisible(boolean visible) {
            setActive(visible);
            setChildVisible(visible);
        }
    }

    /**
     * A {@link Button} is a {@link UIObject} to capture mouse click action from the user.
     * <p>
     * It can be a child of a {@link DialogBox} and the DialogBox defined {@link AlignBehavior} will be applied on it
     * to define its position.
     *
     * @author Frédéric Delorme
     * @since 1.0.0
     */
    public static class Button extends TextObject implements UIObject {
        public Align align;

        /**
         * Create a new {@link Button} named name with a default <code>align</code> set to LEFT.
         *
         * @param name the name for this new {@link Button} instance
         */
        public Button(String name) {
            super(name);
            setRelativeToCamera(true);
            setAlign(Align.LEFT);
        }

        /**
         * Set the Align value.
         *
         * @param a the new {@link Align} required for this {@link Button}.
         * @return the updated {@link Button} instance.
         */
        public Button setAlign(Align a) {
            this.align = a;
            return this;
        }

    }

    private static ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private Properties config = new Properties();

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
    private static int debug = 0;
    /**
     * (No used) Internal debug filtering on {@link Entity}'s name.
     */
    private static String debugFilter = "";
    /**
     * Filtering debug information output on console based on debug info level.
     */
    private static String loggerFilter = "ERROR,WARN,INFO";

    /**
     * The window containing the all Game display.
     */
    private JFrame window;
    /**
     * Rendering buffer where everything is drawn.
     */
    private static BufferedImage buffer;
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
    private double mouseX = 0;
    /**
     * Mouse vertical position on buffer
     */
    private double mouseY = 0;
    /**
     * Mouse horizontal position on the window
     */
    private int realMouseX;
    /**
     * Mouse vertical position on the window
     */
    private int realMouseY;
    /**
     * Previously focused Entity by mouse cursor.
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

    /**
     * Internal map of {@link Entity} for the active scene.
     */
    private Map<String, Entity> entities = new ConcurrentHashMap<>();
    /**
     * The current active {@link Camera} (is any).
     */
    private Camera activeCamera;

    /**
     * Default background buffer color for rendering processing.
     */
    private Color backGroundColor = Color.BLACK;

    /**
     * Internal scene Score value for HUD.
     */
    private int score = 0;
    /**
     * Internal life counter value for HUD.
     */
    private int lifeCount = 3;

    /**
     * Create the {@link Demo01Frame} instance and detect the current java context.
     */
    public Demo01Frame() {
        info("Initialization application %s (%s); running on JDK %s; at %s; with classpath = %s",
                messages.getString("app.name"),
                messages.getString("app.version"),
                System.getProperty("java.version"),
                System.getProperty("java.home"),
                System.getProperty("java.class.path"));
    }

    public void run(String[] args) {
        init(args);
        prepareDisplay(true);
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
        loadConfiguration("/config.properties");
        parseConfiguration();
        info("Configuration applied: %s", config.stringPropertyNames().stream()
                .map(key -> key + "=" + config.getProperty(key))
                .collect(Collectors.joining(", ")));
    }

    /**
     * Parse all the arguments from <code>args</code> and set default values into {@link Demo01Frame#config} as a configuration set.
     *
     * @param args the list of arguments to parse and set as default in the {@link Demo01Frame#config}.
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

    /**
     * Read the configuration file from the configFilePath in the JAR or for Test,
     * or directly from the JAR side external file configFilePath.
     *
     * @param configFilePath path to the configuration file to be loaded
     */
    public void loadConfiguration(String configFilePath) {

        try {
            Path rootPath = Paths.get(Demo01Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
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

    public void prepareDisplay(boolean fullScreen) {
        if (window.isActive()) {
            window.dispose();
        }
        window = new JFrame(config.getProperty("app.window.title", "Demo01"));
        window.setPreferredSize(new Dimension(
                Integer.parseInt(config.getProperty("app.window.width", "640")),
                Integer.parseInt(config.getProperty("app.window.height", "480"))));

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(getResource("/images/thor-hammer.png"));
        window.pack();
        // processing keyboard input
        window.addKeyListener(this);
        // processing mouse input
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
        window.addMouseWheelListener(this);
        // show window.
        window.setVisible(true);
        window.createBufferStrategy(3);
        if (fullScreen) {
            window.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    /*----- Manage current Scene -----*/

    public void createScene() {
        Font scoreFont = getResource("/fonts/upheavtt.ttf");
        Font textFont = getResource("/fonts/Minecraftia-Regular.ttf");

        add(new ImageObject("forest")
                .setImage(getResource("/images/backgrounds/forest.jpg"))
                .setPosition(0, 0)
                .setSize(world.playArea.getWidth(), world.playArea.getHeight())
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
                    public void input(Demo01Frame app, Entity e) {
                        ((TextObject) e).setValue(score);
                    }
                })
        );
        add(new ImageObject("heart")
                .setImage(getResource("/images/tiles01.png|0,96,16,16"))
                .setPosition(buffer.getWidth() - 40, 3)
                .setSize(16, 16)
                .setRelativeToCamera(true)
        );

        add(new TextObject("Life")
                .setText("%01d")
                .setValue(lifeCount)
                .setFont(textFont.deriveFont(8.0f))
                .setPosition(buffer.getWidth() - 32, 16)
                .setBorderColor(Color.WHITE)
                .setRelativeToCamera(true)
                .add(new Behavior() {
                    @Override
                    public void input(Demo01Frame app, Entity e) {
                        ((TextObject) e).setValue(lifeCount);
                    }
                })
        );

        generateEntities("enemy_", 20);

        Entity player = new Entity("player")
                .setPosition(world.playArea.getWidth() * 0.5,
                        world.playArea.getHeight() * 0.5)
                .setSize(16, 16)
                .setPriority(200)
                .setMaterial(new Material("Player_MAT", 1.0, 0.998, 0.98))
                .setMass(10.0)
                .add(new Behavior() {
                    @Override
                    public void input(Demo01Frame app, Entity player) {
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
                });
        add(player);

        generateEntities("enemy_", 20);

        setActiveCamera((Camera)
                new Camera("cam01")
                        .setTarget(player)
                        .setTweenFactor(0.01)
                        .setSize(buffer.getWidth(), buffer.getHeight())
                        .add(new Behavior() {
                            @Override
                            public void draw(Demo01Frame app, Entity e, Graphics2D g) {
                                g.setColor(Color.WHITE);
                                g.setFont(textFont.deriveFont(8.0f));
                                g.drawString(
                                        messages.getString("app.camera.name"),
                                        (int) world.playArea.getHeight(), (int) world.playArea.getHeight() - 20);
                            }
                        }));

        DialogBox exitConfirmation = (DialogBox) new DialogBox("exitConfirmBox")
                .setText(messages.getString("app.dialog.exit.message"))
                .setFont(textFont.deriveFont(8.0f))
                .setTextColor(Color.WHITE)
                .setSize(140, 40)
                .setFillColor(Color.DARK_GRAY)
                .setBorderColor(Color.BLACK)
                .setActive(false)
                .setPosition((buffer.getWidth() - 140) * 0.5, (buffer.getHeight() - 40) * 0.5)
                .setPriority(10)
                .add(new Behavior() {
                    @Override
                    public void onActivate(Demo01Frame app, Entity e) {
                        setPause(true);
                    }

                    @Override
                    public void onDeactivate(Demo01Frame app, Entity e) {
                        setPause(false);
                    }
                })
                .add(new UIObject() {
                    @Override
                    public void onKeyReleased(Demo01Frame app, Entity e, KeyEvent k) {
                        if (k.getKeyCode() == KeyEvent.VK_Y || k.getKeyCode() == KeyEvent.VK_SPACE) {
                            exit = true;
                        }
                        if (k.getKeyCode() == KeyEvent.VK_N || k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            exit = false;
                            setVisible(e, false);
                        }
                    }
                });
        add(exitConfirmation);

        // Add the required button OK
        Entity okButton = (Button) new Button("OK")
                .setAlign(Align.RIGHT)
                .setTextAlign(Align.CENTER)
                .setText(messages.getString("app.dialog.button.ok"))
                .setTextColor(Color.WHITE)
                .setFillColor(Color.LIGHT_GRAY)
                .setBorderColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new UIObject() {
                    @Override
                    public void onMouseClick(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
                        exit = true;
                        e.setFillColor(Color.CYAN);
                    }
                });

        // Add the required button Cancel
        Entity cancelButton = new Button("Cancel")
                .setAlign(Align.LEFT)
                .setText(messages.getString("app.dialog.button.cancel"))
                .setTextAlign(Align.CENTER)
                .setTextColor(Color.WHITE)
                .setFillColor(Color.LIGHT_GRAY)
                .setBorderColor(Color.GRAY)
                .setActive(false)
                .setSize(40, 12)
                .setPriority(20)
                .add(new UIObject() {
                    @Override
                    public void onMouseClick(Demo01Frame app, Entity e, double mouseX, double mouseY, int buttonId) {
                        exit = false;
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
    }

    /**
     * Switch visibility of the {@link Entity} <code>e</code> to the required <code>visible</code> status.
     *
     * <p>The {@link Entity} and its child are set to active, and the corresponding behaviors for the
     * {@link Entity} and all its child will be applied</p>
     * <ul>
     *     <li>{@link Behavior#onActivate(Demo01Frame, Entity)} if {@link Entity} is set to visible,</li>
     *      <li>{@link Behavior#onDeactivate(Demo01Frame, Entity)} if visibility of the {@link Entity} is unset.</li>
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
     * Retrieve from the cache the {@link Entity} with name <code>entityName</code>.
     *
     * @param entityName the name of the {@link Entity} to be retrieved from the scene cache.
     * @param <T>        the Entity extended type
     * @return the corresponding {@link Entity} with the name <code>entityName</code>.
     */
    public <T extends Entity> T getEntity(String entityName) {
        return (T) entities.get(entityName);
    }

    private void generateEntities(String rootName, int nbEntities) {
        for (int i = 0; i < nbEntities; i++) {
            add(new Entity(rootName + Entity.index)
                    .setPosition(world.playArea.getWidth() * Math.random(),
                            world.playArea.getHeight() * Math.random())
                    .setSize(8, 8)
                    .setPriority(100 + i)
                    .setFillColor(Color.RED)
                    .setAcceleration(0.25 - (Math.random() * 0.5), 0.25 - (Math.random() * 0.5))
                    .setMaterial(new Material("Enemy_MAT", 1.0, 0.98, 1.0))
                    .setMass(2.0 + (5.0 * Math.random())));
        }
    }

    /**
     * Define the current active {@link Camera}.
     * <p>
     * The defined {@link Camera}'s targeted {@link Entity} will be tracked on center of the Camera viewport
     * corresponding to the window center.
     *
     * @param cam the new {@link Camera} to activate.
     */
    private void setActiveCamera(Camera cam) {
        this.activeCamera = cam;

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
                            Demo01Frame.class.getResourceAsStream(path));
                }
                case "png", "jpg" -> {
                    if (path.contains("|")) {
                        String filePath = path.substring(0, path.lastIndexOf("|"));
                        BufferedImage img = ImageIO.read(Objects.requireNonNull(Demo01Frame.class.getResourceAsStream(filePath)));
                        String slice = path.substring(path.lastIndexOf("|") + 1);
                        String[] slices = slice.split(",");
                        return (T) img.getSubimage(
                                Integer.parseInt(slices[0]),
                                Integer.parseInt(slices[1]),
                                Integer.parseInt(slices[2]),
                                Integer.parseInt(slices[3])
                        );
                    } else {
                        return (T) ImageIO.read(Demo01Frame.class.getResourceAsStream(path));
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
        entities.clear();
        createScene();
    }

    /**
     * Add an {@link Entity} to the current scene.
     *
     * @param entity the new {@link Entity} to be added to the current scene.
     */
    public void add(Entity entity) {
        entity.behaviors.forEach(b -> {
            b.create(this, entity);
        });
        entities.put(entity.name, entity);
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
        long previousTime = startTime;
        long delay = 1;

        long updateFrames = 0;
        long updateTime = 0;
        long currentUPS = 0;

        long renderTime = 0;
        long renderFrames = 0;
        long currentFPS = 0;

        Map<String, Object> stats = new ConcurrentHashMap<>();
        do {
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
                Thread.sleep(delay > 1000 / UPS ? 1 : 1000 / UPS - delay);
            } catch (IllegalArgumentException iae) {
                error("Unable to wait for a negative number of ms !");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            previousTime = startTime;
            stats.put("fps", currentFPS);
            stats.put("ups", currentUPS);
            stats.put("ft", delay);
        } while (!exit);
    }

    private static boolean isPause() {
        return pause;
    }

    /**
     * Process all input management on the current scene {@link Entity}'s.
     */
    public void input() {
        entities.values().stream().filter(Entity::isActive).forEach(this::processInputBehaviorForEntity);
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
        entities.values()
                .forEach(e -> {
                    updateEntity(delay, e);
                });
        // update camera position
        if (Optional.ofNullable(activeCamera).isPresent()) {
            activeCamera.update(delay);
            activeCamera.behaviors.forEach(b -> {
                b.update(this, activeCamera, delay);
            });
        }

        // update camera position
        if (Optional.ofNullable(activeCamera).isPresent()) {
            activeCamera.update(delay);
            activeCamera.behaviors.forEach(b -> {
                b.update(this, activeCamera, delay);
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
        e.behaviors.forEach(b -> {
            b.update(this, e, delay);
        });
        // proceed with child entities (if any).
        e.child.forEach(c -> updateEntity(delay, c));
    }

    /**
     * The {@link Demo01Frame#applyPhysics(double, Entity)} method updates the
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
        Entity player = getEntity("player");
        if (Optional.ofNullable(activeCamera).isPresent()) {
            g.translate(-activeCamera.x, -activeCamera.y);
        }
        // draw play area
        g.setColor(world.playAreaColor);
        g.fillRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());

        //draw everything
        entities.values().stream().filter(e -> e.isActive() && !e.isRelativeToCamera())
                .sorted(Comparator.comparingInt(a -> a.priority))
                .forEach(e -> {
                    drawEntity(e, g);
                    if (isDebugAtLeast(3)) {
                        g.setColor(Color.ORANGE);
                        g.drawRect(
                                (int) e.getX(), (int) e.getY(),
                                (int) e.getWidth(), (int) e.getHeight());
                    }
                });

        // draw play area limits in debug mode
        if (isDebugAtLeast(1)) {
            g.setColor(Color.YELLOW);
            g.drawRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());
        }

        if (Optional.ofNullable(activeCamera).isPresent()) {
            g.translate(activeCamera.x, activeCamera.y);
        }

        // draw all objects stick to the Camera.
        entities.values().stream().filter(e -> e.isActive() && e.isRelativeToCamera())
                .sorted(Comparator.comparingInt(a -> a.priority))
                .forEach(e -> {
                    drawEntity(e, g);
                });

        // draw all Behaviors about active camera.
        if (Optional.ofNullable(activeCamera).isPresent()) {
            activeCamera.behaviors.forEach(b -> {
                b.draw(this, activeCamera, g);
            });
        }

        // keep mouse coordinates
        stats.put("mouse", "(" + mouseX + "," + mouseY + ")");
        if (isDebugAtLeast(1)) {
            g.setColor(Color.YELLOW);
            g.fillRect(
                    (int) mouseX,
                    (int) mouseY,
                    2, 2);
        }
        g.dispose();

        if (window.getGraphics() != null && window.getBufferStrategy() != null) {
            Graphics g2s = window.getBufferStrategy().getDrawGraphics();
            Insets insets = window.getInsets();

            g2s.drawImage(buffer, 0, insets.top, window.getWidth(), window.getHeight(),
                    0, 0, buffer.getWidth(), buffer.getHeight(), null);

            if (debug > 0) {
                g2s.setColor(Color.ORANGE);
                g2s.drawString(
                        String.format("[ dbg:%01d / fps:%03d ups:%03d ft:%03d / nbObj:%04d active:%04d / mouse: %s]",
                                debug,
                                stats.get("fps"),
                                stats.get("ups"),
                                stats.get("ft"),
                                (long) entities.values().size(),
                                entities.values().stream().filter(Entity::isActive).count(),
                                stats.get("mouse")),
                        10, window.getHeight() - 10
                );
            }

            if (isDebugAtLeast(2)) {
                // draw mouse
                g2s.setColor(Color.WHITE);
                g2s.fillRect((int) realMouseX, (int) realMouseY, 1, 1);
            }

            g2s.dispose();
            window.getBufferStrategy().show();
        }
    }

    /**
     * Detects if debug level is greater than the required one
     *
     * @param debugLevel the minimum required debug level
     * @return true if the required level is reached.
     */
    private boolean isDebugAtLeast(int debugLevel) {
        return debug >= debugLevel;
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
            case "GameObject" -> {
                drawGameObject(g, (GameObject) e);
            }
            case "AnimatedObject" -> {
                drawAnimatedObject(g, (AnimatedObject) e);
            }
            case "ImageObject" -> {
                drawImageObject(g, (ImageObject) e);
            }
            case "TextObject" -> {
                drawTextBox(g, (TextObject) e);
            }
            case "DialogBox" -> {
                drawDialogBox(g, (DialogBox) e);
            }
            case "Button" -> {
                drawButton(g, (Button) e);
            }
        }
        e.behaviors.forEach(b -> {
            b.draw(this, e, g);
        });
        e.child.forEach(c -> drawEntity(c, g));
    }

    private void drawAnimatedObject(Graphics2D g, AnimatedObject ao) {
        // TODO Create the AnimatedObject drawing method
    }

    private void drawImageObject(Graphics2D g, ImageObject io) {
        g.drawImage(
                io.getImage(),
                (int) io.getX(), (int) io.getY(),
                (int) io.getWidth(), (int) io.getHeight(),
                null);
    }

    private void drawGameObject(Graphics2D g, GameObject go) {
        // TODO Create the GameObject drawing method
    }

    private static void drawTextBox(Graphics2D g, TextObject te) {
        g.setColor(te.textColor);

        if (Optional.ofNullable(te.font).isPresent()) {
            g.setFont(te.font);
        }

        int textWidth = g.getFontMetrics().stringWidth(te.text);
        int textHeight = g.getFontMetrics().getHeight();
        int tx2 = g.getFontMetrics().getDescent();

        te.setSize(textWidth, textHeight);
        g.drawString(te.getText(), (int) te.getX(), (int) te.getY());

        if (debug > 2) {
            g.setColor(Color.ORANGE);
            g.drawRect(
                    (int) te.getX() + tx2, (int) (te.getY() - te.getHeight()),
                    (int) te.getWidth(), (int) te.getHeight());
        }
    }

    private void drawButton(Graphics2D g, Button te) {

        int x = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getX() + te.getX())
                : te.getX());

        int y = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getY() + te.getY())
                : te.getY());

        if (Optional.ofNullable(te.font).isPresent()) {
            g.setFont(te.font);
        }

        int fontHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(te.getText());
        int yOffset = g.getFontMetrics().getDescent();

        te.setSize(te.getWidth(), fontHeight + 2 * UIObject.margin);

        drawEdgeRectangle(g, te);

        g.setColor(te.textColor);
        g.drawString(
                te.getText(),
                x + (int) ((te.getWidth() - textWidth) * 0.5) + UIObject.margin,
                y + UIObject.margin + fontHeight - yOffset);
    }

    private static void drawEdgeRectangle(Graphics2D g, Entity te) {

        int x = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getX() + te.getX())
                : te.getX());

        int y = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getY() + te.getY())
                : te.getY());
        g.setColor(te.fillColor);
        g.fillRect(x, y, (int) te.getWidth(), (int) te.getHeight());

        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(
                (int) te.getX(), (int) te.getY(),
                (int) (x + te.getWidth()), (int) te.getY());
        g.drawLine(
                (int) te.getX(), (int) (te.getY()),
                (int) (te.getX()), (int) (te.getY() + te.getHeight()));

        g.setColor(Color.DARK_GRAY);
        g.drawLine((int) te.getX(), (int) (te.getY() + te.getHeight()), (int) (x + te.getWidth()), (int) (te.getY() + te.getHeight()));
        g.drawLine((int) (te.getX() + te.getWidth()), (int) te.getY(), (int) (x + te.getWidth()), (int) (te.getY() + te.getHeight()));

        g.setColor(te.borderColor);
        g.drawRect(x - 1, y - 1, (int) (te.getWidth() + 2), (int) (te.getHeight() + 2));

        g.setColor(Color.GRAY);
        g.drawLine(
                (int) (te.getX() + te.getWidth()), (int) (te.getY()),
                (int) (te.getX() + te.getWidth()), (int) (te.getY()));
        g.drawLine(
                (int) (te.getX()), (int) (te.getY() + te.getHeight()),
                (int) (te.getX()), (int) (te.getY() + te.getHeight()));
    }

    private static void drawDialogBox(Graphics2D g, DialogBox db) {
        if (Optional.ofNullable(db.font).isPresent()) {
            g.setFont(db.font);
        }
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(db.getText());

        g.setColor(Color.GRAY);
        g.fillRect((int) db.getX(), (int) db.getY(), (int) db.getWidth(), (int) db.getHeight());

        g.setColor(db.borderColor);
        g.drawRect((int) db.getX(), (int) db.getY(), (int) db.getWidth(), (int) db.getHeight());

        g.setColor(db.textColor);

        g.drawString(db.getText(), (int) (db.getX() + (db.getWidth() - textWidth) * 0.5 - UIObject.margin * 2),
                (int) (db.getY() + (db.getHeight() * 0.30) + UIObject.margin + UIObject.padding));
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

    public static void setPause(boolean p) {
        pause = p;
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
        entities.values().stream()
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
        entities.values().stream()
                .filter(Entity::isActive)
                .filter(e -> !e.behaviors.isEmpty())
                .forEach(e -> {
                    e.behaviors.forEach(b -> {
                        b.onKeyReleased(this, e, k);
                    });
                });
        switch (k.getKeyCode()) {
            // exit application on ESCAPE
            case KeyEvent.VK_ESCAPE -> {
                DialogBox db = (DialogBox) getEntity("exitConfirmBox");
                activateEntity(db, true);
            }
            // reset the scene on CTRL+Z
            case KeyEvent.VK_Z -> {
                if (k.isControlDown()) {
                    resetScene();
                }
            }
            case KeyEvent.VK_G -> {
                if (k.isControlDown()) {
                    world.gravity *= -1;
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
                prepareDisplay(true);
                setPause(false);
            }
            case KeyEvent.VK_PAGE_UP -> {
                generateEntities("enemy_", 10);
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
            info("Entity %s has been clicked", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMouseClick(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    private Optional<Entity> getEntityUnderMouse(double mouseX, double mouseY) {
        Optional<Entity> entityClicked = entities.values().stream()
                .filter(entity -> Arrays.stream(entity.getClass().getInterfaces()).filter(i -> i.equals(UIObject.class)).findFirst().isPresent()
                        && entity.isActive()
                        && entity.contains(mouseX, mouseY)).sorted((a, b) -> Integer.compare(b.priority, a.priority)).findFirst();

        return entityClicked;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            info("Entity %s has been clicked", entityClicked.name);
            entityClicked.behaviors
                    .forEach(b -> b.onMousePressed(this, entityClicked, mouseX, mouseY, e.getButton()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            info("Entity %s has been clicked", entityClicked.name);
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
        this.realMouseX = e.getX();
        this.realMouseY = e.getY() - window.getInsets().top;
        this.mouseX = (realMouseX * ((double) buffer.getWidth() / window.getWidth()));
        this.mouseY = (realMouseY * ((double) buffer.getHeight() / (window.getHeight() - window.getInsets().top)));

        if (getEntityUnderMouse(mouseX, mouseY).isPresent()) {
            Entity entityClicked = getEntityUnderMouse(mouseX, mouseY).get();
            //reset previously highlighted UIObject
            if (previousEntity != null && !previousEntity.equals(entityClicked)) {
                if (previousEntity instanceof Button) {
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
}