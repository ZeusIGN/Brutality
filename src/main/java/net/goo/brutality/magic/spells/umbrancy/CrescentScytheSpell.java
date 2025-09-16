package net.goo.brutality.magic.spells.umbrancy;

import net.goo.brutality.entity.spells.umbrancy.CrescentScythe;
import net.goo.brutality.event.forge.DelayedTaskScheduler;
import net.goo.brutality.magic.BrutalitySpell;
import net.goo.brutality.registry.BrutalityModEntities;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

import static net.goo.brutality.magic.IBrutalitySpell.SpellCategory.AOE;
import static net.goo.brutality.magic.IBrutalitySpell.SpellCategory.INSTANT;
import static net.goo.brutality.util.helpers.BrutalityTooltipHelper.SpellStatComponents.QUANTITY;
import static net.goo.brutality.util.helpers.BrutalityTooltipHelper.SpellStatComponents.SPEED;

public class CrescentScytheSpell extends BrutalitySpell {


    public CrescentScytheSpell() {
        super(MagicSchool.UMBRANCY,
                List.of(INSTANT, AOE),
                "crescent_scythe",
                40, 3, 100, 0, 1, List.of(
                        new BrutalityTooltipHelper.SpellStatComponent(QUANTITY, 1, 0.5F, 1F, null),
                        new BrutalityTooltipHelper.SpellStatComponent(SPEED, 0.25F, 0, null, null)
                ));
    }

    @Override
    public float getDamageLevelScaling() {
        return 2F;
    }

    @Override
    public boolean onStartCast(Player player, ItemStack stack, int spellLevel) {
        int quantity = Mth.floor(getFinalStat(spellLevel, getStat(QUANTITY)));
        float speed = getFinalStat(spellLevel, getStat(SPEED));
        float gap = 15f; // Reduced gap for tighter arc; adjust as needed
        Level level = player.level();

        for (int i = 0; i < quantity; i++) {
            float angleOffset = (i - (quantity - 1) / 2f) * gap; // Center the arc
            CrescentScythe crescentScythe = new CrescentScythe(BrutalityModEntities.CRESCENT_SCYTHE_ENTITY.get(), level);
            crescentScythe.setSpellLevel(spellLevel);
            crescentScythe.setOwner(player);
            crescentScythe.setPos(player.getX(), player.getY(0.75f), player.getZ());
            crescentScythe.shootFromRotation(player, player.getXRot(), player.getYRot() + angleOffset, 0, speed, 0);

            if (i > 0) {
                DelayedTaskScheduler.queueServerWork(i * 2, () -> level.addFreshEntity(crescentScythe)); // Small delay for staggered spawn
            } else {
                level.addFreshEntity(crescentScythe);
            }
        }

        return true;
    }
}
