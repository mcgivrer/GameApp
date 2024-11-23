package com.snapgames.apps.desktop.game.entity.util;

import com.snapgames.apps.desktop.game.behaviors.AlignBehavior;
import com.snapgames.apps.desktop.game.entity.Entity;
import com.snapgames.apps.desktop.game.entity.TextObject;
import com.snapgames.apps.desktop.game.entity.ui.Button;
import com.snapgames.apps.desktop.game.entity.ui.UIObject;

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
