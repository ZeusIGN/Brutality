package net.goo.brutality.magic.spells.voidwalker;

import net.goo.brutality.entity.spells.voidwalker.GraviticImplosionEntity;
import net.goo.brutality.magic.BrutalitySpell;
import net.goo.brutality.registry.BrutalityModEntities;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GraviticImplosionSpell extends BrutalitySpell {


    public GraviticImplosionSpell() {
        super(MagicSchool.VOIDWALKER,
                List.of(SpellCategory.INSTANT, SpellCategory.AOE),
                "gravitic_implosion",
                30, 5, 80, 0, 1, List.of(
                        new BrutalityTooltipHelper.SpellStatComponent(BrutalityTooltipHelper.SpellStatComponents.SIZE, 3, 1, 3F, 50F)
                ));
    }

    @Override
    public float getDamageLevelScaling() {
        return 1;
    }


    @Override
    public boolean onStartCast(Player player, ItemStack stack, int spellLevel) {
        GraviticImplosionEntity graviticImplosionEntity = new GraviticImplosionEntity(BrutalityModEntities.GRAVITIC_IMPLOSION_ENTITY.get(), player.level());
        graviticImplosionEntity.setSpellLevel(spellLevel);
        graviticImplosionEntity.setPos(player.getEyePosition());
        graviticImplosionEntity.setOwner(player);
        graviticImplosionEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0F, Math.min(0.5F + (spellLevel * 0.1F), 1), 0);
        player.level().addFreshEntity(graviticImplosionEntity);
        return true;
    }
}
