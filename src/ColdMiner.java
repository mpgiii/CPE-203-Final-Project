import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class ColdMiner extends MovableEntity {
    private int steps;
    private ActiveEntity previousMiner;

    private PathingStrategy strategy = new SingleStepPathingStrategy();

    public ColdMiner(String id, Point position,
                    List<PImage> images,
                    int actionPeriod, int animationPeriod, ActiveEntity previousMiner) {
        super(id, position, images, actionPeriod, animationPeriod);

        this.previousMiner = previousMiner;
        this.steps = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(previousMiner);
        scheduler.scheduleActions(previousMiner, world, imageStore);

    }


    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
    }

    public Point nextPosition(WorldModel world,
                              Point destPos) {
        List<Point> points;
        points = strategy.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p),
                Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() != 0) {
            world.setBackground(points.get(0), world.getBackgroundCell(getPosition()));
            return points.get(0);
        }
        return getPosition();
    }


}
