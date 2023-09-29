import processing.core.PImage;

import java.util.List;

abstract class ActionEntity extends Entity {
    private double actionPeriod;
    private double animationPeriod;

    public ActionEntity(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod) {
        super(id, position, images, imageIndex);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public double getActionPeriod() {
        return actionPeriod;
    }

    public double getAnimationPeriod() {
        try {
            return this.animationPeriod;
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported"));
        }
    }

    public Animation createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }

    public Activity createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore, 0);
    }

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    abstract void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore);
}
