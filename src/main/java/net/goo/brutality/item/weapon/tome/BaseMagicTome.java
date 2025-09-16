package net.goo.brutality.item.weapon.tome;

import com.mojang.blaze3d.platform.InputConstants;
import net.goo.brutality.Brutality;
import net.goo.brutality.item.BrutalityCategories;
import net.goo.brutality.item.base.BrutalityGenericItem;
import net.goo.brutality.magic.IBrutalitySpell;
import net.goo.brutality.magic.SpellCastingHandler;
import net.goo.brutality.magic.SpellStorage;
import net.goo.brutality.magic.SpellUtils;
import net.goo.brutality.registry.BrutalityModSounds;
import net.goo.brutality.registry.ModAttributes;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.mcreator.terramity.init.TerramityModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.List;

import static net.goo.brutality.util.helpers.BrutalityTooltipHelper.SpellStatComponents.CHANCE;
import static net.goo.brutality.util.helpers.BrutalityTooltipHelper.SpellStatComponents.DURATION;

public class BaseMagicTome extends BrutalityGenericItem {


    public BaseMagicTome(Rarity rarity, List<BrutalityTooltipHelper.ItemDescriptionComponent> descriptionComponents) {
        super(rarity, descriptionComponents);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putBoolean("closed", true);
        return stack;
    }

