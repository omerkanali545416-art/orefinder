package com.example.orefinder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public enum Ore {
    COAL("Kömür", 0x9A9A9A, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE),
    IRON("Demir", 0xD8AF93, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE),
    COPPER("Bakır", 0xE77C56, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE),
    GOLD("Altın", 0xFCEE4B, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE),
    REDSTONE("Redstone", 0xFF2020, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE),
    LAPIS("Lapis", 0x2F5BFF, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE),
    DIAMOND("Elmas", 0x4AEDD9, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE),
    EMERALD("Zümrüt", 0x17DD62, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE),
    QUARTZ("Kuvars", 0xEEEEEE, Blocks.NETHER_QUARTZ_ORE),
    DEBRIS("Antik Kalıntı", 0xB5653A, Blocks.ANCIENT_DEBRIS);

    public final String label;
    public final float r, g, b;
    public final Block[] blocks;

    public static final Map<Block, Ore> BY_BLOCK = new HashMap<>();

    static {
        for (Ore o : values()) {
            for (Block bl : o.blocks) {
                BY_BLOCK.put(bl, o);
            }
        }
    }

    Ore(String label, int rgb, Block... blocks) {
        this.label = label;
        this.r = ((rgb >> 16) & 0xFF) / 255f;
        this.g = ((rgb >> 8) & 0xFF) / 255f;
        this.b = (rgb & 0xFF) / 255f;
        this.blocks = blocks;
    }
  }
