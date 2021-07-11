package com.github.alexthe666.iceandfire.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ParticleSirenMusic extends SpriteTexturedParticle {
    private static final ResourceLocation SIREN_MUSIC = new ResourceLocation("iceandfire:textures/particles/siren_music.png");

    float noteParticleScale;
    float colorScale;

    public ParticleSirenMusic(ClientWorld world, double x, double y, double z, double motX, double motY, double motZ, float size) {
        super(world, x, y, z, motX, motY, motZ);
        this.setPosition(x, y, z);
        this.colorScale = (float) 1;
        this.particleRed = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.particleGreen = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.particleBlue = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d inerp = renderInfo.getProjectedView();
        if (age > this.getMaxAge()) {
            this.setExpired();
        }

        Vector3d Vector3d = renderInfo.getProjectedView();
        float f = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - Vector3d.getX());
        float f1 = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - Vector3d.getY());
        float f2 = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - Vector3d.getZ());
        Quaternion quaternion;
        if (this.particleAngle == 0.0F) {
            quaternion = renderInfo.getRotation();
        } else {
            quaternion = new Quaternion(renderInfo.getRotation());
            float f3 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
            quaternion.multiply(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getScale(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        Minecraft.getInstance().getTextureManager().bindTexture(SIREN_MUSIC);
        int j = this.getBrightnessForRender(partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        vertexbuffer.pos(avector3f[0].getX(), avector3f[0].getY(), avector3f[0].getZ()).tex(f8, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        vertexbuffer.pos(avector3f[1].getX(), avector3f[1].getY(), avector3f[1].getZ()).tex(f8, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        vertexbuffer.pos(avector3f[2].getX(), avector3f[2].getY(), avector3f[2].getZ()).tex(f7, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        vertexbuffer.pos(avector3f[3].getX(), avector3f[3].getY(), avector3f[3].getZ()).tex(f7, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        Tessellator.getInstance().draw();
    }

    public void tick() {
        super.tick();
        colorScale += 0.015;
        if (colorScale > 25) {
            colorScale = 0;
        }
        this.particleRed = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.particleGreen = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.particleBlue = Math.max(0.0F, MathHelper.sin(((float)colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);

    }

    public int getBrightnessForRender(float partialTick) {
        return super.getBrightnessForRender(partialTick);
    }

    public int getFXLayer() {
        return 3;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

}