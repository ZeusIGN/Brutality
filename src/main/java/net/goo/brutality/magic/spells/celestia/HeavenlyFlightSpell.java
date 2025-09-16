package net.goo.brutality.magic.spells.celestia;

import net.goo.brutality.magic.BrutalitySpell;
import net.goo.brutality.registry.BrutalityModSounds;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.mcreator.terramity.init.TerramityModMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HeavenlyFlightSpell extends BrutalitySpell {


    public HeavenlyFlightSpell() {
        super(MagicSchool.CELESTIA,
                List.of(SpellCategory.INSTANT, SpellCategory.SELF, SpellCategory.BUFF),
                "heavenly_flight",
                50, 0, 200, 0, 1, List.of(
                        new BrutalityTooltipHelper.SpellStatComponent(BrutalityTooltipHelper.SpellStatComponents.DURATION, 200, 100, null, null)
                ));
    }

    @Override
    public float getDamageLevelScaling() {
        return 0;
    }

    @Override
    public float getManaCostLevelScaling() {
        return 10;
    }

    @Override
    public int getCooldownLevelScaling() {
        return 140;
    }

    @Override
    public boolean onStartCast(Player player, ItemStack stack, int spellLevel) {
        float duration = getFinalStat(spellLevel, getStat(BrutalityTooltipHelper.SpellStatComponents.DURATION));
        player.addEffect(new MobEffectInstance(TerramityModMobEffects.MORTAL_FLIGHT.get(), (int) duration));
        player.playSound(BrutalityModSounds.WINGS_FLAP.get(), 1, Mth.nextFloat(player.getRandom(), 0.8F, 1.2F));
        return true;
    }
}
