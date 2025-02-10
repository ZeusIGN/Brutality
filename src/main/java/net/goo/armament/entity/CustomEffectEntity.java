//package net.goo.armament.entity;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientGamePacketListener;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.EntityDataSerializers;
//import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.network.NetworkHooks;
//import net.minecraftforge.registries.RegistryObject;
//import software.bernie.geckolib.animatable.GeoEntity;
//import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
//import software.bernie.geckolib.core.animation.AnimatableManager;
//import software.bernie.geckolib.core.animation.AnimationController;
//import software.bernie.geckolib.core.animation.RawAnimation;
//import software.bernie.geckolib.util.GeckoLibUtil;
//
//import javax.annotation.Nullable;
//import java.util.Optional;
//import java.util.UUID;
//
//public class CustomEffectEntity extends Entity implements GeoEntity {
//
//    // A lot of code from Celestisynth mod
//    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.OPTIONAL_UUID);
//    private static final EntityDataAccessor<String> VISUAL_ID = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.STRING);
//    private static final EntityDataAccessor<String> ANIMATION_ID = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.STRING);
//    private static final EntityDataAccessor<Integer> FRAME_LEVEL = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> SET_ROT_X = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Integer> SET_ROT_Z = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.INT);
//    private static final EntityDataAccessor<Float> CUSTOMIZABLE_SIZE = SynchedEntityData.defineId(CustomEffectEntity.class, EntityDataSerializers.FLOAT);
//
//    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
//    private static final RawAnimation DEFAULT_ANIMATION = RawAnimation.begin().thenLoop("spin");
//
//    private Entity toFollow;
//    private int lifespan;
//    private int frameTimer;
//
//    public CustomEffectEntity(EntityType<? extends CustomEffectEntity> pEntityType, Level pLevel) {
//        super(pEntityType, pLevel);
//        this.noCulling = true;
//    }
//
//    @Nullable
//    public static CustomEffectEntity getEffectInstance(LivingEntity owner, @Nullable Entity toFollow, CustomVisualType visual, double offsetX, double offsetY, double offsetZ) {
//        if (owner == null) return null;
//        CustomEffectEntity slash = ModEntities.CS_EFFECT.get().create(owner.level());
//        slash.setVisualID(visual.getName());
//        slash.setAnimationID(visual.getAnimation().getAnimName());
//        if (toFollow != null) {
//            slash.moveTo(toFollow.getX() + offsetX, (toFollow.getY() - 1.5) + offsetY, toFollow.getZ() + offsetZ);
//        } else {
//            slash.moveTo(owner.getX()  + offsetX, (owner.getY() - 1.5) + offsetY, owner.getZ() + offsetZ);
//        }
//        slash.setOwnerUuid(owner.getUUID());
//        slash.setToFollow(toFollow);
//        slash.setRandomRotation();
//        slash.setXRot(owner.getXRot());
//        slash.xRotO = slash.getXRot();
//        slash.setYRot(owner.getYRot());
//        slash.yRotO = slash.getYRot();
//        slash.setRot(slash.getYRot(), slash.getXRot());
//        return slash;
//    }
//
//    public void setRandomRotation() {
//        int rotationX = random.nextInt(360);
//        int rotationZ = random.nextInt(360);
//
//        setRotationX(rotationX);
//        setRotationZ(rotationZ);
//    }
//
//    public static void createInstance(LivingEntity owner, @Nullable Entity toFollow, CustomVisualType effectTypes) {
//        createInstance(owner, toFollow, effectTypes, 0, 0, 0);
//    }
//
//    public static void createInstanceLockedPos(LivingEntity owner, @Nullable Entity toFollow, CustomVisualType effectTypes, double x, double y, double z) {
//        if (owner == null) return;
//        CustomEffectEntity slash = getEffectInstance(owner, toFollow, effectTypes, x, y, z);
//        slash.moveTo(x, y, z);
//        owner.level().addFreshEntity(slash);
//    }
//
//    public static void createInstance(LivingEntity owner, @Nullable Entity toFollow, CustomVisualType effectTypes, double xOffset, double yOffset, double zOffset) {
//        if (owner == null) return;
//        CustomEffectEntity slash = getEffectInstance(owner, toFollow, effectTypes, xOffset, yOffset, zOffset);
//        slash.setOwnerUuid(owner.getUUID());
//        slash.setToFollow(toFollow);
//        owner.level().addFreshEntity(slash);
//    }
//
//    public boolean isControlledByLocalInstance() {
//        return true;
//    }
//
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, (state) -> {
//            if (getAnimationID() != null && getVisualType().getAnimation() != null) {
//                return state.setAndContinue(RawAnimation.begin().thenLoop(getAnimationID()));
//            } else {
//                return state.setAndContinue(DEFAULT_ANIMATION);
//            }
//        }));
//    }
//
//    public AnimatableInstanceCache getAnimatableInstanceCache() {
//        return this.cache;
//    }
//
//    public CustomVisualType getVisualType() {
//        if (getVisualID() != null && !getVisualID().equals("none")) {
//            for (RegistryObject<CustomVisualType> visual : CustomVisualType.VISUALS.getEntries()) {
//                if (getVisualID().equals(visual.get().getName())) {
//                    return visual.get();
//                }
//            }
//        }
//        return getDefaultVisual();
//    }
//
//    public void setVisualType(CustomVisualType getEffectType) {
//        setVisualID(getEffectType.getName());
//    }
//
//    public String getVisualID() {
//        return this.entityData.get(VISUAL_ID);
//    }
//
//    public void setVisualID(String value) {
//        this.entityData.set(VISUAL_ID, value);
//    }
//
//    public String getAnimationID() {
//        return this.entityData.get(ANIMATION_ID);
//    }
//
//    public void setAnimationID(String value) {
//        this.entityData.set(ANIMATION_ID, value);
//    }
//
//    public int getFrameLevel() {
//        return this.entityData.get(FRAME_LEVEL);
//    }
//
//    public void setFrameLevel(int value) {
//        this.entityData.set(FRAME_LEVEL, value);
//    }
//
//    public void setRotationX(int rotationX) {
//        this.entityData.set(SET_ROT_X, rotationX);
//    }
//
//    public void setRotationZ(int rotationZ) {
//        this.entityData.set(SET_ROT_Z, rotationZ);
//    }
//
//    public int getRotationX() {
//        return this.entityData.get(SET_ROT_X);
//    }
//
//    public int getRotationZ() {
//        return this.entityData.get(SET_ROT_Z);
//    }
//
//    public float getCustomizableSize() {
//        return this.entityData.get(CUSTOMIZABLE_SIZE);
//    }
//
//    public void setCustomizableSize(float size) {
//        this.entityData.set(CUSTOMIZABLE_SIZE, size);
//    }
//
//    public void setOwnerUuid(@Nullable UUID ownerUuid) {
//        this.entityData.set(OWNER_UUID, Optional.ofNullable(ownerUuid));
//    }
//
//    @Nullable
//    public UUID getOwnerUuid() {
//        return this.entityData.get(OWNER_UUID).orElse(null);
//    }
//
//    public Entity getToFollow() {
//        return this.toFollow;
//    }
//
//    public void setToFollow(Entity livingEntity) {
//        this.toFollow = livingEntity;
//    }
//
//    public void setLifespan(int value) {
//        lifespan = value;
//    }
//
//    @Override
//    public void tick() {
//        if (getOwnerUuid() == null) {
//            remove(RemovalReason.DISCARDED);
//        }
//
//        frameTimer++;
//
//        if (frameTimer >= (getVisualType().getFramesSpeed())) {
//            setFrameLevel(getFrameLevel() == getVisualType().getFrames() ? 1 : getFrameLevel() + 1);
//
//            frameTimer = 0;
//        }
//
//        lifespan++;
//
//        if (lifespan >= getVisualType().getAnimation().getLifespan()) {
//            setLifespan(0);
//            remove(RemovalReason.DISCARDED);
//        }
//
//        super.tick();
//    }
//
//    public void calculateCustomizableSize(float size, double yOff) {
//        setYRot(toFollow.getYRot());
//        this.yRotO = getYRot();
//        setRot(getYRot(), getXRot());
//        setCustomizableSize(size);
//        moveTo(toFollow.getX(), toFollow.getY() + yOff, toFollow.getZ());
//    }
//
//    @Override
//    protected void defineSynchedData() {
//        this.entityData.define(OWNER_UUID, Optional.empty());
//        this.entityData.define(VISUAL_ID, "none");
//        this.entityData.define(ANIMATION_ID, "none");
//        this.entityData.define(FRAME_LEVEL, 1);
//        this.entityData.define(SET_ROT_X, 0);
//        this.entityData.define(SET_ROT_Z, 0);
//        this.entityData.define(CUSTOMIZABLE_SIZE, 1F);
//    }
//
//    @Override
//    public void readAdditionalSaveData(CompoundTag compoundNBT) {
//        this.remove(RemovalReason.DISCARDED);
//    }
//
//    @Override
//    public void addAdditionalSaveData(CompoundTag compoundNBT) {
//        this.remove(RemovalReason.DISCARDED);
//    }
//
//    @Override
//    public Packet<ClientGamePacketListener> getAddEntityPacket() {
//        return NetworkHooks.getEntitySpawningPacket(this);
//    }
//}
