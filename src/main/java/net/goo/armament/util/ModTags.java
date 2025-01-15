package net.goo.armament.util;

import net.goo.armament.Armament;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> METAL_DETECTOR_VALUABLES = tag("metal_detector_valuables");
        public static final TagKey<Block> NEEDS_SAPPHIRE_TOOL = tag("needs_sapphire_tool");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Armament.MOD_ID, name));
        }
    }

    public static class Items {

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Armament.MOD_ID, name));
        }
    }
}
