import java.util.List;
import java.util.Optional;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {return this.x;}

    public int getY() {return this.y;}

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public Optional<Entity> nearestEntity(List<Entity> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.getPosition(), this);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.getPosition(), this);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }
            return Optional.of(nearest);
        }
    }

    public int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public boolean adjacent(Point p2) {
        return (getX() == p2.getX() && Math.abs(getY() - p2.getY()) == 1) || (getY() == p2.getY() && Math.abs(getX() - p2.getX()) == 1);
    }
}
