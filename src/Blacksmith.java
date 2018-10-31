import java.util.List;

import processing.core.PImage;

final class Blacksmith extends Entity
{

    public Blacksmith(String id, Point position,
                  List<PImage> images, int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

}