package com.snapgames.apps.desktop.game.physic;

import com.snapgames.apps.desktop.game.Game;
import com.snapgames.apps.desktop.game.behaviors.Behavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.scene.Scene;

import java.awt.geom.Point2D;
import java.util.Optional;


/**
 * The {@link PhysicEngine} class is responsible for updating the physical properties
 * of entities within a scene. It ensures that the entities' states, positions,
 * velocities, accelerations, and active states are dynamically adjusted
 * according to the rules defined in the {@link World}'s physical properties and
 * the entities' own characteristics.
 *
 * @author Frédéric Delorme
 * @since 1.0.1
 */
public class PhysicEngine {

    private final Game app;

    public PhysicEngine(Game app) {
        this.app = app;
    }

    /**
     * Update all entities from the current scene
     *
     * <p>It will refresh their status, position, velocity and acceleration, and active state.</p>
     *
     * @param elapsed The elapsed time since previous call.
     */
    public void update(Scene currentScene, double elapsed) {
        // update all entities not stick to activeCamera.
        app.getSpacePartition().update(currentScene, elapsed);
        // update all entities not stick to activeCamera.
        currentScene.getEntities().values()
                .forEach(e -> {
                    updateEntity(elapsed, e);
                });
        app.getCollisionManager().update(elapsed);
        app.getCollisionManager().getCollisions().forEach(ce -> {
            ce.o1().setContact(ce.o1().getContact() + 16);
            ce.o2().setContact(ce.o2().getContact() + 16);
        });
        // update camera position
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().update(elapsed);
            currentScene.getActiveCamera().getBehaviors().forEach(b -> {
                b.update(app, currentScene.getActiveCamera(), elapsed);
            });
        }
        // update camera position
        if (Optional.ofNullable(currentScene.getActiveCamera()).isPresent()) {
            currentScene.getActiveCamera().update(elapsed);
            currentScene.getActiveCamera().getBehaviors().forEach(b -> {
                b.update(app, currentScene.getActiveCamera(), elapsed);
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
        if (!e.isRelativeToCamera() && !Game.isPause()) {
            if (e.getPhysicNature().equals(PhysicNature.DYNAMIC)) {
                applyPhysics(app.getWorld(), delay, e);
                if (e.isCollisionActivated()) {
                    controlPlayAreaBoundaries(app.getWorld(), e);
                }
            }
        }
        e.update(app, delay);
        e.getBehaviors().forEach(b -> {
            b.update(app, e, delay);
        });
        // proceed with child entities (if any).
        e.child.forEach(c -> updateEntity(delay, c));
    }

    /**
     * The {@link PhysicEngine#applyPhysics(World, double, Entity)} method updates the
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
    public void applyPhysics(World world, double delay, Entity e) {
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
    public void controlPlayAreaBoundaries(World world, Entity e) {
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
            e.getBehaviors().forEach(c -> c.onDeactivate(app, e));
            e.child.forEach(c -> c.getBehaviors().forEach(b -> b.onDeactivate(app, e)));
        } else {
            e.getBehaviors().forEach(c -> c.onActivate(app, e));
            e.child.forEach(c -> c.getBehaviors().forEach(b -> b.onActivate(app, e)));
        }
    }
}
