import processing.core.PImage;

import java.util.List;

public abstract class MinerEntity extends MovableEntity{

    private int resourceLimit;

    public MinerEntity(String id, Point position,
                         List<PImage> images, int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceLimit() { return resourceLimit; }
}
