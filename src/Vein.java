import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Vein implements Entity{

    private static final Random rand = new Random();

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;

    private static final String ORE_KEY = "ore";

    public Vein(String id, Point position,
                  List<PImage> images, int resourceLimit, int resourceCount,
                  int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public int getAnimationPeriod() {
        return 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(position);

        if (openPt.isPresent())
        {
            Entity ore = new Ore(ORE_ID_PREFIX + id,
                    openPt.get(), imageStore.getImageList(ORE_KEY), 0, 0,
                    ORE_CORRUPT_MIN + rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    0
                    );
            world.addEntity(ore);
            scheduler.scheduleActions(ore, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore, 0),
                actionPeriod);
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
