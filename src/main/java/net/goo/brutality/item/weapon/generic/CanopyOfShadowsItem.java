package net.goo.brutality.item.weapon.custom;

import net.goo.brutality.item.base.BrutalityGenericItem;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class CanopyOfShadows extends BrutalityGenericItem {


    public CanopyOfShadows(String identifier, Rarity rarity, List<BrutalityTooltipHelper.DescriptionComponent> descriptionComponents) {
        super(identifier, rarity, descriptionComponents);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof LivingEntity player) {
            if (player.getMainHandItem() == pStack || player.getOffhandItem() == pStack) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, true, false), player);

            }
        }
    }
}
