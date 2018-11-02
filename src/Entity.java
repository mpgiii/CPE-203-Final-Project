import processing.core.PImage;

import java.util.List;

public abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position,
                  List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    protected PImage getCurrentImage() {
        return (images.get(imageIndex));
    }

    protected int getImageIndex() {
        return imageIndex;
    }

    protected void setImageIndex(int newIndex) {
        imageIndex = newIndex;
    }

    protected String getId() {
        return id;
    }

    protected Point getPosition() {
        return position;
    }

    protected void setPosition(Point p) {
        position = p;
    }

    protected List<PImage> getImages() {
        return images;
    }
}