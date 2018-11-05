import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends MinerEntity {
    private int resourceCount;

    public MinerNotFull(String id, Point position,
                        List<PImage> images, int resourceLimit, int resourceCount,
                        int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit);

        this.resourceCount = resourceCount;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(),
                Ore.class);

        if (!notFullTarget.isPresent() ||
                !this.moveTo(world, notFullTarget.get(), scheduler) ||
                !this.transform(world, scheduler, imageStore)) {
            scheduleEvent(scheduler,
                    new Activity(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world,
                             EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= getResourceLimit()) {
            MovableEntity miner = new MinerFull(getId(), getPosition(), getImages(),
                    getResourceLimit(), getActionPeriod(), getAnimationPeriod());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            scheduler.scheduleActions(miner, world, imageStore);

            return true;
        }

        return false;
    }


    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        this.resourceCount += 1;
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
    }

}
