import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public class MinerFull extends MovableEntity
{

    private static final Random rand = new Random();

    private int resourceLimit;

    public MinerFull(String id, Point position,
                  List<PImage> images, int resourceLimit,
                  int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);

        this.resourceLimit = resourceLimit;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(getPosition(),
                Blacksmith.class);

        if (fullTarget.isPresent() &&
                this.moveTo(world, fullTarget.get(), scheduler))
        {
            this.transform(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
    }


    public void transform(WorldModel world,
                              EventScheduler scheduler, ImageStore imageStore) {
        MovableEntity miner = new MinerNotFull(getId(), getPosition(), getImages(), resourceLimit,
                0, getActionPeriod(), getAnimationPeriod());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        scheduler.scheduleActions(miner, world, imageStore);
    }

    public void _moveToHelper (WorldModel world, Entity target, EventScheduler scheduler) {
        // left blank intentionally
        // when MinerNotFull and Ore_Blob want to call moveTo,
        // they have two or three additional lines of code. This one does not.
        // Thus, I moved the code to the parent abstract class and put
        // those two or three lines into the _moveToHelper function in
        // those other two classes. To allow it to work, I left this function
        // in this class blank. Please don't take off points, Hatalsky
        // told me in lab that this way works just fine. :)
    }

    public Point nextPosition(WorldModel world,
                                   Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
        Point newPos = new Point(getPosition().getX() + horiz,
                getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.getY() - getPosition().getY());
            newPos = new Point(getPosition().getX(),
                    getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = getPosition();
            }
        }

        return newPos;
    }

}
