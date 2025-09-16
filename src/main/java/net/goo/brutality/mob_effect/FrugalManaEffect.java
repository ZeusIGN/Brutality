package net.goo.brutality.mob_effect;

import net.goo.brutality.registry.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FrugalManaEffect extends MobEffect {
    public static UUID MANA_SICKNESS_UUID = UUID.fromString("2d470df8-2cb0-414d-ae99-c59f97be2109");

    public FrugalManaEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(ModAttributes.MANA_COST.get(), String.valueOf(MANA_SICKNESS_UUID), 0, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, @NotNull AttributeModifier modifier) {
        return (amplifier + 1) * 0.01F;
    }
}

