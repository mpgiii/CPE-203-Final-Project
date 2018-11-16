import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class MovableEntity extends AnimatedEntity {

    public MovableEntity(String id, Point position,
                         List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    private PathingStrategy strategy = new AStarPathingStrategy();

    protected boolean moveTo(WorldModel world,
                             Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            _moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                occupant.ifPresent(scheduler::unscheduleAllEvents);
                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected Point nextPosition(WorldModel world,
                              Point destPos) {
        List<Point> points;
        points = strategy.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p),
                Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() != 0)
            return points.get(0);
        return getPosition();
    }

    protected abstract void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);

}
