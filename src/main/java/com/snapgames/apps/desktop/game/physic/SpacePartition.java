package com.snapgames.apps.desktop.game.physic;

import com.snapgames.apps.desktop.game.GameApp;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a space partitioning system using a quadtree structure.
 * <p>
 * The {@code SpacePartition} is used to efficiently manage objects within a 2D space,
 * optimizing processes like collision detection by dividing the space into smaller sections (nodes).
 *
 * @author Frederic Delorme
 * @since 1.0.0
 */
public class SpacePartition extends Rectangle2D.Double {
    private int maxObjectsPerNode = 5;
    private int maxTreeLevels = 5;

    /**
     * The root Cell, containing all others "sub-cells".
     */
    private SpacePartition root;
    /**
     * Depth level of this cell
     */
    private final int level;
    /**
     * Contained objects by the cell
     */
    private final List<GameApp.Entity> objects;
    /**
     * All the child sub-cells known as nodes.
     */
    private final SpacePartition[] nodes;

    /**
     * Create a new {@link SpacePartition} with a depth level and its defined
     * rectangle area.
     *
     * @param pLevel  the depth level for this {@link SpacePartition}
     * @param pBounds the Rectangle area covered by this {@link SpacePartition}.
     */
    public SpacePartition(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        setRect(pBounds);
        nodes = new SpacePartition[4];
    }

    /**
     * Initialize the {@link SpacePartition} according to the defined configuration.
     * <p>
     * The configuration file will provide 2 parameters:
     * <ul>
     * <li><code>app.physic.space.max.entities</code> is the maximum number of
     * entities that a SpacePartition node can contain,</li>
     * <li><code>app.physic.space.max.levels</code> is the max Depth level the tree
     * hierarchy can contain.</li>
     * </ul>
     * </p>
     *
     * @param world the {@link GameApp.World} object for the {@link GameApp} instance.
     */
    public SpacePartition(GameApp.World world, int maxObjectPerNode, int maxTreeLevels) {
        this(0, world.getPlayArea().getBounds());
        this.maxObjectsPerNode = maxObjectPerNode;
        this.maxTreeLevels = maxTreeLevels;
    }

    /**
     * Dispatch all the active entities into the Space partitioning system to reduce collision
     * detections and optimize processing.
     *
     * @param scene the parent {@link GameApp.Scene } instance
     * @param d     the elapsed time since previous call.
     */
    public synchronized void cullingProcess(GameApp.Scene scene, double d) {
        clear();
        scene.getEntities().values().stream()
                .filter(GameApp.Entity::isActive)
                .forEach(this::insert);
    }

    /**
     * Clears the {@link SpacePartition} nodes.
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * Split the current SpacePartition into four subspaces.
     */
    private void split() {
        int subWidth = (int) (getWidth() / 2);
        int subHeight = (int) (getHeight() / 2);
        int x = (int) getX();
        int y = (int) getY();
        nodes[0] = new SpacePartition(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new SpacePartition(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new SpacePartition(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new SpacePartition(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /**
     * Determine which {@link SpacePartition} node the {@link GameApp.Entity} belongs to.
     *
     * @param pRect the {@link GameApp.Entity} to search in the {@link SpacePartition}'s
     *              tree.
     * @return the depth level of the {@link GameApp.Entity}; -1 means object can’t
     * completely fit
     * within a child node and is part of the parent node
     */
    private int getIndex(GameApp.Entity pRect) {
        int index = -1;
        double verticalMidpoint = getX() + (getWidth() / 2);
        double horizontalMidpoint = getY() + (getHeight() / 2);
        // Object can completely fit within the top quadrants
        boolean topQuadrant = (pRect.getY() < horizontalMidpoint
                && pRect.getY() + pRect.height < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);
        // Object can completely fit within the left quadrants
        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }

    /**
     * Insert the {@link GameApp.Entity} into the {@link SpacePartition} tree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     *
     * @param pRect the {@link GameApp.Entity} to insert into the tree.
     */
    public void insert(GameApp.Entity pRect) {
        if (nodes[0] != null) {
            int index = getIndex(pRect);
            if (index != -1) {
                nodes[index].insert(pRect);
                return;
            }
        }
        objects.add(pRect);
        if (objects.size() > maxObjectsPerNode && level < maxTreeLevels) {
            if (nodes[0] == null) {
                split();
            }
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
        // insert all children Entity
        pRect.getChild().forEach(this::insert);
    }

    /**
     * Find the {@link GameApp.Entity} into the {@link SpacePartition} tree and return the
     * list of neighbour's entities.
     *
     * @param e the Entity to find.
     * @return a list of neighbour's entities.
     */
    public List<GameApp.Entity> find(GameApp.Entity e) {
        List<GameApp.Entity> list = new ArrayList<>();
        return find(list, e);
    }

    /*
     * Return all objects that could collide with the given object
     */
    private List<GameApp.Entity> find(List<GameApp.Entity> returnObjects, GameApp.Entity pRect) {
        int index = getIndex(pRect);
        if (index != -1 && nodes[0] != null) {
            nodes[index].find(returnObjects, pRect);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }

    /**
     * Dispatch all the {@link GameApp.Scene} {@link GameApp.Entity}'s into the
     * {@link SpacePartition} tree.
     *
     * @param scene   the Scene to be processed.
     * @param elapsed the elapsed time since previous call (not used here).
     */
    public void update(GameApp.Scene scene, double elapsed) {
        this.clear();
        scene.getEntities().forEach((k, v) -> insert(v));
    }

    public void initialize(GameApp app) {
        this.root = this;
    }

    /**
     * Draw all {@link SpacePartition} nodes with a following color code:
     * <ul>
     * <li><code>RED</code> the node is full,</li>
     * <li><code>ORANGE</code> the node has entities but is not full,</li>
     * <li><code>GREEN</code> the node is empty.</li>
     * </ul>
     *
     * @param g     the {@link Graphics2D} API instance
     * @param alpha the stroke size of the grid line.
     */
    public void draw(Graphics2D g, float alpha) {
        SpacePartition sp = this;
        if (objects.isEmpty()) {
            g.setColor(new Color(0.0f, 1.0f, 0.0f, alpha));
        } else if (objects.size() < maxObjectsPerNode) {
            g.setColor(new Color(1.0f, 1.0f, 0.0f, alpha));
        } else {
            g.setColor(new Color(1.0f, 0.0f, 0.0f, alpha));
        }
        g.setFont(g.getFont().deriveFont(9.0f));
        g.drawString("" + objects.size(), (int) x + 4, (int) y + 8);
        g.setStroke(new BasicStroke(0.5f));
        g.draw(this);
        if (this.nodes != null) {
            for (SpacePartition node : nodes) {
                if (node != null) {
                    node.draw(g, alpha);
                }
            }
        }
    }
}
