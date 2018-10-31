import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class MovableEntity extends AnimatedEntity {

    public MovableEntity(String id, Point position,
                        List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public boolean moveTo(WorldModel world,
                          Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            _moveToHelper(world, target, scheduler);
            return true;
        }
        else
        {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
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

    abstract Point nextPosition(WorldModel world, Point destPos);

    protected abstract void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);

}
