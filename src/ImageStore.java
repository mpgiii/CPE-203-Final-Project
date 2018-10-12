import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

final class ImageStore
{
   public Map<String, List<PImage>> images;
   public List<PImage> defaultImages;

    public static final int COLOR_MASK = 0xffffff;

    public static final int PROPERTY_KEY = 0;
    public static final String BGND_KEY = "background";
    public static final String MINER_KEY = "miner";
    public static final String OBSTACLE_KEY = "obstacle";
    public static final String ORE_KEY = "ore";
    public static final String SMITH_KEY = "blacksmith";
    public static final String VEIN_KEY = "vein";


   public ImageStore(PImage defaultImage)
   {
      this.images = new HashMap<>();
      defaultImages = new LinkedList<>();
      defaultImages.add(defaultImage);
   }

   public List<PImage> getImageList(String key)
   {
      return images.getOrDefault(key, defaultImages);
   }

    public static List<PImage> getImages(Map<String, List<PImage>> images,
                                         String key)
    {
        List<PImage> imgs = images.get(key);
        if (imgs == null)
        {
            imgs = new LinkedList<>();
            images.put(key, imgs);
        }
        return imgs;
    }

    public static void setAlpha(PImage img, int maskColor, int alpha)
    {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++)
        {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha)
            {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public void load(Scanner in, WorldModel world)
    {
        int lineNumber = 0;
        while (in.hasNextLine())
        {
            try
            {
                if (!processLine(in.nextLine(), world))
                {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e)
            {
                System.err.println(String.format("invalid entry on line %d",
                        lineNumber));
            }
            catch (IllegalArgumentException e)
            {
                System.err.println(String.format("issue on line %d: %s",
                        lineNumber, e.getMessage()));
            }
            lineNumber++;
        }
    }

    public boolean processLine(String line, WorldModel world)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0)
        {
            switch (properties[PROPERTY_KEY])
            {
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
            }
        }

        return false;
    }
}
