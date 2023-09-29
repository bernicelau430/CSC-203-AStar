import processing.core.PImage;

import java.util.List;

public class Stump extends Entity {
    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;

    public Stump(String id, Point position, List<PImage> images, int imageIndex) {
        super(id, position, images, 0);
    }
}
