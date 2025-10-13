package net.goo.brutality.entity.projectile.trident.physics_projectile;

import com.lowdragmc.photon.client.fx.EntityEffect;
import net.goo.brutality.client.entity.BrutalityGeoEntity;
import net.goo.brutality.entity.base.BrutalityAbstractThrowingProjectile;
import net.mcreator.terramity.init.TerramityModParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;

import static net.goo.brutality.util.ModResources.PHOTON_TRAIL_FX;

public class Photon extends BrutalityAbstractThrowingProjectile implements BrutalityGeoEntity {
    private Vec3 deltaMovementOld = Vec3.ZERO;

    public Photon(EntityType<? extends BrutalityAbstractThrowingProjectile> pEntityType, Level pLevel, ResourceKey<DamageType> damageTypeResourceKey) {
        super(pEntityType, pLevel, damageTypeResourceKey);
    }

    public Photon(EntityType<? extends BrutalityAbstractThrowingProjectile> pEntityType, Player player, Level pLevel, ResourceKey<DamageType> damageTypeResourceKey) {
        super(pEntityType, player, pLevel, damageTypeResourceKey);
    }

    @Override
    public int getInGroundLifespan() {
        return 200;
    }


    @Override
    public @NotNull SoundEvent getHitGroundSoundEvent() {
        return SoundEvents.EMPTY;
    }


    @Override
    public SoundEvent getHitEntitySoundEvent() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected int getLifespan() {
        return 40;
    }

    @Override
    protected float getDamageMultiplier() {
        System.out.println(getDeltaMovement());
        return (float) getDeltaMovement().length();
    }

    @Override
    public void tick() {
        if (firstTick && !(level() instanceof ServerLevel)) {
            EntityEffect photonTrail = new EntityEffect(PHOTON_TRAIL_FX, level(), this, EntityEffect.AutoRotate.NONE);
            photonTrail.start();
        }

        super.tick();
    }



    @Override
    protected void onHit(@NotNull HitResult pResult) {
        Vec3 loc = pResult.getLocation();
        for (int i = 0; i < 5; i++) {
            level().addParticle(TerramityModParticleTypes.HOLY_GLINT.get(), loc.x, loc.y, loc.z,
                    Mth.nextFloat(random, -0.2F, 0.2F),
                    Mth.nextFloat(random, -0.2F, 0.2F),
                    Mth.nextFloat(random, -0.2F, 0.2F)
            );
        }
        super.onHit(pResult);
    }



    @Override
    public void setNoGravity(boolean pNoGravity) {
        super.setNoGravity(true);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
}
