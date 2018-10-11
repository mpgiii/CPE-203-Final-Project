import java.util.List;
import java.util.Optional;

import processing.core.PImage;

final class Entity
{
   public EntityKind kind;
   public String id;
   public Point position;
   public List<PImage> images;
   public int imageIndex;
   public int resourceLimit;
   public int resourceCount;
   public int actionPeriod;
   public int animationPeriod;

    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;

    public static final String QUAKE_KEY = "quake";

    public static final String ORE_KEY = "ore";

   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }
    public int getAnimationPeriod()
    {
        switch (kind)
        {
            case MINER_FULL:
            case MINER_NOT_FULL:
            case ORE_BLOB:
            case QUAKE:
                return animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                kind));
        }
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public Action createAnimationAction(int repeatCount)
    {
        return new Action(ActionKind.ANIMATION, this, null, null, repeatCount);
    }

    public Action createActivityAction(WorldModel world,
                                              ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, this, world, imageStore, 0);
    }


    public void executeMinerFullActivity(WorldModel world,
                                                ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = Functions.findNearest(world, position,
                EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() &&
                this.moveToFull(world, fullTarget.get(), scheduler))
        {
            this.transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeMinerNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget = Functions.findNearest(world, position,
                EntityKind.ORE);

        if (!notFullTarget.isPresent() ||
                !this.moveToNotFull(world, notFullTarget.get(), scheduler) ||
                !this.transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeOreActivity(WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = position;  // store current position before removing

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        Entity blob = Functions.createOreBlob(id + BLOB_ID_SUFFIX,
                pos, actionPeriod / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        Functions.rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
                Functions.getImageList(imageStore, BLOB_KEY));

        Functions.addEntity(world, blob);
        Functions.scheduleActions(blob, scheduler, world, imageStore);
    }

    public void executeOreBlobActivity(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> blobTarget = Functions.findNearest(world,
                position, EntityKind.VEIN);
        long nextPeriod = actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().position;

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler))
            {
                Entity quake = Functions.createQuake(tgtPos,
                        Functions.getImageList(imageStore, QUAKE_KEY));

                Functions.addEntity(world, quake);
                nextPeriod += this.actionPeriod;
                Functions.scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                nextPeriod);
    }

    public void executeQuakeActivity(WorldModel world,
                                            ImageStore imageStore, EventScheduler scheduler)
    {
        Functions.unscheduleAllEvents(scheduler, this);
        Functions.removeEntity(world, this);
    }

    public void executeVeinActivity(WorldModel world,
                                           ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = Functions.findOpenAround(world, position);

        if (openPt.isPresent())
        {
            Entity ore = Functions.createOre(ORE_ID_PREFIX + id,
                    openPt.get(), ORE_CORRUPT_MIN +
                            Functions.rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    Functions.getImageList(imageStore, ORE_KEY));
            Functions.addEntity(world, ore);
            Functions.scheduleActions(ore, scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
    }

    public boolean transformNotFull(WorldModel world,
                                           EventScheduler scheduler, ImageStore imageStore)
    {
        if (resourceCount >= resourceLimit)
        {
            Entity miner = Functions.createMinerFull(id, resourceLimit,
                    position, actionPeriod, animationPeriod,
                    images);

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            Functions.addEntity(world, miner);
            Functions.scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void transformFull(WorldModel world,
                                     EventScheduler scheduler, ImageStore imageStore)
    {
        Entity miner = Functions.createMinerNotFull(id, resourceLimit,
                position, actionPeriod, animationPeriod,
                images);

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        Functions.addEntity(world, miner);
        Functions.scheduleActions(miner, scheduler, world, imageStore);
    }

    public boolean moveToNotFull(WorldModel world,
                                        Entity target, EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position))
        {
            this.resourceCount += 1;
            Functions.removeEntity(world, target);
            Functions.unscheduleAllEvents(scheduler, target);

            return true;
        }
        else
        {
            Point nextPos = Functions.nextPositionMiner(this, world, target.position);

            if (!this.position.equals(nextPos))
            {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent())
                {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(WorldModel world,
                                     Entity target, EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position))
        {
            return true;
        }
        else
        {
            Point nextPos = Functions.nextPositionMiner(this, world, target.position);

            if (!this.position.equals(nextPos))
            {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent())
                {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToOreBlob(WorldModel world,
                                        Entity target, EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position))
        {
            Functions.removeEntity(world, target);
            Functions.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else
        {
            Point nextPos = Functions.nextPositionOreBlob(this, world, target.position);

            if (!this.position.equals(nextPos))
            {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent())
                {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

}
