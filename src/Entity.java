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
        Optional<Entity> fullTarget = findNearest(world, position,
                EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() &&
                moveToFull(this, world, fullTarget.get(), scheduler))
        {
            transformFull(this, world, scheduler, imageStore);
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
        Optional<Entity> notFullTarget = findNearest(world, position,
                EntityKind.ORE);

        if (!notFullTarget.isPresent() ||
                !moveToNotFull(this, world, notFullTarget.get(), scheduler) ||
                !transformNotFull(this, world, scheduler, imageStore))
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

        removeEntity(world, this);
        unscheduleAllEvents(scheduler, this);

        Entity blob = createOreBlob(id + BLOB_ID_SUFFIX,
                pos, actionPeriod / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
                getImageList(imageStore, BLOB_KEY));

        addEntity(world, blob);
        scheduleActions(blob, scheduler, world, imageStore);
    }

    public void executeOreBlobActivity(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> blobTarget = findNearest(world,
                position, EntityKind.VEIN);
        long nextPeriod = actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().position;

            if (moveToOreBlob(this, world, blobTarget.get(), scheduler))
            {
                Entity quake = createQuake(tgtPos,
                        getImageList(imageStore, QUAKE_KEY));

                addEntity(world, quake);
                nextPeriod += this.actionPeriod;
                scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                nextPeriod);
    }

    public void executeQuakeActivity(WorldModel world,
                                            ImageStore imageStore, EventScheduler scheduler)
    {
        unscheduleAllEvents(scheduler, this);
        removeEntity(world, this);
    }

    public void executeVeinActivity(WorldModel world,
                                           ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = findOpenAround(world, position);

        if (openPt.isPresent())
        {
            Entity ore = createOre(ORE_ID_PREFIX + id,
                    openPt.get(), ORE_CORRUPT_MIN +
                            rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    getImageList(imageStore, ORE_KEY));
            addEntity(world, ore);
            scheduleActions(ore, scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
    }
}
