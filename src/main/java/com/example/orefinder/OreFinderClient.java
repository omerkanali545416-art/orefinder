package com.example.orefinder;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class OreFinderClient implements ClientModInitializer {

    private record Hit(int x, int y, int z, float r, float g, float b) {
    }

    public static boolean enabled = false;

    private static KeyBinding toggleKey;
    private static KeyBinding menuKey;
    private static volatile List<Hit> hits = new ArrayList<>();
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        OreConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.orefinder.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.orefinder"));
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.orefinder.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.orefinder"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                tickCounter = 0;
                if (!enabled) hits = new ArrayList<>();
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("Maden gösterici: " + (enabled ? "§aAÇIK" : "§cKAPALI")), true);
                }
                if (enabled && client.world != null && client.player != null) scan(client);
            }
            while (menuKey.wasPressed()) {
                client.setScreen(new OreScreen());
            }
            if (enabled && client.world != null && client.player != null && ++tickCounter % 20 == 0) {
                scan(client);
            }
        });

        WorldRenderEvents.LAST.register(OreFinderClient::render);
    }

    private static void scan(MinecraftClient mc) {
        List<Hit> found = new ArrayList<>();
        BlockPos c = mc.player.getBlockPos();
        int r = OreConfig.radius;
        int minY = Math.max(mc.world.getBottomY(), c.getY() - r);
        int maxY = Math.min(mc.world.getTopY() - 1, c.getY() + r);
        BlockPos.Mutable p = new BlockPos.Mutable();

        for (int x = c.getX() - r; x <= c.getX() + r; x++) {
            for (int z = c.getZ() - r; z <= c.getZ() + r; z++) {
                for (int y = minY; y <= maxY; y++) {
                    p.set(x, y, z);
                    Block b = mc.world.getBlockState(p).getBlock();
                    Ore o = Ore.BY_BLOCK.get(b);
                    if (o != null && OreConfig.enabled.contains(o)) {
                        found.add(new Hit(x, y, z, o.r, o.g, o.b));
                    }
                }
            }
        }
        hits = found;
    }

    private static void render(WorldRenderContext ctx) {
        List<Hit> list = hits;
        if (!enabled || list.isEmpty()) return;

        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;
        Vec3d cam = ctx.camera().getPos();

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(2.0f);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        for (Hit h : list) {
            WorldRenderer.drawBox(matrices, buffer,
                    h.x, h.y, h.z, h.x + 1, h.y + 1, h.z + 1,
                    h.r, h.g, h.b, 1.0f);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.pop();
    }
                }
