import processing.core.PImage;

import java.util.List;

abstract class PlantHealth extends ActionEntity{
    private int health;
    private int healthLimit;

    public PlantHealth(String id, Point position, List<PImage> images, int imageIndex, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public int getHealth() {
        return this.health;
    }

    public int getHealthLimit() {
        return this.healthLimit;
    }

    public void decrementHealth() {
        health--;
    }

    public void incrementHealth() {
        health++;
    }

    abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

}
