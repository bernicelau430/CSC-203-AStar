import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DudeFull extends Dude {

    // don't technically need resource count ... full
    public DudeFull(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod, int resourceLimit) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod, resourceLimit, 0);
    }

    public void transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Dude dude = new DudeNotFull(this.getId(), this.getPosition(), this.getImages(), this.getImageIndex(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit());

        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(new ArrayList<>(List.of(House.class)), this.getPosition());

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler)) {
            transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        }
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
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
