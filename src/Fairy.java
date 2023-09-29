import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairy extends MoveEntity {
    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;
    private static final PathingStrategy Fairy_PATHING = new AStarPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, 0, actionPeriod, animationPeriod, Fairy_PATHING);
    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(new ArrayList<>(List.of(Stump.class)), this.getPosition());

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(Sapling.SAPLING_KEY),0, Sapling.SAPLING_HEALTH_LIMIT);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel world, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), getAnimationPeriod());
    }

    /*
    public Point nextPosition(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }
    */

    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = getStrategy();
        Predicate<Point> canPassThrough = point -> world.withinBounds(point) && !(world.isOccupied(point));

        BiPredicate<Point, Point> withinReach = (point1, point2) -> (Math.abs(point2.getX() - point1.getX()) + Math.abs(point2.getY() - point1.getY())) == 1;

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = strategy.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        if (path.size() == 0) {
            return this.getPosition();
        } else {
            return path.get(0);
        }
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
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
