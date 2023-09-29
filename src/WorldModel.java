import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRowsWorld;
    private int numColsWorld;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;
    private static final int PROPERTY_KEY = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ROW = 3;
    private static final int ENTITY_NUM_PROPERTIES = 4;

    public WorldModel() {

    }

    public int getNumRowsWorld() {return this.numRowsWorld;}
    public int getNumColsWorld() {return this.numColsWorld;}
    public Set<Entity> getEntities() {return this.entities;}

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Sapling.SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[Sapling.SAPLING_HEALTH]);
            Sapling entity = new Sapling(id, pt, imageStore.getImageList(Sapling.SAPLING_KEY), health, Sapling.SAPLING_HEALTH_LIMIT);
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Sapling.SAPLING_KEY, Sapling.SAPLING_NUM_PROPERTIES));
        }
    }

    public void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Dude.DUDE_NUM_PROPERTIES) {
            Dude entity = new DudeNotFull(id, pt, imageStore.getImageList(Dude.DUDE_KEY), 0, Double.parseDouble(properties[Dude.DUDE_ACTION_PERIOD]), Double.parseDouble(properties[Dude.DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[Dude.DUDE_LIMIT]));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Dude.DUDE_KEY, Dude.DUDE_NUM_PROPERTIES));
        }
    }

    public void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Fairy.FAIRY_NUM_PROPERTIES) {
            Fairy entity = new Fairy(id, pt, imageStore.getImageList(Fairy.FAIRY_KEY), Double.parseDouble(properties[Fairy.FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[Fairy.FAIRY_ANIMATION_PERIOD]));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Fairy.FAIRY_KEY, Fairy.FAIRY_NUM_PROPERTIES));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Tree.TREE_NUM_PROPERTIES) {
            Tree entity = new Tree(id, pt, imageStore.getImageList(Tree.TREE_KEY), Double.parseDouble(properties[Tree.TREE_ACTION_PERIOD]), Double.parseDouble(properties[Tree.TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[Tree.TREE_HEALTH]));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Tree.TREE_KEY, Tree.TREE_NUM_PROPERTIES));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES) {
            Obstacle entity = new Obstacle(id, pt, imageStore.getImageList(Obstacle.OBSTACLE_KEY), Double.parseDouble(properties[Obstacle.OBSTACLE_ANIMATION_PERIOD]));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Obstacle.OBSTACLE_KEY, Obstacle.OBSTACLE_NUM_PROPERTIES));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == House.HOUSE_NUM_PROPERTIES) {
            House entity = new House(id, pt, imageStore.getImageList(House.HOUSE_KEY));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", House.HOUSE_KEY, House.HOUSE_NUM_PROPERTIES));
        }
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Stump.STUMP_NUM_PROPERTIES) {
            Stump entity = new Stump(id, pt, imageStore.getImageList(Stump.STUMP_KEY), 0);
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Stump.STUMP_KEY, Stump.STUMP_NUM_PROPERTIES));
        }
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRowsWorld && pos.getX() >= 0 && pos.getX() < this.numColsWorld;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    /*
                   Assumes that there is no entity currently occupying the
                   intended destination cell.
                */
    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell( pos, null);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        this.parseSaveFile(saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRowsWorld][this.numColsWorld];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRowsWorld][this.numColsWorld];
            this.entities = new HashSet<>();
        }
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRowsWorld][this.numColsWorld];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRowsWorld][this.numColsWorld];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRowsWorld = Integer.parseInt(line);
                    case "Cols:" -> this.numColsWorld = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRowsWorld){
            int rows = Math.min(cells.length, this.numColsWorld);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Obstacle.OBSTACLE_KEY -> this.parseObstacle(properties, pt, id, imageStore);
                case Dude.DUDE_KEY -> this.parseDude(properties, pt, id, imageStore);
                case Fairy.FAIRY_KEY -> this.parseFairy(properties, pt, id, imageStore);
                case House.HOUSE_KEY -> this.parseHouse(properties, pt, id, imageStore);
                case Tree.TREE_KEY -> this.parseTree(properties, pt, id, imageStore);
                case Sapling.SAPLING_KEY -> this.parseSapling(properties, pt, id, imageStore);
                case Stump.STUMP_KEY -> this.parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public Optional<Entity> findNearest(List<Class> kinds, Point point) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : getEntities()) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }
        return point.nearestEntity(ofType);
    }


    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
}
