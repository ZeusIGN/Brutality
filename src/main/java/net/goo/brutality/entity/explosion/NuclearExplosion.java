package net.goo.brutality.entity.explosion;

import net.goo.brutality.registry.BrutalityModMobEffects;
import net.goo.brutality.registry.BrutalityModParticles;
import net.goo.brutality.registry.BrutalityModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NuclearExplosion extends BrutalityExplosion {

    public NuclearExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pPositions);
    }

    public NuclearExplosion(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Level.ExplosionInteraction explosionInteraction) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, explosionInteraction);
    }

    public NuclearExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Level.ExplosionInteraction explosionInteraction) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, explosionInteraction);
    }

    public NuclearExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Level.ExplosionInteraction explosionInteraction, List<BlockPos> pPositions) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, explosionInteraction, pPositions);
    }

    @Override
    protected SimpleParticleType getParticle() {
        return BrutalityModParticles.NAPALM_EXPLOSION_PARTICLE.get();
    }

    @Override
    protected SimpleParticleType getParticleEmitter() {
        return BrutalityModParticles.NUCLEAR_EXPLOSION_EMITTER.get();
    }

    @Override
    protected SoundEvent getExplosionSound() {
        return BrutalityModSounds.BIG_EXPLOSION.get();
    }

    @Override
    protected boolean needsInteractWithBlocksForEmitter() {
        return false;
    }

    @Override
    public void onHit(Entity entity, double impactFactor) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(BrutalityModMobEffects.RADIATION.get(), ((int) (40 * impactFactor))));
        }
    }
}
