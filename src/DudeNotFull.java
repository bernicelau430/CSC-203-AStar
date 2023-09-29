import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dude {

    // need resource count, though it always starts at 0
    public DudeNotFull(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod, int resourceLimit) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod, resourceLimit, 0);
    }
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= this.getResourceLimit()) {
            Dude dude = new DudeFull(this.getId(), this.getPosition(), this.getImages(), this.getImageIndex(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }
        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)), this.getPosition());

        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        }
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            this.incrementResourceCount();
            ((PlantHealth) target).decrementHealth();
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }
}
