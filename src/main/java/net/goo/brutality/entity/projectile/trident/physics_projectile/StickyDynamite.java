package net.goo.brutality.entity.projectile.trident.physics_projectile;

import net.goo.brutality.entity.base.BrutalityAbstractThrowingProjectile;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StickyDynamite extends Dynamite{


    public StickyDynamite(EntityType<? extends BrutalityAbstractThrowingProjectile> pEntityType, Level pLevel, ResourceKey<DamageType> damageTypeResourceKey) {
        super(pEntityType, pLevel, damageTypeResourceKey);
    }

    public StickyDynamite(EntityType<? extends BrutalityAbstractThrowingProjectile> pEntityType, Player player, Level pLevel, ResourceKey<DamageType> damageTypeResourceKey) {
        super(pEntityType, player, pLevel, damageTypeResourceKey);
    }

    @Override
    public String model() {
        return "dynamite";
    }

    @Override
    protected int getBounceQuota() {
        return 0;
    }

    protected Vec3 getEntityBounceStrength() {
        return super.getEntityBounceStrength().scale(0);
    }

}
