import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

abstract class Dude extends MoveEntity {
    private int resourceLimit;
    private int resourceCount;

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;
    private static final PathingStrategy Dude_PATHING = new AStarPathingStrategy();

    public Dude(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod, Dude_PATHING);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void incrementResourceCount() {
        resourceCount++;
    }

    /*
    public Point nextPosition(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }
    */

    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = getStrategy();
        Predicate<Point> canPassThrough = point -> world.withinBounds(point) && !(world.isOccupied(point) && !(world.getOccupancyCell(point) instanceof Stump));

        BiPredicate<Point, Point> withinReach = (point1, point2) -> (Math.abs(point2.getX() - point1.getX()) + Math.abs(point2.getY() - point1.getY())) == 1;

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = strategy.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        if (path.size() == 0) {
            return this.getPosition();
        } else {
            return path.get(0);
        }
    }

}
