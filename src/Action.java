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
                 entity.createAnimationAction(Math.max(repeatCount - 1, 0)),
                 entity.getAnimationPeriod());
      }
   }

   public void executeActivityAction(EventScheduler scheduler)
   {
      switch (entity.kind)
      {
         case MINER_FULL:
            entity.executeMinerFullActivity(world,
                    imageStore, scheduler);
            break;

         case MINER_NOT_FULL:
            entity.executeMinerNotFullActivity(world,
                    imageStore, scheduler);
            break;

         case ORE:
            entity.executeOreActivity(world, imageStore,
                    scheduler);
            break;

         case ORE_BLOB:
            entity.executeOreBlobActivity(world,
                    imageStore, scheduler);
            break;

         case QUAKE:
            entity.executeQuakeActivity(world, imageStore,
                    scheduler);
            break;

         case VEIN:
            entity.executeVeinActivity(world, imageStore,
                    scheduler);
            break;

         default:
            throw new UnsupportedOperationException(
                    String.format("executeActivityAction not supported for %s",
                            entity.kind));
      }
   }
}
