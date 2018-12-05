import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

final class ImageStore {
    private Map<String, List<PImage>> images;
    private List<PImage> defaultImages;

    private static final int COLOR_MASK = 0xffffff;

    private static final int PROPERTY_KEY = 0;
    private static final String BGND_KEY = "background";
    private static final String MINER_KEY = "miner";
    private static final String OBSTACLE_KEY = "obstacle";
    private static final String ORE_KEY = "ore";
    private static final String SMITH_KEY = "blacksmith";
    private static final String VEIN_KEY = "vein";

    private static final String FSMITH_KEY = "frozenblacksmith";
    private static final String SBGND_KEY = "snowbackground";
    private static final String SMAN_KEY = "snowman";


    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    public List<PImage> getImageList(String key) {
        return images.getOrDefault(key, defaultImages);
    }

    public static List<PImage> getImages(Map<String, List<PImage>> images,
                                         String key) {
        List<PImage> imgs = images.get(key);
        if (imgs == null) {
            imgs = new LinkedList<>();
            images.put(key, imgs);
        }
        return imgs;
    }

    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public void load(Scanner in, WorldModel world) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            } catch (NumberFormatException e) {
                System.err.println(String.format("invalid entry on line %d",
                        lineNumber));
            } catch (IllegalArgumentException e) {
                System.err.println(String.format("issue on line %d: %s",
                        lineNumber, e.getMessage()));
            }
            lineNumber++;
        }
    }

    private boolean processLine(String line, WorldModel world) {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return world.parseBackground(properties, this);
                case MINER_KEY:
                    return world.parseMiner(properties, this);
                case OBSTACLE_KEY:
                    return world.parseObstacle(properties, this);
                case ORE_KEY:
                    return world.parseOre(properties, this);
                case SMITH_KEY:
                    return world.parseSmith(properties, this);
                case VEIN_KEY:
                    return world.parseVein(properties, this);
//                case FSMITH_KEY:
//                    return world.parseFrozenSmith(properties, this);
                case SBGND_KEY:
                    return world.parseSnowBackground(properties, this);
//                case SMAN_KEY:
//                    return world.parseSnowman(properties, this);
            }
        }

        return false;
    }

    public Map<String, List<PImage>> getImageMap() {
        return images;
    }
}
