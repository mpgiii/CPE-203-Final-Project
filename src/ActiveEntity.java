import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;

public abstract class ActiveEntity extends Entity {

    private int actionPeriod;

    public ActiveEntity(String id, Point position,
                        List<PImage> images, int actionPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected int getActionPeriod() {
        return actionPeriod;
    }

    protected void scheduleEvent(EventScheduler scheduler, Action action, long afterPeriod) {
        long time = System.currentTimeMillis() +
                (long) (afterPeriod * scheduler.getTimeScale());
        Event event = new Event(action, time, this);

        scheduler.addToEventQueue(event);

        // update list of pending events for the given entity
        List<Event> pending = scheduler.getPendingEvents().getOrDefault(this,
                new LinkedList<>());
        pending.add(event);
        scheduler.putInPendingEvents(this, pending);
    }

}
