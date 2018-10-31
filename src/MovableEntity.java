import processing.core.PImage;

import java.util.List;

public abstract class MovableEntity extends AnimatedEntity {

    public MovableEntity(String id, Point position,
                        List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    abstract Point nextPosition(WorldModel world, Point destPos);

}
