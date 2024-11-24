package com.snapgames.apps.desktop.game.gfx;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.gfx.plugins.*;
import com.snapgames.apps.desktop.game.physic.World;
import com.snapgames.apps.desktop.game.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static com.snapgames.apps.desktop.game.utils.Log.error;

/**
 * The new {@link Renderer} service is responsible for drawing the current state of
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
 *
 * @author Frederic Delorme
 * @since 1.0.0
 */
public class Renderer {
    /**
     * A reference to the parent Game instance that owns and coordinates this Renderer.
     * This variable is used throughout the Renderer class to access game-wide configurations,
     * initiate various rendering tasks, and manage the overall rendering lifecycle.
     */
    private Game app;

    /**
     * The window containing the all Game display.
     */
    private JFrame window;
    /**
     * Rendering buffer where everything is drawn.
     */
    public static BufferedImage buffer;
    /**
     * rendering window in fullscreen mode if true
     */
    private boolean fullScreenStatus = false;
    /**
     * Default background buffer color for rendering processing.
     */
    private final Color backGroundColor = Color.BLACK;

    /**
     * (No used) Internal debug filtering on {@link Entity}'s name.
     */
    private static String debugFilter = "";

    /**
     * A map that associates each specific Entity class with its corresponding RendererPlugin.
     * This is used to determine which plugin should be used to render a given entity.
     */
    private final Map<Class<? extends Entity>, RendererPlugin<? extends Entity>> plugins = new HashMap<>();

    /**
     * Constructs a Renderer with the specified parent Game instance.
     *
     * @param app the parent Game instance
     */
    public Renderer(Game app) {
        this.app = app;
    }

    /**
     * Registers a RendererPlugin, associating it with its corresponding entity class.
     *
     * @param rp the RendererPlugin to be registered, which is associated with a specific class of Entity
     */
    public void register(RendererPlugin<? extends Entity> rp) {
        plugins.put(rp.getEntityClass(), rp);
    }

    /**
     * Initializes the Renderer with various settings and default plugins from the provided Game application.
     *
     * @param app the Game application instance used to retrieve configuration settings.
     */
    public void init(Game app) {
        // create the drawing buffer
        buffer = new BufferedImage(
                Integer.parseInt(app.getConfig().getProperty("app.render.buffer.width", "320")),
                Integer.parseInt(app.getConfig().getProperty("app.render.buffer.height", "240")),
                BufferedImage.TYPE_INT_ARGB
        );

        fullScreenStatus = Boolean.parseBoolean(app.getConfig().getProperty("app.window.full.screen", "false"));
        debugFilter = app.getConfig().getProperty("app.debug.entity.filter", "");

        // add default Plugins implementation
        register(new GameObjectRendererPlugin());
        register(new ImageObjectRendererPlugin());
        register(new TextObjectRendererPlugin());
        register(new ButtonRendererPlugin());
        register(new DialogBoxRendererPlugin());
        register(new ItemObjectRendererPlugin());
        register(new MenuObjectRendererPlugin());
    }

    /**
     * Prepare the default display before anything else.
     */
    public void prepareDisplay() {
        prepareDisplay(false);
    }

    /**
     * Prepare the default display, can be full screen ort not.
     *
     * @param fullScreen the flag to request the full screen mode.
     */
    public void prepareDisplay(boolean fullScreen) {
        if (window != null && window.isActive()) {
            window.dispose();
        }
        window = new JFrame(app.getConfig().getProperty("app.window.title", "Demo01"));
        window.setPreferredSize(new Dimension(
                Integer.parseInt(app.getConfig().getProperty("app.window.width", "640")),
                Integer.parseInt(app.getConfig().getProperty("app.window.height", "480"))));
        if (fullScreen) {
            window.setUndecorated(true);
        }
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(Game.getResource("/images/thor-hammer.png"));
        window.pack();
        // processing keyboard input
        window.addKeyListener(app);
        // processing mouse input
        window.addMouseListener(app);
        window.addMouseMotionListener(app);
        window.addMouseWheelListener(app);

        // show window.
        window.setVisible(true);
        window.createBufferStrategy(3);
        if (fullScreen) {
            window.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Renders the specified scene and updates relevant statistics.
     *
     * @param currentScene the Scene to be drawn
     * @param stats        a Map containing various statistics about the rendering process
     */
    public void draw(Scene currentScene, Map<String, Object> stats) {
        Graphics2D g = buffer.createGraphics();
        World world = app.getWorld();
        g.setRenderingHints(
                Map.of(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                        RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g.setBackground(backGroundColor);
        g.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());

        // move Camera
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            g.translate(-currentScene.getActiveCamera().x, -currentScene.getActiveCamera().y);
        }

        // draw play area
        g.setColor(world.playAreaColor);
        g.fillRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());

        //draw everything
        currentScene.getEntities().values().stream().filter(e -> e.isActive() && !e.isRelativeToCamera())
                .sorted(Comparator.comparingInt(a -> a.priority))
                .forEach(e -> {
                    drawEntity(e, g);
                    drawDebugInfo(g, e);
                });
        // draw space partitioning
        if (app.isDebugAtLeast(4)) {
            g.setColor(Color.ORANGE);
            app.getSpacePartition().draw(g, 0.8f);
        }

        // draw play area limits in debug mode
        if (app.isDebugAtLeast(1)) {
            g.setColor(Color.YELLOW);
            g.drawRect(0, 0, (int) world.playArea.getWidth(), (int) world.playArea.getHeight());
        }

        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            g.translate(currentScene.getActiveCamera().x, currentScene.getActiveCamera().y);
        }

        // draw all objects stick to the Camera.
        currentScene.getEntities().values().stream().filter(e -> e.isActive() && e.isRelativeToCamera())
                .sorted(Comparator.comparingInt(a -> a.priority))
                .forEach(e -> {
                    drawEntity(e, g);
                    drawDebugInfo(g, e);
                });

        // draw all Behaviors about active camera.
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().getBehaviors().forEach(b -> {
                b.draw(app, currentScene.getActiveCamera(), g);
            });
        }

