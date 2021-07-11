package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGhost;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ParticleGhostAppearance extends Particle {
    private final ModelGhost model = new ModelGhost();
    private int ghost;
    private boolean fromLeft = false;
    public ParticleGhostAppearance(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, int ghost) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.particleGravity = 0.0F;
        this.maxAge = 15;
        this.ghost = ghost;
        fromLeft = worldIn.rand.nextBoolean();
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        float f = ((float) this.age + partialTicks) / (float) this.maxAge;
        float f1 = 0.05F + 0.5F * MathHelper.sin(f * (float) Math.PI);
        Entity entity = world.getEntityByID(ghost);
        if(entity instanceof EntityGhost && Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON){
            EntityGhost ghostEntity = (EntityGhost)entity;
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.rotate(renderInfo.getRotation());
            if(fromLeft){
                matrixstack.rotate(Vector3f.YN.rotationDegrees(150.0F * f - 60.0F));
                matrixstack.rotate(Vector3f.ZN.rotationDegrees(150.0F * f - 60.0F));

            }else{
                matrixstack.rotate(Vector3f.YP.rotationDegrees(150.0F * f - 60.0F));
                matrixstack.rotate(Vector3f.ZP.rotationDegrees(150.0F * f - 60.0F));

            }
            matrixstack.scale(-1.0F, -1.0F, 1.0F);
            matrixstack.translate(0.0D, 0.3F, 1.25D);
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

            IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(IafRenderType.getGhost(RenderGhost.getGhostOverlayForType(ghostEntity.getColor())));
            this.model.setRotationAngles(ghostEntity, 0, 0, entity.ticksExisted + partialTicks, 0, 0);
            this.model.render(matrixstack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f1);
            irendertypebuffer$impl.finish();
        }

    }
}

