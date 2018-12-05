import processing.core.PImage;

import java.util.List;

final class SnowBackground extends Background {
    private String id;
    private List<PImage> images;
    private int imageIndex;

    public SnowBackground(String id, List<PImage> images) {
        super(id, images);
    }
}
