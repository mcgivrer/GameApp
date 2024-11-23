package com.snapgames.apps.desktop.game.entity;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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
    POLYGON
}
