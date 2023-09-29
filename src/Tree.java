import processing.core.PImage;

import java.util.List;

public class Tree extends PlantHealth {
    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final int TREE_NUM_PROPERTIES = 3;
    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;

    public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        super(id, position, images, 0, actionPeriod, animationPeriod, health, TREE_HEALTH_MAX);
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Stump stump = new Stump(Stump.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(Stump.STUMP_KEY), getImageIndex());

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transform(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        }
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }
}
