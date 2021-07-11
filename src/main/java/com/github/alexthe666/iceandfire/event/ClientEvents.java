package com.github.alexthe666.iceandfire.event;

import java.util.Random;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.gui.IceAndFireMainMenu;
import com.github.alexthe666.iceandfire.client.render.entity.RenderCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.SirenEntityProperties;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.Pathfinding;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    private static final ResourceLocation SIREN_SHADER = new ResourceLocation("iceandfire:shaders/post/siren.json");
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("textures/block/frosted_ice_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("textures/block/frosted_ice_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("textures/block/frosted_ice_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("textures/block/frosted_ice_3.png");
    private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation("iceandfire:textures/models/misc/chain_link.png");
    private Random rand = new Random();

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if(Pathfinding.isDebug()){
            Pathfinding.debugDraw(event.getPartialTicks(), event.getMatrixStack());
        }
    }

    private static ResourceLocation getIceTexture(int ticksFrozen) {
        if (ticksFrozen < 100) {
            if (ticksFrozen < 50) {
                if (ticksFrozen < 20) {
                    return TEXTURE_3;
                }
                return TEXTURE_2;
            }
            return TEXTURE_1;
        }
        return TEXTURE_0;
    }

    public static void renderMovingAABB(AxisAlignedBB boundingBox, MatrixStack stack) {
        Tessellator tessellator = Tessellator.getInstance();
        IVertexBuilder vertexbuffer = tessellator.getBuffer().getVertexBuilder();
        BufferBuilder buffer = tessellator.getBuffer();
        float f3 = 0;
        Matrix4f matrix4f = stack.getLast().getMatrix();
        float maxX = (float) boundingBox.maxX * 0.425F;
        float minX = (float) boundingBox.minX * 0.425F;
        float maxY = (float) boundingBox.maxY * 0.425F;
        float minY = (float) boundingBox.minY * 0.425F;
        float maxZ = (float) boundingBox.maxZ * 0.425F;
        float minZ = (float) boundingBox.minZ * 0.425F;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();

        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();

        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();

        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();

        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).tex(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).tex(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        tessellator.draw();
    }

    private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_, int packedLight) {
        p_229108_0_.pos(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).tex(p_229108_9_, p_229108_10_).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player.getRidingEntity() != null) {
            if (player.getRidingEntity() instanceof EntityDragonBase) {
                int currentView = IceAndFire.PROXY.getDragon3rdPersonView();
                EntityDragonBase dragon = (EntityDragonBase) player.getRidingEntity();
                float scale = ((EntityDragonBase) player.getRidingEntity()).getRenderSize() / 3;
                if (Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
                    if (currentView == 0) {
                    } else if (currentView == 1) {
                        event.getInfo().movePosition(-scale * 1.2F, 0F, 0);
                    } else if (currentView == 2) {
                        event.getInfo().movePosition(-scale * 3F, 0F, 0);
                    } else if (currentView == 3) {
                        event.getInfo().movePosition(-scale * 5F, 0F, 0);
                    }
                }
                if (Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.THIRD_PERSON_FRONT) {
                    if (currentView == 0) {
                    } else if (currentView == 1) {
                        event.getInfo().movePosition(-scale * 1.2F, 0F, 0);
                    } else if (currentView == 2) {
                        event.getInfo().movePosition(-scale * 3F, 0F, 0);
                    } else if (currentView == 3) {
                        event.getInfo().movePosition(-scale * 5F, 0F, 0);
                    }
                }

            }

        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (player.world.isRemote && IafKeybindRegistry.dragon_change_view.isKeyDown()) {
                int currentView = IceAndFire.PROXY.getDragon3rdPersonView();
                if (currentView + 1 > 3) {
                    currentView = 0;
                } else {
                    currentView++;
                }
                IceAndFire.PROXY.setDragon3rdPersonView(currentView);
            }

            SirenEntityProperties sirenProps = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), SirenEntityProperties.class);
            if (player.world.isRemote && sirenProps != null) {
                GameRenderer renderer = Minecraft.getInstance().gameRenderer;
                EntitySiren siren = sirenProps.getSiren(event.getEntityLiving().world);
                if (IafConfig.sirenShader && !sirenProps.isCharmed && renderer != null && renderer.getShaderGroup() != null && SIREN_SHADER.toString().equals(renderer.getShaderGroup().getShaderGroupName())) {
                    renderer.stopUseShader();
                }
                if (siren == null) {
                    sirenProps.isCharmed = false;

                    return;
                }
                if (sirenProps.isCharmed) {
                    if (player.world.isRemote && rand.nextInt(40) == 0) {
                        IceAndFire.PROXY.spawnParticle("siren_appearance", player.getPosX(), player.getPosY(), player.getPosZ(), siren.getHairColor(), 0, 0);
                    }

                    if (IafConfig.sirenShader && renderer.getShaderGroup() == null) {
                        renderer.loadShader(SIREN_SHADER);
                    }

                }

            }
        }
    }

    @SubscribeEvent
    public void onPreRenderLiving(RenderLivingEvent.Pre event) {
        if (event.getEntity().getRidingEntity() != null && event.getEntity().getRidingEntity() instanceof EntityDragonBase) {
            if (ClientProxy.currentDragonRiders.contains(event.getEntity().getUniqueID()) || event.getEntity() == Minecraft.getInstance().player && Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a()) {
                event.setCanceled(true);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight()));
            }
        }
        FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntity(), FrozenEntityProperties.class);
        if (frozenProps != null && frozenProps.isFrozen) {
            LivingEntity entity = event.getEntity();
            float sideExpand = -0.125F;
            float sideExpandY = 0.325F;
            AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(-entity.getWidth() / 2F - sideExpand, 0, -entity.getWidth() / 2F - sideExpand, entity.getWidth() / 2F + sideExpand, entity.getHeight() + sideExpandY, entity.getWidth() / 2F + sideExpand);
            event.getMatrixStack().push();
            event.getMatrixStack().push();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().getTextureManager().bindTexture(getIceTexture(frozenProps.ticksUntilUnfrozen));
            renderMovingAABB(axisalignedbb1, event.getMatrixStack());
            RenderSystem.disableDepthTest();
            event.getMatrixStack().pop();
            event.getMatrixStack().pop();
        }

    }

    @SubscribeEvent
    public void onPostRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        ChainEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, ChainEntityProperties.class);

        MiscEntityProperties miscProps = EntityPropertiesHandler.INSTANCE.getProperties(entity, MiscEntityProperties.class);
        if (miscProps != null && miscProps.glarers.size() > 0) {
            MatrixStack matrixStackIn = event.getMatrixStack();
            Entity entityIn = entity;
            for (Entity livingentity : miscProps.glarers) {
                float f = 1;
                float f1 = (float) entityIn.world.getGameTime() + event.getPartialRenderTick();
                float f2 = f1 * 0.5F % 1.0F;
                float f3 = entityIn.getEyeHeight();
                matrixStackIn.push();
                matrixStackIn.translate(0.0D, f3, 0.0D);
                Vector3d Vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, event.getPartialRenderTick());
                Vector3d Vector3d1 = this.getPosition(entityIn, f3, event.getPartialRenderTick());
                Vector3d Vector3d2 = Vector3d.subtract(Vector3d1);
                float f4 = (float) (Vector3d2.length() + 1.0D);
                Vector3d2 = Vector3d2.normalize();
                float f5 = (float) Math.acos(Vector3d2.y);
                float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
                int i = 1;
                float f7 = f1 * 0.05F * -1.5F;
                float f8 = f * f;
                int j = 64 + (int) (f8 * 191.0F);
                int k = 32 + (int) (f8 * 191.0F);
                int l = 128 - (int) (f8 * 64.0F);
                float f9 = 0.2F;
                float f10 = 0.282F;
                float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
                float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
                float f13 = MathHelper.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
                float f14 = MathHelper.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
                float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
                float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
                float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
                float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
                float f19 = MathHelper.cos(f7 + (float) Math.PI) * 0.2F;
                float f20 = MathHelper.sin(f7 + (float) Math.PI) * 0.2F;
                float f21 = MathHelper.cos(f7 + 0.0F) * 0.2F;
                float f22 = MathHelper.sin(f7 + 0.0F) * 0.2F;
                float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
                float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
                float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                float f27 = 0.0F;
                float f28 = 0.4999F;
                float f29 = -1.0F + f2;
                float f30 = f4 * 2.5F + f29;
                IVertexBuilder ivertexbuilder = event.getBuffers().getBuffer(RenderCockatrice.TEXTURE_BEAM);
                MatrixStack.Entry matrixstack$entry = event.getMatrixStack().getLast();
                Matrix4f matrix4f = matrixstack$entry.getMatrix();
                Matrix3f matrix3f = matrixstack$entry.getNormal();
                int light = 15728880;
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30, light);
                float f31 = 0.0F;
                if (entityIn.ticksExisted % 2 == 0) {
                    f31 = 0.5F;
                }

                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31, light);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31, light);
                matrixStackIn.pop();
            }
        }

        if (properties != null) {
            if (!properties.connectedEntities.isEmpty()) {
                try {
                    for (Entity livingentity : properties.connectedEntities) {
                        if (!properties.alreadyIgnoresCamera) {
                            entity.ignoreFrustumCheck = true;
                        }
                        MatrixStack matrixStackIn = event.getMatrixStack();
                        float f = 1;
                        float f1 = 0;
                        float f2 = 1;
                        float f3 = entity.getEyeHeight();
                        matrixStackIn.push();
                        matrixStackIn.translate(0.0D, f3, 0.0D);
                        Vector3d Vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, event.getPartialRenderTick());
                        Vector3d Vector3d1 = this.getPosition(entity, f3, event.getPartialRenderTick());
                        Vector3d Vector3d2 = Vector3d.subtract(Vector3d1);
                        float f4 = (float) (Vector3d2.length() + 0.0D);
                        Vector3d2 = Vector3d2.normalize();
                        float f5 = (float) Math.acos(Vector3d2.y);
                        float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
                        int i = 1;
                        float f7 = -1.0F;
                        float f8 = f * f;
                        int j = 255;
                        int k = 255;
                        int l = 255;
                        float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
                        float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
                        float f13 = MathHelper.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
                        float f14 = MathHelper.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
                        float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
                        float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
                        float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
                        float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
                        float f19 = 0;
                        float f20 = 0.2F;
                        float f21 = 0F;
                        float f22 = -0.2F;
                        float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
                        float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
                        float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                        float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                        float f27 = 0.0F;
                        float f28 = 0.4999F;
                        float f29 = 0;
                        float f30 = f4 + f29;
                        float f32 = 0.75F;
                        float f31 = f4 + f32;

                        IVertexBuilder ivertexbuilder = event.getBuffers().getBuffer(RenderType.getEntityCutoutNoCull(CHAIN_TEXTURE));
                        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
                        Matrix4f matrix4f = matrixstack$entry.getMatrix();
                        Matrix3f matrix3f = matrixstack$entry.getNormal();
                        matrixStackIn.push();
                        int light = event.getLight();
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30, light);

                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f31, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f32, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f32, light);
                        func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f31, light);
                        matrixStackIn.pop();
                        matrixStackIn.pop();
                    }
                }catch (Exception e){
                    IceAndFire.LOGGER.warn("Could not render chain link");
                }
            } else {
                if (!properties.alreadyIgnoresCamera && entity.ignoreFrustumCheck) {
                    entity.ignoreFrustumCheck = false;
                }
            }
        }
    }

    private Vector3d getPosition(Entity LivingEntityIn, double p_177110_2_, float p_177110_4_) {
        double d0 = LivingEntityIn.lastTickPosX + (LivingEntityIn.getPosX() - LivingEntityIn.lastTickPosX) * (double) p_177110_4_;
        double d1 = p_177110_2_ + LivingEntityIn.lastTickPosY + (LivingEntityIn.getPosY() - LivingEntityIn.lastTickPosY) * (double) p_177110_4_;
        double d2 = LivingEntityIn.lastTickPosZ + (LivingEntityIn.getPosZ() - LivingEntityIn.lastTickPosZ) * (double) p_177110_4_;
        return new Vector3d(d0, d1, d2);
    }


    private double acos(double x) {
        return (-0.69813170079773212 * x * x - 0.87266462599716477) * x + 1.5707963267948966;
    }

    private Vector3d getChainPosition(Entity LivingEntityIn, double p_177110_2_, float p_177110_4_) {
        double d0 = LivingEntityIn.lastTickPosX + (LivingEntityIn.getPosX() - LivingEntityIn.lastTickPosX) * (double) p_177110_4_;
        double d1 = p_177110_2_ + LivingEntityIn.lastTickPosY + (LivingEntityIn.getPosY() - LivingEntityIn.lastTickPosY) * (double) p_177110_4_;
        double d2 = LivingEntityIn.lastTickPosZ + (LivingEntityIn.getPosZ() - LivingEntityIn.lastTickPosZ) * (double) p_177110_4_;
        return new Vector3d(d0, d1, d2);
    }

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        if (IafConfig.customMainMenu && event.getGui() instanceof MainMenuScreen && !(event.getGui() instanceof IceAndFireMainMenu)) {
            event.setGui(new IceAndFireMainMenu());
        }
    }

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event) {
        if (IafConfig.dragonAuto3rdPerson) {
            if (event.getEntityBeingMounted() instanceof EntityDragonBase && event.getWorldObj().isRemote && event.getEntityMounting() == Minecraft.getInstance().player) {
                EntityDragonBase dragon = (EntityDragonBase) event.getEntityBeingMounted();
                if (dragon.isTamed() && dragon.isOwner(Minecraft.getInstance().player)) {
                    if (event.isDismounting()) {
                        Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.values()[IceAndFire.PROXY.getPreviousViewType()]);
                    } else {
                        IceAndFire.PROXY.setPreviousViewType(Minecraft.getInstance().gameSettings.getPointOfView().ordinal());
                        Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.values()[1]);
                        IceAndFire.PROXY.setDragon3rdPersonView(2);
                    }
                }
            }
        }
    }
}