        // keep mouse coordinates
        stats.put("scene", currentScene.getName());
        if (app.isDebugAtLeast(1)) {
            g.setColor(Color.YELLOW);
            g.fillRect(
                    (int) app.mouseX,
                    (int) app.mouseY,
                    2, 2);
        }
        g.dispose();

        if (window.getGraphics() != null && window.getBufferStrategy() != null) {
            Graphics g2s = window.getBufferStrategy().getDrawGraphics();
            Insets insets = window.getInsets();

            g2s.drawImage(buffer, 0, insets.top, window.getWidth(), window.getHeight(),
                    0, 0, buffer.getWidth(), buffer.getHeight(), null);

            if (app.isDebugAtLeast(0)) {
                g2s.setColor(Color.ORANGE);
                g2s.drawString(
                        String.format("[ dbg:%01d / fps:%03d ups:%03d ft:%03d / obj:%04d active:%04d / scn:%s ]",
                                Game.debug,
                                stats.get("fps"),
                                stats.get("ups"),
                                stats.get("ft"),
                                (long) currentScene.getEntities().values().size(),
                                currentScene.getEntities().values().stream().filter(Entity::isActive).count(),
                                stats.get("scene")),
                        10, window.getHeight() - 10
                );
            }

            if (app.isDebugAtLeast(2)) {
                // draw mouse
                g2s.setColor(Color.WHITE);
                g2s.fillRect((int) app.realMouseX, (int) app.realMouseY, 1, 1);
            }
            if (window.getBufferStrategy() != null) {
                window.getBufferStrategy().show();
            }
            g2s.dispose();
        }
    }

    /*----- objects rendering -----*/

    /**
     * Draws the specified entity using the given graphics context. This method first attempts to use
     * a plugin to draw the entity. If a suitable plugin is not found, an error is logged. This method
     * also traverses and draws the entity's child entities.
     *
     * @param e the entity to be drawn
     * @param g the graphics context to use for drawing
     */
    public void drawEntity(Entity e, Graphics2D g) {

        if (plugins.containsKey(e.getClass())) {
            plugins.get(e.getClass()).draw(g, e);
            //plugins.get(e.getClass()).drawVisualDebugInformation(g, e, 0);
            e.setAttribute("renderedBy", plugins.get(e.getClass()).getClass());
        } else {
            error("Unknown drawing method/plugin for '%s' type %s", e.getName(), e.getClass());
        }
        e.getBehaviors().forEach(b -> {
            b.draw(app, e, g);
        });
        e.child.forEach(c -> {
            drawEntity(c, g);
        });
    }

    /**
     * Draws the edge of the rectangle representing the given entity using the specified graphics context.
     * This method uses the fill color of the entity to render the edge.
     *
     * @param g  the graphics context to use for drawing
     * @param te the entity whose edge rectangle is to be drawn
     */
    public static void drawEdgeRectangle(Graphics2D g, Entity te) {
        drawEdgeRectangle(g, te, te.fillColor);
    }

    /**
     * Draws a rectangle representing the given entity using the specified graphics context.
     * The rectangle is filled with the provided color, and different edges of the rectangle
     * are drawn with specific colors to highlight the entity's outline.
     *
     * @param g    the graphics context to use for drawing
     * @param te   the entity whose rectangle is to be drawn
     * @param fill the color to fill the rectangle with
     */
    public static void drawEdgeRectangle(Graphics2D g, Entity te, Color fill) {

        int x = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getX() + te.getX())
                : te.getX());

        int y = (int) ((Optional.ofNullable(te.getParent()).isPresent() && te.isRelativeToParent())
                ? (te.getParent().getY() + te.getY())
                : te.getY());
        g.setColor(fill);
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


    /**
     * Draws debug information for a given entity on the provided graphics context.
     * This method visualizes various aspects of the entity such as contact points,
     * bounding boxes, velocity, acceleration, forces, lifespan, and custom debugging info.
     * The level of detail to be drawn depends on the current debugging level set in the application.
     *
     * @param rbg the graphics context to use for drawing the debug information
     * @param entity the entity for which the debug information is to be drawn
     */
    private void drawDebugInfo(Graphics2D rbg, Entity entity) {
        if (app.isDebugAtLeast(1)) {
            if (entity.getContact() > 0) {
                rbg.setColor(Color.YELLOW);
                if (entity.getContact() << 1 != 0) {
                    rbg.drawLine((int) entity.x, (int) entity.y, (int) entity.x, (int) (entity.y + entity.getHeight()));
                }
                if (entity.getContact() << 2 != 0) {
                    rbg.drawLine((int) (entity.x + entity.getWidth()), (int) entity.y, (int) (entity.x + entity.getWidth()), (int) (entity.y + entity.getHeight()));
                }
                if (entity.getContact() << 3 != 0) {
                    rbg.drawLine((int) (entity.x), (int) entity.y, (int) (entity.x + entity.getWidth()), (int) (entity.y));
                }
                if (entity.getContact() << 4 != 0) {
                    rbg.drawLine((int) (entity.x), (int) (entity.y + entity.getHeight()), (int) (entity.x + entity.getWidth()), (int) (entity.y + entity.getHeight()));
                }
                rbg.setColor(Color.ORANGE);
                rbg.draw(entity);
            }
            if (app.isDebugAtLeast(2)) {
                int centerX = (int) (entity.x + entity.width * 0.5);
                int centerY = (int) (entity.y + entity.height * 0.5);

                // draw Bounding box.
                Stroke b = rbg.getStroke();
                if (entity.getContact() > 0 || !entity.getCollisions().isEmpty()) {
                    rbg.setStroke(new BasicStroke(1.0f));
                    rbg.setColor(Color.RED);
                } else {
                    rbg.setStroke(new BasicStroke(0.5f));
                    rbg.setColor(Color.WHITE);
                }
                rbg.draw(entity);
                rbg.setStroke(b);

                // draw velocity
                rbg.setColor(Color.CYAN);
                rbg.drawLine(
                        centerX, centerY,
                        centerX + (int) (entity.dx * 0.1),
                        centerY + (int) (entity.dy * 0.1));

                // draw acceleration
                rbg.setColor(Color.YELLOW);
                rbg.drawLine(
                        centerX, centerY,
                        centerX + (int) (entity.ax * 5.0),
                        centerY + (int) (entity.ay * 5.0));

                // draw forces
                if (app.isDebugAtLeast(3)) {
                    rbg.setColor(Color.GREEN);
                    entity.getForces().forEach(f -> {
                        rbg.drawLine(
                                centerX,
                                centerY,
                                centerX + (int) (f.getX() * 0.1),
                                centerY + (int) (f.getY() * 0.1));
                    });
                }
                // draw LifeSpan
                if (app.isDebugAtLeast(4) && entity.getDuration() > -1 && entity.getLifeSpan() > 0) {
                    rbg.setColor(Color.RED);
                    Stroke sBack = rbg.getStroke();
                    rbg.setStroke(new BasicStroke(2.0f));
                    rbg.setFont(rbg.getFont().deriveFont(9f));
                    rbg.drawString("[L]", (int) (entity.x - 12), (int) (entity.y - 4));
                    int level = (int) (entity.getWidth() * (1.0 - (1.0 * entity.getLifeSpan() / (1.0 * entity.getDuration()))) * 1.5);
                    rbg.drawLine((int) (entity.x), (int) (entity.y - 8),
                            (int) (entity.x + level), (int) (entity.y - 8));
                    rbg.setStroke(sBack);
                }
            }
            if (!debugFilter.isEmpty() && isEntityToBeDebug(debugFilter, entity.getName()) && app.isDebugAtLeast(1)) {
                rbg.setColor(Color.YELLOW);
                rbg.setFont(rbg.getFont().deriveFont(9.0f));
                entity.getDebugInfo().forEach((key, value) -> {
                    String[] l = key.split("\\|");
                    int dbg = Integer.parseInt(l[0]);
                    int idx = Integer.parseInt(l[1]);
                    if (dbg <= app.getDebugLevel()) {
                        rbg.drawString(l[2] + ":" + value, (int) (entity.x + entity.width), (int) (entity.y + entity.height) + (idx * 10));
                    }
                });
            }
        }
    }

    /**
     * Disposes of the resources used by the Renderer instance.
     * This includes disposing of the window and setting the buffer to null.
     */
    public void dispose() {
        window.dispose();
        buffer = null;
    }

    /**
     * Toggles the full screen mode of the application.
     * This method switches the state of the fullScreenStatus field and
     * reinitializes the display to reflect the new full-screen status.
     */
    public void switchFullScreen() {
        fullScreenStatus = !fullScreenStatus;
        prepareDisplay(fullScreenStatus);
    }

    private boolean isEntityToBeDebug(String debugFilter, String name) {
        return Arrays.stream(debugFilter.split(",")).anyMatch(name::contains);
    }


    /**
     * retrieve the current created {@link JFrame} window.
     *
     * @return the current {@link JFrame} instance as main game window.
     */
    public JFrame getWindow() {
        return window;
    }

    public void setDebugFilter(String debugFilter) {
        Renderer.debugFilter = debugFilter;
    }
}
