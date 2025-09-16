package net.goo.brutality.particle.base;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.goo.brutality.particle.providers.WaveParticleData;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.goo.brutality.util.ModUtils.ModEasings.easeOut;

public class WaveParticle extends FlatParticle {
    private final float maxRadius;
    private final SpriteSet sprites;

    public WaveParticle(ClientLevel level, double x, double y, double z, WaveParticleData<?> data, SpriteSet sprites) {
        super(level, x, y, z, data, sprites);
        this.maxRadius = data.radius();
        this.sprites = sprites;
        this.lifetime = data.growthDuration();
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
        float growthProgress = ((float) this.age + partialTicks) / this.lifetime;
        growthProgress = Mth.clamp(growthProgress, 0.0F, 1.0F);
        this.quadSize = maxRadius * easeOut(growthProgress);
        this.alpha = 1f - easeOut(growthProgress);
        super.render(buffer, camera, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

}