public class Activity implements Action{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(Entity entity, WorldModel world,
                  ImageStore imageStore, int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        entity.executeActivity(world, imageStore, scheduler);
    }

}
