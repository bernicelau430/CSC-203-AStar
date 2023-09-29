import processing.core.PImage;

import java.util.List;

public class Sapling extends PlantHealth {
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;
    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    // health starts at 0 and builds up until ready to convert to Tree
    public Sapling(String id, Point position, List<PImage> images, int health, int healthLimit) {
        super(id, position, images, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, healthLimit);
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Stump stump = new Stump(Stump.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(Stump.STUMP_KEY), getImageIndex());

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (this.getHealth() >= this.getHealthLimit()) {
            Tree tree = new Tree(Tree.TREE_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(Tree.TREE_KEY), Functions.getNumFromRange(Tree.TREE_ACTION_MAX, Tree.TREE_ACTION_MIN), Functions.getNumFromRange(Tree.TREE_ANIMATION_MAX, Tree.TREE_ANIMATION_MIN), Functions.getIntFromRange(Tree.TREE_HEALTH_MAX, Tree.TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.incrementHealth();
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        }
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }
}