    @Override
    public BrutalityCategories category() {
        return BrutalityCategories.ItemType.TOME;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SpellStorage.SpellEntry spellEntry = SpellStorage.getCurrentSpellEntry(stack);
        if (spellEntry == null) {
            return InteractionResultHolder.pass(stack);
        }

        IBrutalitySpell spell = spellEntry.spell();
        int actualSpellLevel = IBrutalitySpell.getActualSpellLevel(player, spell, spellEntry.level());
        List<IBrutalitySpell.SpellCategory> categories = spell.getCategories();

        SpellCastingHandler.CastConditionResult initialCheck = SpellCastingHandler.checkAllConditions(player, spell, actualSpellLevel);
        if (!initialCheck.canCast()) {
            player.displayClientMessage(initialCheck.feedback(), true);
            return InteractionResultHolder.fail(stack);
        }

        if (categories.contains(IBrutalitySpell.SpellCategory.CHANNELLING)) {
            player.startUsingItem(hand);
            // Redirect to useTick
            if (level instanceof ServerLevel serverLevel) {
                stack.getOrCreateTag().putBoolean("closed", false);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), BrutalityModSounds.TOME_OPEN.get(), SoundSource.PLAYERS, 1, 1);
                stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "book_controller", "close");
                triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "main_controller", "cast_channelling");
            }
            return InteractionResultHolder.pass(stack);
        } else if (categories.contains(IBrutalitySpell.SpellCategory.CONTINUOUS)) {
            if (SpellCastingHandler.startContinuousCast(player, stack, spell, actualSpellLevel)) {
                player.startUsingItem(hand);
                if (level instanceof ServerLevel serverLevel) {
                    stack.getOrCreateTag().putBoolean("closed", false);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), BrutalityModSounds.TOME_OPEN.get(), SoundSource.PLAYERS, 1, 1);
                    stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "book_controller", "close");
                    triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "main_controller", "cast_continuous");
                }
                return InteractionResultHolder.pass(stack);
            }
            // No redirect, just cast
        } else if (categories.contains(IBrutalitySpell.SpellCategory.INSTANT)) {
            if (SpellCastingHandler.castInstantSpell(player, stack, spell, actualSpellLevel)) {
                if (level instanceof ServerLevel serverLevel) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), TerramityModSounds.TOMEUSE.get(), SoundSource.PLAYERS, 1, 1);
                    stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "book_controller", "close");
                    triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "main_controller", "cast_instant");
                    player.getCooldowns().addCooldown(this, 30);
                }
                return InteractionResultHolder.pass(stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingTicks) {
        if (!(livingEntity instanceof Player player)) return;
        SpellStorage.SpellEntry spellEntry = SpellStorage.getCurrentSpellEntry(stack);
        if (spellEntry == null) return;

        IBrutalitySpell spell = spellEntry.spell();
        int spellLevel = IBrutalitySpell.getActualSpellLevel(player, spell, spellEntry.level());

        if (spell.getCategories().contains(IBrutalitySpell.SpellCategory.CHANNELLING)) {
            if (SpellCastingHandler.castChannellingSpell(player, stack, spell, spellLevel, remainingTicks)) {
                player.releaseUsingItem();
            }
        } else if (spell.getCategories().contains(IBrutalitySpell.SpellCategory.CONTINUOUS)) {
            SpellCastingHandler.tickContinuousCast(player, stack, spell, spellLevel);
        }
    }


    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int ticksUsed) {
        if (!(livingEntity instanceof Player player)) return;
        SpellStorage.SpellEntry spellEntry = SpellStorage.getCurrentSpellEntry(stack);
        if (spellEntry == null) return;

        IBrutalitySpell spell = spellEntry.spell();
        int spellLevel = IBrutalitySpell.getActualSpellLevel(player, spell, spellEntry.level());

        if (spell.getCategories().contains(IBrutalitySpell.SpellCategory.CONTINUOUS)) {
            SpellCastingHandler.endContinuousCast(player, stack, spell, spellLevel);
        }
        if (!level.isClientSide()) {
            closeBook(player, stack);
            player.getCooldowns().addCooldown(this, 30);
        }
    }


    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (!isSelected && entity instanceof Player player)
            tryCloseBook(player, stack);
    }


    @Override
    public boolean onDroppedByPlayer(ItemStack stack, Player player) {
        tryCloseBook(player, stack);
        return super.onDroppedByPlayer(stack, player);
    }

    public void closeBook(Player player, ItemStack stack) {
        Level level = player.level();
        SpellStorage.SpellEntry spellEntry = SpellStorage.getCurrentSpellEntry(stack);
        if (spellEntry != null) {
            spellEntry.spell().onEndCast(player, stack, IBrutalitySpell.getActualSpellLevel(player, spellEntry.spell(), spellEntry.level()));
        }
        if (!level.isClientSide()) {
            stack.getOrCreateTag().putBoolean("closed", true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), BrutalityModSounds.TOME_CLOSE.get(), SoundSource.PLAYERS, 1, 1);
            stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level), "main_controller", "cast_channelling");
            stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level), "main_controller", "cast_continuous");
            triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level), "book_controller", "close");
        }
    }

    public void tryCloseBook(Player player, ItemStack stack) {
        if (!stack.getOrCreateTag().getBoolean("closed")) {
            closeBook(player, stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (level != null && !level.isClientSide()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        List<SpellStorage.SpellEntry> spells = SpellStorage.getSpells(stack);
        if (spells.isEmpty()) {
            tooltip.add(Component.translatable("spell." + Brutality.MOD_ID + ".empty"));
            return;
        }

        if (InputConstants.isKeyDown(mc.getWindow().getWindow(), mc.options.keyShift.getKey().getValue())) {
            for (SpellStorage.SpellEntry entry : spells) {
                IBrutalitySpell spell = entry.spell();
                String spellName = spell.getSpellName();

                int actualSpellLevel = IBrutalitySpell.getActualSpellLevel(mc.player, spell, entry.level());

                float manaCostReduction = (float) mc.player.getAttributeValue(ModAttributes.MANA_COST.get());
                float spellDamageMultiplier = (float) mc.player.getAttributeValue(ModAttributes.SPELL_DAMAGE.get());
                float spellCdReduction = 2 - (float) mc.player.getAttributeValue(ModAttributes.SPELL_COOLDOWN_REDUCTION.get());
                float castTimeReduction = 2 - (float) mc.player.getAttributeValue(ModAttributes.CAST_TIME_REDUCTION.get());
                castTimeReduction += SpellUtils.getCurioCastTimeMultiplier(mc.player, spell, actualSpellLevel) - 1;
                spellCdReduction += SpellUtils.getCurioCooldownMultiplier(mc.player, spell, actualSpellLevel) - 1;
                spellDamageMultiplier += SpellUtils.getCurioDamageMultiplier(mc.player, spell, actualSpellLevel) - 1;

                boolean showDamageMulti = spellDamageMultiplier != 1F;
                boolean showManaReduct = manaCostReduction != 1F;
                boolean showCDReduct = spellCdReduction != 1F;
                boolean showCastReduct = castTimeReduction != 1F;

                // Constants for consistent formatting
                MutableComponent divider = Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY);
                float manaCost = IBrutalitySpell.getActualManaCost(mc.player, spell, actualSpellLevel);
                float castTime = IBrutalitySpell.getActualCastTime(mc.player, spell, actualSpellLevel);
                castTime /= 20;
                float spellCooldown = IBrutalitySpell.getActualCooldown(mc.player, spell, actualSpellLevel);
                spellCooldown /= 20;
                float finalDamage = spell.getFinalDamage(mc.player, actualSpellLevel);

                int bonusLevel = actualSpellLevel - entry.level();
                // Spell name and actualSpellLevel

                MutableComponent spellType = Component.literal(" §8|§r ");

                spell.getCategories().forEach(category -> spellType.append(category.icon + " "));

                tooltip.add(Component.translatable("spell." + Brutality.MOD_ID + "." + spellName).append(" §8|§r " + entry.level() +
                        (bonusLevel > 0 ? " + §l" + bonusLevel : "")).append(spellType));

                for (int i = 1; i <= spell.getDescriptionCount(); i++) {
                    tooltip.add(Component.translatable("spell." + Brutality.MOD_ID + "." + spellName + ".description." + i));
                }

                if (finalDamage > 0) {
                    String damageOperator = spell.getDamageLevelScaling() > 0 ? " + " : " - ";
                    tooltip.add(Component.literal("\uD83D\uDDE1 §8|§6 " + (showDamageMulti ? "(" : "") + spell.getBaseDamage() + "§r ❤§6" + damageOperator + "(" +
                            Mth.abs(spell.getDamageLevelScaling()) + "§r ❤§6 * level)" + (showDamageMulti ? ") * " + String.format("%.2f", spellDamageMultiplier) : "") + " = §2" + finalDamage + "§r ❤"));

                }
                if (castTime > 0) {
                    String castTimeOperator = spell.getCastTimeLevelScaling() > 0 ? " + " : " - ";
                    tooltip.add(Component.literal("\uD83E\uDE84 §8|§6 " + (showCastReduct ? "(" : "") + spell.getBaseCastTime() / 20 + "s" + castTimeOperator + "(" +
                            Mth.abs(((float) spell.getCastTimeLevelScaling()) / 20) + "s * level)" + (showCastReduct ? ") * " + String.format("%.2f", castTimeReduction) : "") + " = §2" + castTime + "s"));
                }
                // Mana cost line
                String manaOperator = spell.getManaCostLevelScaling() > 0 ? " + " : " - ";
                tooltip.add(Component.literal("\uD83D\uDCA7 §8|§6 " + (showManaReduct ? "(" : "") + spell.getBaseManaCost() + "§r \uD83D\uDCA7§6" + manaOperator + "(" +
                        Mth.abs(spell.getManaCostLevelScaling()) + "§r \uD83D\uDCA7§6 * level)" + (showManaReduct ? ") * " + String.format("%.2f", manaCostReduction) : "") + " = §2" + manaCost + "§r \uD83D\uDCA7"));

                // Cooldown line
                String cdOperator = spell.getCooldownLevelScaling() > 0 ? " + " : " - ";
                tooltip.add(Component.literal("⌛ §8|§6 " + (showCDReduct ? "(" : "") + spell.getBaseCooldown() / 20 + "s" + cdOperator + "(" +
                        Mth.abs(((float) spell.getCooldownLevelScaling()) / 20) + "s * level)" + (showCDReduct ? ") * " + String.format("%.2f", spellCdReduction) : "") + " = §2" + spellCooldown + "s"));


                if (spell.getStatComponents() != null)
                    for (BrutalityTooltipHelper.SpellStatComponent component : spell.getStatComponents()) {
                        MutableComponent finalComponent = getMutableComponent(mc.player, entry, component);

                        if (component.min() != null) {
                            finalComponent.append(divider)
                                    .append(Component.literal("ᴍɪɴ " + computeUnit(component.min(), component.type())).withStyle(ChatFormatting.RED));
                        }

                        if (component.max() != null) {
                            finalComponent.append(divider)
                                    .append(Component.literal("ᴍᴀx " + computeUnit(component.max(), component.type())).withStyle(ChatFormatting.GREEN));
                        }

                        tooltip.add(finalComponent);

                    }


                tooltip.add(Component.empty());

            }
        } else {

            SpellStorage.SpellEntry entry = SpellStorage.getCurrentSpellEntry(stack);
            if (entry == null) return;
            IBrutalitySpell spell = entry.spell();
            String spellName = spell.getSpellName();
            int actualSpellLevel = IBrutalitySpell.getActualSpellLevel(mc.player, spell, entry.level());

            float manaCostReduction = (float) mc.player.getAttributeValue(ModAttributes.MANA_COST.get());
            float spellDamageMultiplier = (float) mc.player.getAttributeValue(ModAttributes.SPELL_DAMAGE.get());
            float spellCdReduction = 2 - (float) mc.player.getAttributeValue(ModAttributes.SPELL_COOLDOWN_REDUCTION.get());
            float castTimeReduction = 2 - (float) mc.player.getAttributeValue(ModAttributes.CAST_TIME_REDUCTION.get());
            spellCdReduction += SpellUtils.getCurioCooldownMultiplier(mc.player, spell, actualSpellLevel) - 1;
            castTimeReduction += SpellUtils.getCurioCastTimeMultiplier(mc.player, spell, actualSpellLevel) - 1;
            spellDamageMultiplier += SpellUtils.getCurioDamageMultiplier(mc.player, spell, actualSpellLevel) - 1;

            boolean showDamageMulti = spellDamageMultiplier != 1F;
            boolean showManaReduct = manaCostReduction != 1F;
            boolean showCDReduct = spellCdReduction != 1F;
            boolean showCastReduct = castTimeReduction != 1F;

            // Constants for consistent formatting
            MutableComponent divider = Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY);
            float manaCost = IBrutalitySpell.getActualManaCost(mc.player, spell, actualSpellLevel);
            float castTime = IBrutalitySpell.getActualCastTime(mc.player, spell, actualSpellLevel);
            castTime /= 20;
            float spellCooldown = IBrutalitySpell.getActualCooldown(mc.player, spell, actualSpellLevel);
            spellCooldown /= 20;
            float finalDamage = spell.getFinalDamage(mc.player, actualSpellLevel);

            int bonusLevel = actualSpellLevel - entry.level();
            // Spell name and actualSpellLevel

            MutableComponent spellType = Component.literal(" §8|§r ");

            spell.getCategories().forEach(category -> {
                spellType.append(category.icon + " ");
            });

            tooltip.add(Component.translatable("spell." + Brutality.MOD_ID + "." + spellName).append(" §8|§r " + entry.level() +
                    (bonusLevel > 0 ? " + §l" + bonusLevel : "")).append(spellType));

            for (int i = 1; i <= spell.getDescriptionCount(); i++) {
                tooltip.add(Component.translatable("spell." + Brutality.MOD_ID + "." + spellName + ".description." + i));
            }

            if (finalDamage > 0) {
                String damageOperator = spell.getDamageLevelScaling() > 0 ? " + " : " - ";
                tooltip.add(Component.literal("\uD83D\uDDE1 §8|§6 " + (showDamageMulti ? "(" : "") + spell.getBaseDamage() + "§r ❤§6" + damageOperator + "(" +
                        Mth.abs(spell.getDamageLevelScaling()) + "§r ❤§6 * level)" + (showDamageMulti ? ") * " + String.format("%.2f", spellDamageMultiplier) : "") + " = §2" + finalDamage + "§r ❤"));

            }
            if (castTime > 0) {
                String castTimeOperator = spell.getCastTimeLevelScaling() > 0 ? " + " : " - ";
                tooltip.add(Component.literal("\uD83E\uDE84 §8|§6 " + (showCastReduct ? "(" : "") + spell.getBaseCastTime() / 20 + "s" + castTimeOperator + "(" +
                        Mth.abs(((float) spell.getCastTimeLevelScaling()) / 20) + "s * level)" + (showCastReduct ? ") * " + String.format("%.2f", castTimeReduction) : "") + " = §2" + castTime + "s"));
            }
            // Mana cost line
            String manaOperator = spell.getManaCostLevelScaling() > 0 ? " + " : " - ";
            tooltip.add(Component.literal("\uD83D\uDCA7 §8|§6 " + (showManaReduct ? "(" : "") + spell.getBaseManaCost() + "§r \uD83D\uDCA7§6" + manaOperator + "(" +
                    Mth.abs(spell.getManaCostLevelScaling()) + "§r \uD83D\uDCA7§6 * level)" + (showManaReduct ? ") * " + String.format("%.2f", manaCostReduction) : "") + " = §2" + manaCost + "§r \uD83D\uDCA7"));

            // Cooldown line
            String cdOperator = spell.getCooldownLevelScaling() > 0 ? " + " : " - ";
            tooltip.add(Component.literal("\u231b §8|§6 " + (showCDReduct ? "(" : "") + spell.getBaseCooldown() / 20 + "s" + cdOperator + "(" +
                    Mth.abs(((float) spell.getCooldownLevelScaling()) / 20) + "s * level)" + (showCDReduct ? ") * " + String.format("%.2f", spellCdReduction) : "") + " = §2" + spellCooldown + "s"));


            if (spell.getStatComponents() != null)
                for (BrutalityTooltipHelper.SpellStatComponent component : spell.getStatComponents()) {
                    MutableComponent finalComponent = getMutableComponent(mc.player, entry, component);

                    if (component.min() != null) {
                        finalComponent.append(divider)
                                .append(Component.literal("ᴍɪɴ " + computeUnit(component.min(), component.type())).withStyle(ChatFormatting.RED));
                    }

                    if (component.max() != null) {
                        finalComponent.append(divider)
                                .append(Component.literal("ᴍᴀx " + computeUnit(component.max(), component.type())).withStyle(ChatFormatting.GREEN));
                    }

                    tooltip.add(finalComponent);

                }


            tooltip.add(Component.empty());


            tooltip.add(Component.literal("§8Spell " + (spells.indexOf(entry) + 1) + "/" + spells.size()));
            tooltip.add(Component.literal("§8Press ")
                    .append(mc.options.keyShift.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY)
                    .append(" §8to view all Spells"));
        }
    }

    private static float computeUnit(float input, BrutalityTooltipHelper.SpellStatComponents type) {
        if (type.equals(DURATION)) {
            return input / 20;
        }
        if (type.unit.contains("❤")) {
            return input / 2;
        }
        return input;
    }

    private static @NotNull MutableComponent getMutableComponent(Player player, SpellStorage.SpellEntry entry, net.goo.brutality.util.helpers.BrutalityTooltipHelper.SpellStatComponent component) {
        BrutalityTooltipHelper.SpellStatComponents type = component.type();
        String operand = component.levelDelta() > 0 ? " + " : " - ";
        float base = computeUnit(component.base(), type);
        float levelDelta = computeUnit(Mth.abs(component.levelDelta()), type);
        float finalValue = computeUnit(entry.spell().getFinalStat(IBrutalitySpell.getActualSpellLevel(player, entry.spell(), entry.level()), component), type);
        boolean shouldReset = !(type.equals(DURATION) || type.equals(CHANCE));

        if (component.max() != null && component.min() != null) {
            finalValue = Mth.clamp(finalValue, computeUnit(component.min(), type), computeUnit(component.max(), type));
        } else if (component.max() != null) {
            finalValue = Math.min(finalValue, computeUnit(component.max(), type));
        } else if (component.min() != null) {
            finalValue = Math.max(finalValue, computeUnit(component.min(), type));
        }


        return Component.literal(type.icon +
                " §8|§6 " + base +
                (shouldReset ? "§r" : "") + type.unit
                + "§6" + operand + "(" + levelDelta +
                (shouldReset ? "§r" : "") + type.unit
                + "§6" + " * level) = §2" + finalValue +
                (shouldReset ? "§r" : "") + type.unit);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", state -> PlayState.STOP)
                .triggerableAnim("cast_instant", RawAnimation.begin().thenPlay("cast_instant"))
                .triggerableAnim("cast_channelling", RawAnimation.begin().thenPlayAndHold("cast_channelling"))
                .triggerableAnim("cast_continuous", RawAnimation.begin().thenPlayAndHold("cast_continuous")));
        controllers.add(new AnimationController<>(this, "book_controller", state -> PlayState.STOP)
                .triggerableAnim("close", RawAnimation.begin().thenPlayAndHold("close")));
    }
}