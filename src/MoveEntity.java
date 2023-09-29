import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

abstract class MoveEntity extends ActionEntity {
    private PathingStrategy strategy;

    public MoveEntity(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod, PathingStrategy pStrategy) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
        this.strategy = pStrategy;
    }

    public abstract Point nextPosition(WorldModel world, Point destPos);

    abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    public PathingStrategy getStrategy() {
        return this.strategy;
    }

}
