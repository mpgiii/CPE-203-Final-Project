import java.util.*;

final class EventScheduler
{
   public PriorityQueue<Event> eventQueue;
   public Map<Entity, List<Event>> pendingEvents;
   public double timeScale;

    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }
   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * timeScale);
      Event event = new Event(action, time, entity);

      eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      pendingEvents.put(entity, pending);
   }

    public void scheduleActions(Entity entity,
                                       WorldModel world, ImageStore imageStore)
    {
        switch (entity.kind)
        {
            case MINER_FULL:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                this.scheduleEvent(entity, entity.createAnimationAction(0),
                        entity.getAnimationPeriod());
                break;

            case MINER_NOT_FULL:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                this.scheduleEvent(entity,
                        entity.createAnimationAction(0), entity.getAnimationPeriod());
                break;

            case ORE:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                break;

            case ORE_BLOB:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                this.scheduleEvent(entity,
                        entity.createAnimationAction(0), entity.getAnimationPeriod());
                break;

            case QUAKE:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                this.scheduleEvent(entity,
                        entity.createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
                        entity.getAnimationPeriod());
                break;

            case VEIN:
                this.scheduleEvent(entity,
                        entity.createActivityAction(world, imageStore),
                        entity.actionPeriod);
                break;

            default:
        }
    }
}
