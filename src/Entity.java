import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */

abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position, List<PImage> images, int imageIndex) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
    }

    public Point getPosition() {return this.position;}

    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }

    public String getId() {return this.id;}

    public List<PImage> getImages() {return this.images;}

    public int getImageIndex() {return this.imageIndex;}

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.getX(), this.position.getY(), this.imageIndex);
    }
}
