package com.snapgames.apps.desktop.game.entity.animation;

import com.snapgames.apps.desktop.game.entity.ImageObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link AnimationFrames} is a list of frames to animate a Sprite (coming soon) or an {@link ImageObject}.
 *
 * <p>It defines frames for each step of the animation with their display duration.</p>
 *
 * @author Frédéric Delorme
 * @see Animations
 * @since 1.0.0
 */
public class AnimationFrames {
    List<BufferedImage> frames = new ArrayList<>();
    List<Integer> timeFrames = new ArrayList<>();
    double elapsedTime = 0;
    int currentFrame = 0;

    AnimationFrames(List<BufferedImage> frames, List<Integer> timeFrames) {
        this.frames = frames;
        this.timeFrames = timeFrames;
    }

    /**
     * Update the currentFrame according to elapsed time since previous call.
     *
     * @param delay elapsed time since previous call.
     */
    public void update(double delay) {
        elapsedTime += delay;
        if (elapsedTime > timeFrames.get(currentFrame)) {
            currentFrame = (currentFrame + 1 < frames.size() ? currentFrame + 1 : 0);
            elapsedTime = 0;
        }
    }

    /**
     * Return the current active frame for this AnimationFrames.
     *
     * <p>According to elapsed time, it returns the corresponding frame for the {@link AnimationFrames}.</p>
     *
     * @return BufferedImage corresponding to the current active frame.
     */
    public BufferedImage getImage() {
        return this.frames.get(currentFrame);
    }


    /**
     * Reset this {@link AnimationFrames} to its first frame.
     */
    public void reset() {
        currentFrame = 0;
    }

    /**
     * Load all the frames from a broader image by slicing each frame from it.
     *
     * <p></p>source is image source and table is list of integer structured like [x,y,w,h,t] for each frame
     * to slice from the source image where
     * <ul>
     *     <li><code>(x,y)</code> is position of the frame in the source image,</li>
     *     <li><code>(w,h)</code> is the size of the extracted frame,</li>
     *     <li><code>(t)</code> is the duration for that frame.</li>
     * </ul>
     *
     * @param source source for images
     * @param table  list of [x,y,w,h,t] structures.
     * @return a brand new AnimationFrames instance.
     */
    public static AnimationFrames load(BufferedImage source, int[] table) {
        if (source != null) {
            List<BufferedImage> images = new ArrayList<>();
            List<Integer> timeFrames = new ArrayList<>();
            for (int idx = 0; idx < table.length; idx += 5) {
                int x = table[idx];
                int y = table[idx + 1];
                int w = table[idx + 2];
                int h = table[idx + 3];
                int timeFrame = table[idx + 4];
                images.add(source.getSubimage(x, y, w, h));
                timeFrames.add(timeFrame);
            }
            return new AnimationFrames(images, timeFrames);
        }
        return null;
    }
}
