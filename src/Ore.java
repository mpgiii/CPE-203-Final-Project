import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ore implements Entity{

    private static final Random rand = new Random();

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;

    private static final String BLOB_KEY = "blob";
    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;

    public Ore(String id, Point position,
                  List<PImage> images,
                  int actionPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
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
        Point pos = position;  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity blob = new Ore_Blob(id + BLOB_ID_SUFFIX,
                pos, imageStore.getImageList(BLOB_KEY),
                actionPeriod / BLOB_PERIOD_SCALE, BLOB_ANIMATION_MIN +
                rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN)
        );

        world.addEntity(blob);
        scheduler.scheduleActions(blob, world, imageStore);
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
