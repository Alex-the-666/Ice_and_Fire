package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.ICustomStatueModel;
import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.model.ModelStonePlayer;
import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerHydraHead;
import com.github.alexthe666.iceandfire.entity.*;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderStoneStatue extends EntityRenderer<EntityStoneStatue> {

    private Map<String, EntityModel> modelMap = new HashMap();
    private Map<String, Entity> hollowEntityMap = new HashMap();
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{new ResourceLocation("textures/block/destroy_stage_0.png"), new ResourceLocation("textures/block/destroy_stage_1.png"), new ResourceLocation("textures/block/destroy_stage_2.png"), new ResourceLocation("textures/block/destroy_stage_3.png"), new ResourceLocation("textures/block/destroy_stage_4.png"), new ResourceLocation("textures/block/destroy_stage_5.png"), new ResourceLocation("textures/block/destroy_stage_6.png"), new ResourceLocation("textures/block/destroy_stage_7.png"), new ResourceLocation("textures/block/destroy_stage_8.png"), new ResourceLocation("textures/block/destroy_stage_9.png")};

    public RenderStoneStatue(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityStoneStatue entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    protected void preRenderCallback(EntityStoneStatue entity, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entity.getRenderScale() < 0.01F ? 1F : entity.getRenderScale();
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void render(EntityStoneStatue entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        EntityModel model = null;
        if(modelMap.get(entityIn.getTrappedEntityTypeString()) != null){
            model = modelMap.get(entityIn.getTrappedEntityTypeString());
        }else{
            EntityRenderer renderer = Minecraft.getInstance().getRenderManager().renderers.get(entityIn.getTrappedEntityType());
            if(renderer instanceof LivingRenderer){
                model = ((LivingRenderer) renderer).getEntityModel();
                modelMap.put(entityIn.getTrappedEntityTypeString(), model);
            }
        }
        if(model != null){
            Entity fakeEntity = null;
            if(this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString()) == null){
                Entity build = entityIn.getTrappedEntityType().create(Minecraft.getInstance().world);
                if(build != null){
                    try{
                        build.read(entityIn.getTrappedTag());
                    }catch (Exception e){
                        IceAndFire.LOGGER.warn("Mob " + entityIn.getTrappedEntityTypeString() + " could not build statue NBT");
                    }
                    fakeEntity = (Entity)this.hollowEntityMap.putIfAbsent(entityIn.getTrappedEntityTypeString(), build);
                }
            }else{
                fakeEntity = this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString());
            }
            float x = Math.max(model.textureWidth, 1) / 16F; //default to 4
            float y = Math.max(model.textureHeight, 1) / 16F; //default to 2
            RenderType tex = IafRenderType.getStoneMobRenderType(x, y);
            if(fakeEntity instanceof EntityTroll){
                tex = RenderType.getEntityCutout(((EntityTroll) fakeEntity).getTrollType().TEXTURE_STONE);
            }
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(tex);
            matrixStackIn.push();
            matrixStackIn.push();
            matrixStackIn.push();
            float yaw = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
            boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
            model.isChild = entityIn.isChild();
            model.isSitting = shouldSit;
            model.swingProgress = entityIn.getSwingProgress(partialTicks);
            if(model instanceof AdvancedEntityModel){
                ((AdvancedEntityModel)model).resetToDefaultPose();
            }else if(fakeEntity != null){
                model.setRotationAngles(fakeEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
            }
            preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0, 1.5F, 0);
            matrixStackIn.rotate(new Quaternion(Vector3f.XP, 180, true));
            matrixStackIn.rotate(new Quaternion(Vector3f.YP, yaw, true));
            if(model instanceof ICustomStatueModel && fakeEntity != null){
                ((ICustomStatueModel) model).renderStatue(matrixStackIn, ivertexbuilder, packedLightIn, fakeEntity);
                if(model instanceof ModelHydraBody && fakeEntity instanceof EntityHydra){
                    LayerHydraHead.renderHydraHeads((ModelHydraBody)model, true, matrixStackIn, bufferIn, packedLightIn, (EntityHydra) fakeEntity, 0, 0, partialTicks, 0, 0, 0);
                }
            }else{
                model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            matrixStackIn.pop();
            matrixStackIn.pop();
            matrixStackIn.pop();
            if(entityIn.getCrackAmount() >= 1){
                int i = MathHelper.clamp(entityIn.getCrackAmount() - 1, 0, DESTROY_STAGES.length - 1);
                RenderType crackTex = IafRenderType.getStoneCrackRenderType(DESTROY_STAGES[i], x, y);
                IVertexBuilder ivertexbuilder2 = bufferIn.getBuffer(crackTex);
                matrixStackIn.push();
                matrixStackIn.push();
                preRenderCallback(entityIn, matrixStackIn, partialTicks);
                matrixStackIn.translate(0, 1.5F, 0);
                matrixStackIn.rotate(new Quaternion(Vector3f.XP, 180, true));
                matrixStackIn.rotate(new Quaternion(Vector3f.YP, yaw, true));
                if(model instanceof ICustomStatueModel){
                    ((ICustomStatueModel) model).renderStatue(matrixStackIn, ivertexbuilder2, packedLightIn, fakeEntity);
                }else{
                    model.render(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }
                matrixStackIn.pop();
                matrixStackIn.pop();
            }
        }

    }
}