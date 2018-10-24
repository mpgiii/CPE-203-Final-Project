import processing.core.PImage;

import java.util.List;

public class Quake implements Entity{

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;
    private int animationPeriod;

    public Quake(String id, Point position,
                  List<PImage> images,
                  int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
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
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
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
