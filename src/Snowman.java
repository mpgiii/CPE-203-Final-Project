import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Snowman extends MovableEntity {

    private static final String QUAKE_KEY = "quake";
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;

    private static final String FSMITH_KEY = "frozenblacksmith";
    private static final int FSMITH_NUM_PROPERTIES = 4;
    private static final int FSMITH_ID = 1;
    private static final int FSMITH_COL = 2;
    private static final int FSMITH_ROW = 3;

    private PathingStrategy strategy = new AStarPathingStrategy();

    public Snowman(String id, Point position,
                    List<PImage> images,
                    int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
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
        if (points.size() != 0)
            return points.get(0);
        return getPosition();
    }


}
