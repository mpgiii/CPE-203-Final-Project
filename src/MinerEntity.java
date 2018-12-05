import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class MinerEntity extends MovableEntity{

    private PathingStrategy strategy = new AStarPathingStrategy();

    private int resourceLimit;

    public MinerEntity(String id, Point position,
                         List<PImage> images, int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceLimit() { return resourceLimit; }

    protected abstract int getResourceCount();

//    public void freeze(WorldModel world, EventScheduler scheduler) {
//        Background background = world.getBackgroundCell(getPosition());
//        if ((background.getClass() == SnowBackground.class)) {
//            System.out.println("Hi");
//            MovableEntity miner = new ColdMiner("frozenminer", getPosition(), getImages(), getResourceLimit(),
//                    getResourceCount(), 2, 3);
//            world.removeEntity(this);
//            scheduler.unscheduleAllEvents(this);
//
//            world.addEntity(miner);
//        }
//    }

}
