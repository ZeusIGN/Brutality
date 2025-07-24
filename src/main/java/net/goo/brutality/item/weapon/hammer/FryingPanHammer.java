//package net.goo.brutality.item.weapon.hammer;
//
//import com.google.common.collect.ImmutableMultimap;
//import com.google.common.collect.Multimap;
//import net.goo.brutality.item.base.BrutalityGeoItem;
//import net.goo.brutality.item.base.BrutalityHammerItem;
//import net.goo.brutality.registry.BrutalityModMobEffects;
//import net.goo.brutality.registry.BrutalityModSounds;
//import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.util.Mth;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.Attribute;
//import net.minecraft.world.entity.ai.attributes.AttributeModifier;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Rarity;
//import net.minecraft.world.item.Tier;
//import net.minecraftforge.common.ForgeMod;
//import software.bernie.geckolib.core.animation.AnimatableManager;
//
//import java.util.List;
//import java.util.UUID;
//
//public class FryingPanHammer extends BrutalityHammerItem implements BrutalityGeoItem {
//
//
//    public FryingPanHammer(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Rarity rarity, List<BrutalityTooltipHelper.DescriptionComponent> descriptionComponents) {
//        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, rarity, descriptionComponents);
//    }
//
//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//    }
//
//    UUID FRYING_PAN_RANGE_UUID = UUID.fromString("cf6097a9-e0fb-4c1e-b561-474c0f0e73ad");
//
//    @Override
//    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
//        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
//
//        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
//            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
//            builder.putAll(modifiers);
//            builder.put(
//                    ForgeMod.ENTITY_REACH.get(),
//                    new AttributeModifier(
//                            FRYING_PAN_RANGE_UUID,
//                            "Reach bonus",
//                            2,
//                            AttributeModifier.Operation.ADDITION
//                    )
//            );
//
//            return builder.build();
//        }
//        return modifiers;
//    }
//
//    @Override
//    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
//        pAttacker.level().playSound(null, pAttacker.getOnPos(), BrutalityModSounds.FRYING_PAN_HIT.get(), SoundSource.PLAYERS, 1F, Mth.nextFloat(pAttacker.getRandom(), 0.8F, 1.2F));
//        pTarget.addEffect(new MobEffectInstance(BrutalityModMobEffects.MASHED.get(), 60, 1, false, true));
//
//        return super.hurtEnemy(pStack, pTarget, pAttacker);
//    }
//}
