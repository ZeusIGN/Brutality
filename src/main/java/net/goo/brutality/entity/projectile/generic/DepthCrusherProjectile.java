package net.goo.brutality.entity.projectile.generic;

import net.goo.brutality.client.entity.BrutalityGeoEntity;
import net.goo.brutality.particle.providers.TrailParticleData;
import net.goo.brutality.registry.BrutalityModParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DepthCrusherProjectile extends ThrowableProjectile implements BrutalityGeoEntity {
    private static final EntityDataAccessor<Integer> HOMING_TARGET_ID = SynchedEntityData.defineId(DepthCrusherProjectile.class, EntityDataSerializers.INT);

    public DepthCrusherProjectile(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HOMING_TARGET_ID, -1);
    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this, true);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public void tick() {

        if (level().isClientSide()) {
            if (firstTick) {
                this.level().addParticle(new TrailParticleData(BrutalityModParticles.TRAIL_PARTICLE.get(),
                                0.078F, 0.078F, 0.33F, 1, this.getBbHeight() * 4F, this.getId(), 10), this.getX(),
                        this.getY() - this.getBbHeight() / 2, this.getZ(), 0, 0, 0);
            }
        }

        super.tick();

        if (this.tickCount > 100) discard();
        if (this.entityData.get(HOMING_TARGET_ID) == -1) {
            LivingEntity entity = level().getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT.selector(target -> target != getOwner()),
                    null, getX(), getY(), getZ(), getBoundingBox().inflate(10));

            this.entityData.set(HOMING_TARGET_ID, entity != null ? entity.getId() : -1);
        } else {
            Entity entity = level().getEntity(entityData.get(HOMING_TARGET_ID));
            this.setNoGravity(true);

            if (entity instanceof LivingEntity target) {
                Vec3 targetVec = target.getPosition(1).add(0, entity.getBbHeight() / 2, 0).subtract(getPosition(1));
                double distance = targetVec.length();

                if (distance > 0.01) {
//                        double scale = 0.05 * (distance + 0.1);
                    double scale = 1 / (distance * 2 + 0.1) + 0.25;
                    Vec3 motion = targetVec.normalize().scale(scale);
                    this.addDeltaMovement(motion);

                    if (this.getDeltaMovement().length() > 2) {
                        this.setDeltaMovement(this.getDeltaMovement().scale(0.85));
                    }

                }

            }
        }

    }


    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        entity.invulnerableTime = 0;
        Entity owner = getOwner();
        if (owner != null && entity != owner) {
            entity.hurt(entity.damageSources().indirectMagic(owner, this), 5);
        }
        this.discard();
        super.onHitEntity(pResult);
    }
}
