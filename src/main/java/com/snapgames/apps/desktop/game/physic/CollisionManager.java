package com.snapgames.apps.desktop.game.physic;

import com.snapgames.apps.desktop.game.GameApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>{@link CollisionManager} is used to detect and resolve collision<p>
 * </p>This manager will detect all the collision between all active {@link GameApp.Entity}
 * into the current active {@link GameApp.Scene}.</p>
 *
 * @author Frédéric Delorme
 * @see CollisionEvent
 * @see GameApp.Entity
 * @since 0.0.11
 */
public class CollisionManager {
    private final GameApp parent;
    private final List<CollisionEvent> collisionEvents = new ArrayList<>();

    public CollisionManager(GameApp app) {
        this.parent = app;
    }

    /**
     * Parse all active objects to detect possible collision.
     *
     * @param elapsed the elapsed time since previous call.
     */
    public void update(double elapsed) {
        // remove all previous collision.
        collisionEvents.clear();
        parent.getActiveScene().getEntities().values().forEach(o -> o.collisions.clear());
        // detect new possible collision on active objects only.
        parent.getActiveScene().getEntities().values().stream().
                filter(e -> e.isActive() && e.isCollisionActivated()).
                forEach(go -> {
                    parent.getSpacePartition().find(go).stream().filter(
                            go2 -> go2.isActive() && go2.isCollisionActivated()
                                    && !go2.name.equals(go.getName())
                                    && go.intersects(go2)).forEach(g -> {
                        addCollisionEvent(g, go);
                    });
                });
    }

    /**
     * Add a new {@link CollisionEvent} to be processed
     *
     * @param o1 {@link GameApp.Entity} 1 colliding with {@link GameApp.Entity} 2
     * @param o2 {@link GameApp.Entity} 2 colliding with {@link GameApp.Entity} 1
     */
    public void addCollisionEvent(GameApp.Entity o1, GameApp.Entity o2) {
        CollisionEvent ce = new CollisionEvent(o1, o2);
        collisionEvents.add(ce);
        pprocessCollisionBehaviorFor(o1, ce);
        pprocessCollisionBehaviorFor(o2, ce);

    }

    private void pprocessCollisionBehaviorFor(GameApp.Entity o1, CollisionEvent ce) {
        o1.collisions.add(ce);
        o1.getBehaviors().forEach(b -> b.onCollide(parent, parent.getActiveScene(), o1, ce));
    }

    public Collection<CollisionEvent> getCollisionFor(GameApp.Entity collider) {
        return collisionEvents.stream().filter(go -> go.o1().equals(collider)).toList();
    }

    public Collection<CollisionEvent> getCollisions() {
        return collisionEvents;
    }

    public void init(GameApp GameApp) {
        collisionEvents.clear();
    }
}
