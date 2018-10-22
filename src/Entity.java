import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public interface Entity
{
    void nextImage();
    void setPosition(Point p);
    PImage getCurrentImage();
    String getId();
    Point getPosition();
    List<PImage> getImages();
    int getAnimationPeriod();
    int getActionPeriod();
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}