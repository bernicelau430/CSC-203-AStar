import processing.core.PImage;

import java.util.List;

public class Obstacle extends ActionEntity {
    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;

    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images, 0, 0, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // pass
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }
}
