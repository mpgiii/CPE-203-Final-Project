import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Snowman extends MovableEntity {


    private static final String FSMITH_KEY = "frozenblacksmith";

    private PathingStrategy strategy = new AStarPathingStrategy();

    private int steps;

    public Snowman(String id, Point position,
                    List<PImage> images,
                    int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.steps = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> snowmanTarget = world.findNearest(getPosition(), Blacksmith.class);
        long nextPeriod = getActionPeriod();

        if (snowmanTarget.isPresent()) {
            Point tgtPos = snowmanTarget.get().getPosition();

            if (moveTo(world, snowmanTarget.get(), scheduler)) {
                Entity frozenSmith = new FrozenBlacksmith(FSMITH_KEY, tgtPos,   //to be replaced
                        imageStore.getImageList(FSMITH_KEY));

                world.addEntity(frozenSmith);
                nextPeriod += getActionPeriod();
            }
        }

        scheduleEvent(scheduler,
                new Activity(this, world, imageStore),
                nextPeriod);

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
        incrementStep(world);
        if (points.size() != 0) {
            world.setBackground(points.get(0), world.getBackgroundCell(getPosition()));
            return points.get(0);
        }
        return getPosition();
    }

    private void incrementStep(WorldModel world) {
        if (steps > 25) {
            world.removeEntity(this);
        }
        else
            steps += 1;
    }

}
