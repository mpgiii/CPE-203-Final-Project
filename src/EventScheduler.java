import java.util.*;

final class EventScheduler
{
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double timeScale;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

    public void scheduleActions(ActiveEntity entity,
                                WorldModel world, ImageStore imageStore)
    {
        entity.scheduleEvent(this, new Activity(entity, world, imageStore), entity.getActionPeriod());
        if (entity instanceof AnimatedEntity)
            entity.scheduleEvent(this, new Animation((AnimatedEntity)entity, 0), ((AnimatedEntity)entity).getAnimationPeriod());

    }

    public void unscheduleAllEvents(Entity entity)
    {
        List<Event> pending = pendingEvents.remove(entity);

        if (pending != null)
        {
            for (Event event : pending)
            {
                eventQueue.remove(event);
            }
        }
    }

    private void removePendingEvent(Event event)
    {
        List<Event> pending = pendingEvents.get(event.getEntity());

        if (pending != null)
        {
            pending.remove(event);
        }
    }

    public void updateOnTime(long time)
    {
        while (!eventQueue.isEmpty() &&
                eventQueue.peek().getTime() < time)
        {
            Event next = eventQueue.poll();

            removePendingEvent(next);

            next.getAction().executeAction(this);
        }
    }

    public PriorityQueue<Event> getEventQueue() {
       return eventQueue;
    }
    public void addToEventQueue(Event event) {
       eventQueue.add(event);
    }
    public Map<Entity, List<Event>> getPendingEvents() {
       return pendingEvents;
    }
    public void putInPendingEvents(ActiveEntity entity, List<Event> pending) {
       pendingEvents.put(entity, pending);
    }
    public double getTimeScale() {
       return timeScale;
    }
}
