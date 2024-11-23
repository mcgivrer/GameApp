package com.snapgames.apps.desktop.game.entity;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.gfx.Renderer;
import com.snapgames.apps.desktop.game.physic.Material;
import com.snapgames.apps.desktop.game.scenes.Scene;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p>The {@link Entity} is drawn by the {@link Renderer#draw(Scene, Map)} and more precisely by
 * the {@link Renderer#drawEntity(Entity, Graphics2D)}  operation.</p>
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
 * @see Game#update(double)
 * @see Renderer#draw(Scene, Map)
 * @since 1.0.0
 */
public class Entity extends Rectangle2D.Double {
    public static int index = 0;
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
    public java.util.List<Point2D> forces = new ArrayList<>();
    // Material
    public Material material = Material.DEFAULT;
    // mass
    public double mass = 1.0;

    // this Entity will be relative to camera viewport.
    public boolean relativeToCamera = false;

    // use for child entity for update/rendering operation.
    public boolean relativeToParent = false;

    // Enhance Entity with behaviors
    public java.util.List<Behavior> behaviors = new ArrayList<>();

    // add any attribute object to this entity.
    private Map<String, Object> attributes = new HashMap<>();

    public Shape shape = new Double();

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

    public void update(Game app, double elapsed) {

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

    public String getName() {
        return name;
    }
}
