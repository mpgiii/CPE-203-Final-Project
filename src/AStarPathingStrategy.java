import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.*;
import java.util.stream.*;

public class AStarPathingStrategy
        implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {

        List<Point> path = new LinkedList<>();

        Comparator<MyNode> sorter = Comparator.comparing(MyNode::getF);
        PriorityQueue<MyNode> open = new PriorityQueue<>(sorter);
        Map<Point, MyNode> closed = new HashMap<>();

        int initG = 0;
        int initH = getHeuristic(start, end);
        int initF = initG + initH;

        MyNode current = new MyNode(start, initG, initH, initF, null);
        open.add(current);

        while (open.size() > 0) {
            current = open.poll();

            if (withinReach.test(current.getPos(), end))
            {
                writePath(path, current);
                break;
            }

            closed.put(current.getPos(), current);

            List<Point> neighbors = potentialNeighbors
                    .apply(current.getPos())
                    .filter(canPassThrough)
                    .filter(p -> !closed.containsKey(p))
                    .collect(Collectors.toList());

            for (Point neighbor : neighbors) {
                initG = current.getG() + 1;
                initH = getHeuristic(neighbor, end);
                initF = initH + initG;

                MyNode neigh = new MyNode(neighbor, initG, initH, initF, current);

                if (!open.contains(neigh)) //WRONG: need to see if it contains a node with that point.
                    open.add(neigh);

            }

        }

        return path;

    }

    private void writePath(List<Point> path, MyNode node) {
        if (node.getPrior() == null)
            return;
        path.add(0, node.getPos());
        if (node.getPrior().getPrior() != null){
            writePath(path, node.getPrior());
        }
    }

    private int getHeuristic(Point p1, Point p2) {
        return Math.abs((p1.getY() - p2.getY()) + (p1.getX() - p2.getX()));
    }

    private class MyNode {
        private Point position;
        private int g;
        private int h;
        private int f;
        private MyNode prior;


        public MyNode(Point position, int g, int h, int f, MyNode prior) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = f;
            this.prior = prior;
        }

        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            return ((MyNode)other).position.equals(this.position);
        }


        public int getF() { return f; }
        public int getG() { return g; }
        public int getH() { return h; }
        public Point getPos() { return position; }
        public MyNode getPrior() { return prior; }
    }
}