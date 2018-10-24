import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public class MinerFull implements Entity
{

    private static final Random rand = new Random();

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int actionPeriod;
    private int animationPeriod;

    public MinerFull(String id, Point position,
                  List<PImage> images, int resourceLimit,
                  int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }
    public int getAnimationPeriod()
    {
        return animationPeriod;
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(position,
                Blacksmith.class);

        if (fullTarget.isPresent() &&
                this.moveToFull(world, fullTarget.get(), scheduler))
        {
            this.transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore, 0),
                    this.actionPeriod);
        }
    }


    public void transformFull(WorldModel world,
                              EventScheduler scheduler, ImageStore imageStore) {
        Entity miner = new MinerNotFull(id, position, images, resourceLimit,
                0, actionPeriod, animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        scheduler.scheduleActions(miner, world, imageStore);
    }

    public boolean moveToFull(WorldModel world,
                              Entity target, EventScheduler scheduler)
    {
        if (this.position.adjacent(target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = this.nextPositionMiner(world, target.getPosition());

            if (!this.position.equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }


    public Point nextPositionMiner(WorldModel world,
                                   Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - position.getX());
        Point newPos = new Point(position.getX() + horiz,
                position.getY());

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.getY() - position.getY());
            newPos = new Point(position.getX(),
                    position.getY() + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = position;
            }
        }

        return newPos;
    }

    public PImage getCurrentImage()
    {
        return (images.get(imageIndex));
    }



    public String getId() {
        return id;
    }
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point p) {
        position = p;
    }
    public List<PImage> getImages() {
        return images;
    }
    public int getActionPeriod() {
        return actionPeriod;
    }

}
