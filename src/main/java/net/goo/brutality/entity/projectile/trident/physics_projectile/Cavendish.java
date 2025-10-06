package net.goo.brutality.entity.projectile.trident.physics_projectile;

import net.goo.brutality.client.entity.BrutalityGeoEntity;
import net.goo.brutality.entity.base.BrutalityAbstractPhysicsTrident;
import net.goo.brutality.entity.base.BrutalityAbstractTrident;
import net.goo.brutality.registry.BrutalityModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Cavendish extends BrutalityAbstractPhysicsTrident implements BrutalityGeoEntity {


    public Cavendish(EntityType<? extends BrutalityAbstractTrident> pEntityType, Player player, Level pLevel) {
        super(pEntityType, player, pLevel);
    }

    public Cavendish(EntityType<? extends BrutalityAbstractTrident> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected int getLifespan() {
        return 200;
    }

    @Override
    public float getModelHeight() {
        return 8;
    }


    @Override
    public @NotNull SoundEvent getHitGroundSoundEvent() {
        return BrutalityModSounds.SQUELCH.get();
    }

    @Override
    public SoundEvent getHitEntitySoundEvent() {
        return BrutalityModSounds.SQUELCH.get();
    }

    @Override
    protected int getBounceQuota() {
        return 0;
    }

    @Override
    protected float getBounciness() {
        return 0.25F;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        triggerAnim("controller", "ground");
    }

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        if (inGround) {
            List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2));

            for (LivingEntity livingEntity : nearbyEntities) {
                onSteppedOn(livingEntity);
            }

        }
    }

    private void onSteppedOn(LivingEntity livingEntity) {

        livingEntity.push(random.nextFloat() * 0.1, 0, random.nextFloat() * 0.1);
        if (getOwner() instanceof Player player)
            livingEntity.hurt(livingEntity.damageSources().playerAttack(player), 5);
        else
            livingEntity.hurt(livingEntity.damageSources().generic(), 5);

        discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", (state) ->
                state.setAndContinue(RawAnimation.begin().thenLoop("idle")))
                .triggerableAnim("ground", RawAnimation.begin().thenPlayAndHold("ground"))
        );
    }
}
