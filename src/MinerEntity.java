import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class MinerEntity extends MovableEntity{

    private PathingStrategy strategy = new SingleStepPathingStrategy();
    private int resourceLimit;

    public MinerEntity(String id, Point position,
                         List<PImage> images, int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    public Point nextPosition(WorldModel world,
                              Point destPos) {
        List<Point> points;
        points = strategy.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p),
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() != 0)
            return points.get(0);
        return getPosition();
    }

    protected int getResourceLimit() { return resourceLimit; }
}
