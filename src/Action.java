final class Action
{
   public ActionKind kind;
   public Entity entity;
   public WorldModel world;
   public ImageStore imageStore;
   public int repeatCount;

   public Action(ActionKind kind, Entity entity, WorldModel world,
      ImageStore imageStore, int repeatCount)
   {
      this.kind = kind;
      this.entity = entity;
      this.world = world;
      this.imageStore = imageStore;
      this.repeatCount = repeatCount;
   }

   public void executeAction(EventScheduler scheduler)
   {
      switch (kind)
      {
         case ACTIVITY:
            executeActivityAction(scheduler);
            break;

         case ANIMATION:
            executeAnimationAction(scheduler);
            break;
      }
   }

   public void executeAnimationAction(EventScheduler scheduler)
   {
      entity.nextImage();

      if (repeatCount != 1)
      {
         scheduler.scheduleEvent(entity,
                 createAnimationAction(entity,
                         Math.max(repeatCount - 1, 0)),
                 entity.getAnimationPeriod());
      }
   }

   public void executeActivityAction(EventScheduler scheduler)
   {
      switch (entity.kind)
      {
         case MINER_FULL:
            executeMinerFullActivity(entity, world,
                    imageStore, scheduler);
            break;

         case MINER_NOT_FULL:
            executeMinerNotFullActivity(entity, world,
                    imageStore, scheduler);
            break;

         case ORE:
            executeOreActivity(entity, world, imageStore,
                    scheduler);
            break;

         case ORE_BLOB:
            executeOreBlobActivity(entity, world,
                    imageStore, scheduler);
            break;

         case QUAKE:
            executeQuakeActivity(entity, world, imageStore,
                    scheduler);
            break;

         case VEIN:
            executeVeinActivity(entity, world, imageStore,
                    scheduler);
            break;

         default:
            throw new UnsupportedOperationException(
                    String.format("executeActivityAction not supported for %s",
                            entity.kind));
      }
   }
}
