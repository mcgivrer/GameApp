package com.snapgames.apps.desktop.game.gfx;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.gfx.plugins.*;
import com.snapgames.apps.desktop.game.physic.World;
import com.snapgames.apps.desktop.game.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private Color backGroundColor = Color.BLACK;

    private Map<Class<? extends Entity>, RendererPlugin<? extends Entity>> plugins = new HashMap<>();

    public Renderer(Game app) {
        this.app = app;
    }

    public void register(RendererPlugin<? extends Entity> rp) {
        plugins.put(rp.getEntityClass(), rp);
    }

    /**
     * Initialize the window and the rendering buffer according to configuration properties from {@link Game}.
     *
     * @param app the parent {@link Game} instance.
     */
    public void init(Game app) {
        // create the drawing buffer
        buffer = new BufferedImage(
                Integer.parseInt(app.getConfig().getProperty("app.render.buffer.width", "320")),
                Integer.parseInt(app.getConfig().getProperty("app.render.buffer.height", "240")),
                BufferedImage.TYPE_INT_ARGB
        );
        fullScreenStatus = Boolean.parseBoolean(app.getConfig().getProperty("app.window.full.screen", "false"));
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
     * Draw the current active {@link Scene}
     *
     * @param currentScene the current active scene
     * @param stats        the map with stats to be updated.
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
                    if (app.isDebugAtLeast(3)) {
                        g.setColor(Color.ORANGE);
                        g.drawRect(
                                (int) e.getX(), (int) e.getY(),
                                (int) e.getWidth(), (int) e.getHeight());

                    }
                });

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
                });

        // draw all Behaviors about active camera.
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().behaviors.forEach(b -> {
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

    public void drawEntity(Entity e, Graphics2D g) {

        if (plugins.containsKey(e.getClass())) {
            plugins.get(e.getClass()).draw(g, e);
            //plugins.get(e.getClass()).drawVisualDebugInformation(g, e, 0);
            e.setAttribute("renderedBy", plugins.get(e.getClass()).getClass());
        } else {
            Game.error("Unknown drawing method/plugin for '%s' type %s", e.getName(), e.getClass());
        }
        e.behaviors.forEach(b -> {
            b.draw(app, e, g);
        });
        e.child.forEach(c -> {
            drawEntity(c, g);
        });
    }

    public static void drawEdgeRectangle(Graphics2D g, Entity te) {
        drawEdgeRectangle(g, te, te.fillColor);
    }

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
     * release all resources from Renderer system.
     */
    public void dispose() {
        window.dispose();
        buffer = null;
    }

    /**
     * switch from windowed to full screen display.
     */
    public void switchFullScreen() {
        fullScreenStatus = !fullScreenStatus;
        prepareDisplay(fullScreenStatus);
    }

    /**
     * retrieve the current created {@link JFrame} window.
     *
     * @return the current {@link JFrame} instance as main game window.
     */
    public JFrame getWindow() {
        return window;
    }
}
