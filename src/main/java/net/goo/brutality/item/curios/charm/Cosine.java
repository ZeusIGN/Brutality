package net.goo.brutality.item.curios.charm;

import net.goo.brutality.event.forge.ServerTickHandler;
import net.goo.brutality.event.forge.client.ClientTickHandler;
import net.goo.brutality.item.BrutalityCategories;
import net.goo.brutality.item.base.BrutalityCurioItem;
import net.goo.brutality.registry.BrutalityModItems;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class Cosine extends BrutalityCurioItem {


    public Cosine(Rarity rarity, List<BrutalityTooltipHelper.ItemDescriptionComponent> descriptionComponents) {
        super(rarity, descriptionComponents);
    }

    @Override
    public BrutalityCategories category() {
        return BrutalityCategories.CurioType.CHARM;
    }

    public static float getCurrentBonus(@Nullable Level level) {
        if (level instanceof ServerLevel) {
            return Mth.cos(ServerTickHandler.getServerTick() * 0.025f) * 0.25f + 0.125f;
        } else {
            return Mth.cos(ClientTickHandler.getClientTick() * 0.025f) * 0.25f + 0.125f;
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity())
                .map(handler ->
                        handler.findFirstCurio(BrutalityModItems.SCIENTIFIC_CALCULATOR.get()).isPresent()
                )
                .orElse(false);
    }

//    @Override
//    public void curioTick(SlotContext slotContext, ItemStack stack) {
//        if (slotContext.entity() instanceof Player player) {
//            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
//                AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
//                if (attackSpeed != null) {
//
//                    if (handler.isEquipped(BrutalityModItems.SCIENTIFIC_CALCULATOR_BELT.get())) {
//
//                        attackSpeed.removeModifier(COSINE_CHARM_AS_UUID);
//
//                        attackSpeed.addTransientModifier(
//                                new AttributeModifier(
//                                        COSINE_CHARM_AS_UUID,
//                                        "Temporary Speed Bonus",
//                                        getCurrentBonus(),
//                                        AttributeModifier.Operation.MULTIPLY_TOTAL
//                                )
//                        );
//                    } else {
//                        attackSpeed.removeModifier(COSINE_CHARM_AS_UUID);
//                    }
//                }
//            });
//        }
//    }
//
//    protected static final UUID COSINE_CHARM_AS_UUID = UUID.fromString("d11f8d34-2c5d-4fdc-880a-7a72500ba3e4");
//
//    @Override
//    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
//        if (slotContext.entity() instanceof Player player) {
//            AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
//            if (attackSpeed != null) {
//                attackSpeed.removeModifier(COSINE_CHARM_AS_UUID);
//            }
//        }
//    }


    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("curios.modifiers.charm").withStyle(ChatFormatting.GOLD));

        float value = getCurrentBonus(world) * 100;

        String formattedValue = value % 1 == 0 ?
                String.format("%.0f", value) :
                String.format("%.1f", value);

        tooltip.add(Component.literal((value >= 0 ? "+" : "") + formattedValue + "% ").append(Component.translatable("attribute.name.generic.attack_speed"))
                .withStyle(value >= 0 ? ChatFormatting.BLUE : ChatFormatting.RED));

    }
}
