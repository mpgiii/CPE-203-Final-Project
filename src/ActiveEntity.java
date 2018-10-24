public interface ActiveEntity extends Entity {
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}
