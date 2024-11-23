package com.snapgames.apps.desktop.game.entity.animation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
public class Animations {
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
