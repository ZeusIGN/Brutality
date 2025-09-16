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

public class Sine extends BrutalityCurioItem {


    public Sine(Rarity rarity, List<BrutalityTooltipHelper.ItemDescriptionComponent> descriptionComponents) {
        super(rarity, descriptionComponents);
    }

    public static float getCurrentBonus(@Nullable Level level) {
        if (level instanceof ServerLevel) {
            return Mth.sin(ServerTickHandler.getServerTick() * 0.025f) * 5f + 2.5f;
        } else {
            return Mth.sin(ClientTickHandler.getClientTick() * 0.025f) * 5f + 2.5f;
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

    @Override
    public BrutalityCategories category() {
        return BrutalityCategories.CurioType.CHARM;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("curios.modifiers.charm").withStyle(ChatFormatting.GOLD));
        float value = getCurrentBonus(world);

        String formattedValue = value % 1 == 0 ?
                String.format("%.0f", value) :
                String.format("%.1f", value);

        tooltip.add(Component.literal((value >= 0 ? "+" : "") + formattedValue + " ").append(Component.translatable("attribute.name.generic.attack_damage"))
                .withStyle(value >= 0 ? ChatFormatting.BLUE : ChatFormatting.RED));

    }

}
