import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.List;

public interface PathingStrategy {
    /*
     * Returns a prefix of a path from the start point to a point within reach
     * of the end point.  This path is only valid ("clear") when returned, but
     * may be invalidated by movement of other entities.
     *
     * The prefix includes neither the start point nor the end point.
     */
    List<Point> computePath(Point start, Point end,
        Predicate<Point> canPassThrough,
        BiPredicate<Point, Point> withinReach,
        Function<Point, Stream<Point>> potentialNeighbors);

    static final Function<Point, Stream<Point>> CARDINAL_NEIGHBORS =
        point ->
            Stream.<Point>builder()
                .add(new Point(point.getX() - 1, point.getY())) // left
                .add(new Point(point.getX() + 1, point.getY())) // right
                .add(new Point(point.getX(), point.getY() - 1)) // down
                .add(new Point(point.getX(), point.getY() + 1)) // up
                .build();
}