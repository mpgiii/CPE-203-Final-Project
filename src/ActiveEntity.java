import processing.core.PImage;

import java.util.List;

public abstract class ActiveEntity extends Entity {

    public ActiveEntity(String id, Point position,
                  List<PImage> images, int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}
