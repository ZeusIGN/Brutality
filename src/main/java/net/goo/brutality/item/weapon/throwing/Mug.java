package net.goo.brutality.item.weapon.throwing;

import net.goo.brutality.item.BrutalityCategories;
import net.goo.brutality.item.base.BrutalityThrowingItem;
import net.goo.brutality.registry.BrutalityModEntities;
import net.goo.brutality.registry.BrutalityModMobEffects;
import net.goo.brutality.util.ModUtils;
import net.goo.brutality.util.helpers.BrutalityTooltipHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class Mug extends BrutalityThrowingItem {

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(1).alwaysEat();
        if (ModUtils.getTextureIdx(stack) == 1) {
            builder.effect(() -> new MobEffectInstance(BrutalityModMobEffects.CAFFEINATED.get(), 600, 0), 1.0F);
        } else if (ModUtils.getTextureIdx(stack) == 2) {
            builder.effect(() -> new MobEffectInstance(BrutalityModMobEffects.CAFFEINATED.get(), 600, 1), 1.0F);
        }
        return builder.build();
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    public Mug(int pAttackDamageModifier, float pAttackSpeedModifier, Rarity rarity, List<BrutalityTooltipHelper.ItemDescriptionComponent> descriptionComponents, Block block) {
        super(pAttackDamageModifier, pAttackSpeedModifier, rarity, descriptionComponents);
        this.block = block;
    }

    @Override
    public ResourceLocation getAnimationResourceLocation() {
        return THROW_ANIMATION.DROP.getAnimationResource();
    }

    @Override
    public BrutalityCategories.AttackType getAttackType() {
        return BrutalityCategories.AttackType.BLUNT;
    }

    @Override
    public EntityType<? extends Projectile> getThrownEntity() {
        return BrutalityModEntities.MUG.get();
    }


    protected String[] types = new String[]{"", "_coffee", "_eeffoc"};

//    @Override
//    public String texture(@Nullable ItemStack stack) {
//        return "styrofoam_cup" + types[ModUtils.getTextureIdx(stack)];
//    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        return Component.translatable("item.brutality.mug" + types[ModUtils.getTextureIdx(pStack)]);
    }

    @Override
    public void handleThrowPacket(ItemStack stack, Player player) {
        EntityType<? extends Projectile> entityType = this.getThrownEntity();
        Level level = player.level();
        net.goo.brutality.entity.projectile.trident.physics_projectile.Mug mug =
                (net.goo.brutality.entity.projectile.trident.physics_projectile.Mug) entityType.create(level);

        if (mug != null) {
            mug.setPos(player.getEyePosition());
            mug.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, getThrowVelocity(player), getThrowInaccuracy());
            mug.setOwner(player);

            handleSealType(mug, stack);

            mug.setMugTypeIndex(ModUtils.getTextureIdx(stack));

            level.addFreshEntity(mug);
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (ModUtils.getTextureIdx(stack) != 0)
            return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
        return InteractionResultHolder.fail(stack);
    }

    public @NotNull ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player) pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, pStack);
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }
        ItemStack stack = new ItemStack(this);

        if (player == null || !player.getAbilities().instabuild) {
            if (pStack.isEmpty()) {
                return stack;
            }

            if (player != null) {
                player.getInventory().add(stack);
            }
        }

        pEntityLiving.gameEvent(GameEvent.DRINK);
        return pStack;
    }

    @Override
    public float getInitialThrowVelocity() {
        return 1F;
    }

    // region BlockItem class
    private final Block block;

    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        InteractionResult interactionresult = this.place(new BlockPlaceContext(pContext));
        if (!interactionresult.consumesAction() && this.isEdible()) {
            InteractionResult result = this.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        } else {
            return interactionresult;
        }
    }

    public InteractionResult place(BlockPlaceContext pContext) {
        if (!this.getBlock().isEnabled(pContext.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!pContext.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(pContext);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockplacecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    Level level = blockplacecontext.getLevel();
                    Player player = blockplacecontext.getPlayer();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.is(blockstate.getBlock())) {
                        blockstate1 = this.updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, pContext.getPlayer());
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, pContext.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                    if (player == null || !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }


    @Deprecated
    //Forge: Use more sensitive version {@link BlockItem#getPlaceSound(BlockState, IBlockReader, BlockPos, Entity) }
    protected SoundEvent getPlaceSound(BlockState pState) {
        return pState.getSoundType().getPlaceSound();
    }

    //Forge: Sensitive version of BlockItem#getPlaceSound
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
        return pContext;
    }

    protected void updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        BlockState blockstate = this.getBlock().getStateForPlacement(pContext);
        return blockstate != null && this.canPlace(pContext, blockstate) ? blockstate : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos pPos, Level pLevel, ItemStack pStack, BlockState pState) {
        BlockState blockstate = pState;
        CompoundTag compoundtag = pStack.getTag();
        if (compoundtag != null) {
            CompoundTag blockStateTag = compoundtag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> statedefinition = pState.getBlock().getStateDefinition();

            for (String s : blockStateTag.getAllKeys()) {
                Property<?> property = statedefinition.getProperty(s);
                if (property != null) {
                    String s1 = blockStateTag.get(s).getAsString();
                    blockstate = updateState(blockstate, property, s1);
                }
            }
        }

        if (blockstate != pState) {
            pLevel.setBlock(pPos, blockstate, 2);
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState pState, Property<T> pProperty, String pValueIdentifier) {
        return pProperty.getValue(pValueIdentifier).map((p_40592_) -> {
            return pState.setValue(pProperty, p_40592_);
        }).orElse(pState);
    }

    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        Player player = pContext.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || pState.canSurvive(pContext.getLevel(), pContext.getClickedPos())) && pContext.getLevel().isUnobstructed(pState, pContext.getClickedPos(), collisioncontext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 11);
    }

    public static void updateCustomBlockEntityTag(Level pLevel, @Nullable Player pPlayer, BlockPos pPos, ItemStack pStack) {
        MinecraftServer minecraftserver = pLevel.getServer();
        if (minecraftserver != null) {
            CompoundTag compoundtag = getBlockEntityData(pStack);
            if (compoundtag != null) {
                BlockEntity blockentity = pLevel.getBlockEntity(pPos);
                if (blockentity != null) {
                    if (!pLevel.isClientSide && blockentity.onlyOpCanSetNbt() && (pPlayer == null || !pPlayer.canUseGameMasterBlocks())) {
                        return;
                    }

                    CompoundTag saveWithoutMetadata = blockentity.saveWithoutMetadata();
                    CompoundTag copied = saveWithoutMetadata.copy();
                    saveWithoutMetadata.merge(compoundtag);
                    if (!saveWithoutMetadata.equals(copied)) {
                        blockentity.load(saveWithoutMetadata);
                        blockentity.setChanged();
                    }
                }
            }

        }
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public @NotNull String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    /**
     * Allows items to add custom lines of information to the mouseover description.
     */
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        this.getBlock().appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    public Block getBlock() {
        return this.getBlockRaw() == null ? null : ForgeRegistries.BLOCKS.getDelegateOrThrow(this.getBlockRaw()).get();
    }

    private Block getBlockRaw() {
        return this.block;
    }

    public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
        pBlockToItemMap.put(this.getBlock(), pItem);
    }

    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        blockToItemMap.remove(this.getBlock());
    }

    public boolean canFitInsideContainerItems() {
        return !(this.block instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity pItemEntity) {
        if (this.block instanceof ShulkerBoxBlock) {
            ItemStack itemstack = pItemEntity.getItem();
            CompoundTag compoundtag = getBlockEntityData(itemstack);
            if (compoundtag != null && compoundtag.contains("Items", 9)) {
                ListTag listtag = compoundtag.getList("Items", 10);
                ItemUtils.onContainerDestroyed(pItemEntity, listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of));
            }
        }

    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Nullable
    public static CompoundTag getBlockEntityData(ItemStack pStack) {
        return pStack.getTagElement("BlockEntityTag");
    }

    public static void setBlockEntityData(ItemStack pStack, BlockEntityType<?> pBlockEntityType, CompoundTag pBlockEntityData) {
        if (pBlockEntityData.isEmpty()) {
            pStack.removeTagKey("BlockEntityTag");
        } else {
            BlockEntity.addEntityType(pBlockEntityData, pBlockEntityType);
            pStack.addTagElement("BlockEntityTag", pBlockEntityData);
        }

    }

    public @NotNull FeatureFlagSet requiredFeatures() {
        return this.getBlock().requiredFeatures();
    }

    // endregion

}

