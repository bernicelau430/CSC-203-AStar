import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {

    private class Node {
        private final Point point;
        private int g; // cost from start node
        private int h; // heuristic cost to the target node
        private int f; // total cost (g + h)
        private Node parent;

        public Node(Point point, int g, int h, Node parent) {
            this.point = point;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<>();
        /* define closed list
           define open list
           while (true){
            Filtered list containing neighbors you can actually move to
            Check if any of the neighbors are beside the target
            set the g, h, f values
            add them to open list if not in open list
            add the selected node to close list
           return path*/

        // Define closed list to store visited nodes
        Set<Point> closedList = new HashSet<>();

        // Define open list to store nodes to be explored
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));

        // Create the starting node with initial values
        Node startNode = new Node(start, 0, calculateHeuristic(start, end), null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            // Get the node with the lowest total cost (f) from the open list
            Node currentNode = openList.poll();
            Point currentPoint = currentNode.point;

            // Check if the current point is the target point
            if (withinReach.test(currentPoint, end)) {
                // Reconstruct the path by following the parent nodes
                Node node = currentNode;
                while (node.parent != null) {
                    path.add(0, node.point);
                    node = node.parent;
                }
                return path;
            }

            // Add the current point to the closed list
            closedList.add(currentPoint);

            // Generate potential neighbors of the current point
            Stream<Point> neighbors = potentialNeighbors.apply(currentPoint);

            neighbors.filter(canPassThrough)
                    .filter(neighbor -> !closedList.contains(neighbor))
                    .forEach(neighbor -> {
                        int g = currentNode.g + 1; // assuming all steps have a cost of 1

                        // Calculate the heuristic cost from the neighbor to the target
                        int h = calculateHeuristic(neighbor, end);

                        // Create a new node for the neighbor
                        Node neighborNode = new Node(neighbor, g, h, currentNode);

                        // Check if the neighbor is already in the open list
                        // If it is, update its g value if the new path is better
                        // Otherwise, add the neighbor to the open list
                        if (isOpenListContainsNodeWithPoint(openList, neighbor)) {
                            Node existingNode = getNodeFromOpenListWithPoint(openList, neighbor);
                            if (g < existingNode.g) {
                                existingNode.g = g;
                                existingNode.f = g + existingNode.h;
                                existingNode.parent = currentNode;
                            }
                        } else {
                            openList.add(neighborNode);
                        }
                    });
        }

        return path; // Return an empty path if no path is found
    }

    // Helper method to calculate the Manhattan distance between two points
    private int calculateHeuristic(Point point, Point target) {
        return Math.abs(point.x - target.x + Math.abs(point.y - target.y));
    }

    // Helper method to check if the open list contains a node with a specific point
    private boolean isOpenListContainsNodeWithPoint(PriorityQueue<Node> openList, Point point) {
        return openList.stream()
                .anyMatch(node -> node.point.equals(point));
    }

    // Helper method to retrieve the node with a specific point from the open list
    private Node getNodeFromOpenListWithPoint(PriorityQueue<Node> openList, Point point) {
        return openList.stream()
                .filter(node -> node.point.equals(point))
                .findFirst()
                .orElse(null);
    }

}
