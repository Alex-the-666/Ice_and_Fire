package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs.AbstractPathJob;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import org.lwjgl.opengl.GL11;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.*;

import static com.github.alexthe666.iceandfire.pathfinding.raycoms.PathfindingConstants.debugNodeMonitor;

/**
 * Static class the handles all the Pathfinding.
 */
public final class Pathfinding {
    private static final Set<Class<?>> loadedJobs = new CopyOnWriteArraySet<>();
    private static final BlockingQueue<Runnable> jobQueue = new LinkedBlockingDeque<>();
    private static ThreadPoolExecutor executor;

    private Pathfinding() {
        //Hides default constructor.
    }

    public static boolean isDebug(){
        return false;
    }
    /**
     * Creates a new thread pool for pathfinding jobs
     *
     * @return the threadpool executor.
     */
    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(1, IafConfig.dragonPathfindingThreads, 10, TimeUnit.SECONDS, jobQueue, new IafThreadFactory());
        }
        return executor;
    }

    /**
     * Stops all running threads in this thread pool
     */
    public static void shutdown() {
        getExecutor().shutdownNow();
        jobQueue.clear();
        executor = null;
    }

    /**
     * Add a job to the queue for processing.
     *
     * @param job PathJob
     * @return a Future containing the Path
     */
    public static Future<Path> enqueue(final AbstractPathJob job) {
        if (!loadedJobs.contains(job.getClass())) {
            ThreadTaskExecutor<?> workqueue = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
            CompletableFuture<Path> result = workqueue.isOnExecutionThread() ? CompletableFuture.completedFuture(job.call()) : CompletableFuture.supplyAsync(job::call, workqueue);
            return result.thenApply(path -> {
                loadedJobs.add(job.getClass());
                return path;
            });
        }
        if(getExecutor().isShutdown() || getExecutor().isTerminating() || getExecutor().isTerminated()){
            return null;
        }
        return getExecutor().submit(job);
    }

    /**
     * Render debugging information for the pathfinding system.
     *
     * @param frame       entity movement weight.
     * @param matrixStack the matrix stack to apply to.
     */
    @OnlyIn(Dist.CLIENT)
    public static void debugDraw(final double frame, final MatrixStack matrixStack) {
        if (AbstractPathJob.lastDebugNodesNotVisited == null) {
            return;
        }

        final Vector3d vec = Minecraft.getInstance().getRenderManager().info.getProjectedView();
        final double dx = vec.getX();
        final double dy = vec.getY();
        final double dz = vec.getZ();

        RenderSystem.pushTextureAttributes();

        matrixStack.push();
        matrixStack.translate(-dx, -dy, -dz);

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableLighting();

        final Set<Node> debugNodesNotVisited;
        final Set<Node> debugNodesVisited;
        final Set<Node> debugNodesPath;

        synchronized (debugNodeMonitor) {
            debugNodesNotVisited = AbstractPathJob.lastDebugNodesNotVisited;
            debugNodesVisited = AbstractPathJob.lastDebugNodesVisited;
            debugNodesPath = AbstractPathJob.lastDebugNodesPath;
        }

        try {
            for (final Node n : debugNodesNotVisited) {
                debugDrawNode(n, 1.0F, 0F, 0F, matrixStack);
            }

            for (final Node n : debugNodesVisited) {
               debugDrawNode(n, 0F, 0F, 1.0F, matrixStack);
            }

            for (final Node n : debugNodesPath) {
                if (n.isReachedByWorker()) {
                    debugDrawNode(n, 1F, 0.4F, 0F, matrixStack);
                } else {
                    debugDrawNode(n, 0F, 1.0F, 0F, matrixStack);
                }
            }
        } catch (final ConcurrentModificationException exc) {
            IceAndFire.LOGGER.catching(exc);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.popAttributes();
        matrixStack.pop();
    }

    @OnlyIn(Dist.CLIENT)
    private static void debugDrawNode(final Node n, final float r, final float g, final float b, final MatrixStack matrixStack) {
        matrixStack.push();
        matrixStack.translate((double) n.pos.getX() + 0.375, (double) n.pos.getY() + 0.375, (double) n.pos.getZ() + 0.375);

        final Entity entity = Minecraft.getInstance().getRenderViewEntity();
        final double dx = n.pos.getX() - entity.getPosX();
        final double dy = n.pos.getY() - entity.getPosY();
        final double dz = n.pos.getZ() - entity.getPosZ();
        if (Math.sqrt(dx * dx + dy * dy + dz * dz) <= 5D) {
            renderDebugText(n, matrixStack);
        }

        matrixStack.scale(0.25F, 0.25F, 0.25F);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuffer();

        final Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        RenderSystem.color3f(r, g, b);

        //  X+
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        //  X-
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();

        //  Z-
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();

        //  Z+
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();

        //  Y+
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();

        //  Y-
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.pos(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.pos(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        tessellator.draw();

        if (n.parent != null) {
            final float pdx = n.parent.pos.getX() - n.pos.getX() + 0.125f;
            final float pdy = n.parent.pos.getY() - n.pos.getY() + 0.125f;
            final float pdz = n.parent.pos.getZ() - n.pos.getZ() + 0.125f;
            vertexBuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            vertexBuffer.pos(matrix4f, 0.5f, 0.5f, 0.5f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            vertexBuffer.pos(matrix4f, pdx / 0.25f, pdy / 0.25f, pdz / 0.25f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.draw();
        }

        matrixStack.pop();
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderDebugText(final Node n, final MatrixStack matrixStack) {
        final String s1 = String.format("F: %.3f [%d]", n.getCost(), n.getCounterAdded());
        final String s2 = String.format("G: %.3f [%d]", n.getScore(), n.getCounterVisited());
        final FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;

        matrixStack.push();
        matrixStack.translate(0.0F, 0.75F, 0.0F);
        RenderSystem.normal3f(0.0F, 1.0F, 0.0F);

        final EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        matrixStack.rotate(renderManager.getCameraOrientation());
        matrixStack.scale(-0.014F, -0.014F, 0.014F);
        matrixStack.translate(0.0F, 18F, 0.0F);

        RenderSystem.depthMask(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        RenderSystem.disableTexture();

        final int i = Math.max(fontrenderer.getStringWidth(s1), fontrenderer.getStringWidth(s2)) / 2;

        final Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(matrix4f, (-i - 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.pos(matrix4f, (-i - 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.pos(matrix4f, (i + 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.pos(matrix4f, (i + 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        tessellator.draw();

        RenderSystem.enableTexture();

        final IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        matrixStack.translate(0.0F, -5F, 0.0F);
        fontrenderer.renderString(s1, -fontrenderer.getStringWidth(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.renderString(s2, -fontrenderer.getStringWidth(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);

        RenderSystem.depthMask(true);
        matrixStack.translate(0.0F, -8F, 0.0F);
        fontrenderer.renderString(s1, -fontrenderer.getStringWidth(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.renderString(s2, -fontrenderer.getStringWidth(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        buffer.finish();

        matrixStack.pop();
    }

    /**
     * Ice and Fire specific thread factory.
     */
    public static class IafThreadFactory implements ThreadFactory {
        /**
         * Ongoing thread IDs.
         */
        public static int id;


        private static final ClassLoader classLoader;

        static {
            ThreadTaskExecutor<?> workqueue = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
            if (workqueue.isOnExecutionThread())
                classLoader = Thread.currentThread().getContextClassLoader();
            else
                classLoader = CompletableFuture.supplyAsync(() -> Thread.currentThread().getContextClassLoader(), workqueue).join();
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(runnable, "Ice and Fire Pathfinding Worker #" + (id++));
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            if (thread.getContextClassLoader() != classLoader) {
                IceAndFire.LOGGER.info("Corrected CCL of new Ice and Fire Pathfinding Thread, was: " + thread.getContextClassLoader().toString());
                thread.setContextClassLoader(classLoader);
            }
            thread.setUncaughtExceptionHandler((thread1, throwable) -> IceAndFire.LOGGER.error("Ice and Fire Pathfinding Thread errored! ", throwable));
            return thread;
        }
    }
}
