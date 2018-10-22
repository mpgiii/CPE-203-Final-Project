import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Create {

    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;



    public static Action createAnimationAction(Entity entity, int repeatCount)
    {
        return new Animation(entity, null, null, repeatCount);
    }

    public static Action createActivityAction(WorldModel world, Entity entity,
                                       ImageStore imageStore)
    {
        return new Activity(entity, world, imageStore, 0);
    }


    public static MinerFull createMinerFull(String id, int resourceLimit,
                                         Point position, int actionPeriod, int animationPeriod,
                                         List<PImage> images)
    {
        return new MinerFull(id, position, images,
                resourceLimit, resourceLimit, actionPeriod, animationPeriod);
    }

    public static MinerNotFull createMinerNotFull(String id, int resourceLimit,
                                            Point position, int actionPeriod, int animationPeriod,
                                            List<PImage> images)
    {
        return new MinerNotFull(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }


    public static Blacksmith createBlacksmith(String id, Point position,
                                           List<PImage> images)
    {
        return new Blacksmith(id, position, images,
                0, 0, 0, 0);
    }

    public static Obstacle createObstacle(String id, Point position,
                                         List<PImage> images)
    {
        return new Obstacle(id, position, images,
                0, 0, 0, 0);
    }

    public static Ore createOre(String id, Point position, int actionPeriod,
                                   List<PImage> images)
    {
        return new Ore(id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public static Ore_Blob createOreBlob(String id, Point position,
                                       int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new Ore_Blob(id, position, images,
                0, 0, actionPeriod, animationPeriod);
    }

    public static Quake createQuake(Point position, List<PImage> images)
    {
        return new Quake(QUAKE_ID, position, images,
                0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(String id, Point position, int actionPeriod,
                                     List<PImage> images)
    {
        return new Vein(id, position, images, 0, 0,
                actionPeriod, 0);
    }
}