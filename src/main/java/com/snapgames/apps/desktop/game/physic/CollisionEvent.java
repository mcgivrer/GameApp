package com.snapgames.apps.desktop.game.physic;

import com.snapgames.apps.desktop.game.GameApp;

/**
 * <p>A {@link CollisionEvent} record is created for each detected collision into the {@link CollisionManager} service.</p>
 * <p>Each collision between two {@link GameApp.Entity} produce a new instance of the record CollisionEvent into the
 * {@link CollisionManager#update(double)}.</p>
 *
 * @param o1 the primary object in the detected collision,
 * @param o2 the secondary object int the detected collision.
 * @author Frédéric Delorme
 * @see CollisionManager
 * @see GameApp.Entity
 * @since 0.0.11
 */
public record CollisionEvent(GameApp.Entity o1, GameApp.Entity o2) {

}
