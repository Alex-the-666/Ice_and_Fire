package com.github.alexthe666.iceandfire.client.particle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;

/*
    Lightning bolt effect code used with permission from aidancbrady
 */
public class LightningBoltData {

    private final Random random = new Random();

    private final BoltRenderInfo renderInfo;

    private final Vector3d start;
    private final Vector3d end;

    private final int segments;

    private int count = 1;
    private float size = 0.1F;
    private int lifespan = 30;

    private SpawnFunction spawnFunction = SpawnFunction.delay(60);
    private FadeFunction fadeFunction = FadeFunction.fade(0.5F);

    public LightningBoltData(Vector3d start, Vector3d end) {
        this(BoltRenderInfo.DEFAULT, start, end, (int) (Math.sqrt(start.distanceTo(end) * 100)));
    }

    public LightningBoltData(BoltRenderInfo info, Vector3d start, Vector3d end, int segments) {
        this.renderInfo = info;
        this.start = start;
        this.end = end;
        this.segments = segments;
    }

    public LightningBoltData count(int count) {
        this.count = count;
        return this;
    }

    public LightningBoltData size(float size) {
        this.size = size;
        return this;
    }

    public LightningBoltData spawn(SpawnFunction spawnFunction) {
        this.spawnFunction = spawnFunction;
        return this;
    }

    public LightningBoltData fade(FadeFunction fadeFunction) {
        this.fadeFunction = fadeFunction;
        return this;
    }

    public LightningBoltData lifespan(int lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    public int getLifespan() {
        return lifespan;
    }

    public SpawnFunction getSpawnFunction() {
        return spawnFunction;
    }

    public FadeFunction getFadeFunction() {
        return fadeFunction;
    }

    public Vector4f getColor() {
        return renderInfo.color;
    }

    public List<BoltQuads> generate() {
        List<BoltQuads> quads = new ArrayList<>();
        Vector3d diff = end.subtract(start);
        float totalDistance = (float) diff.length();
        for (int i = 0; i < count; i++) {
            LinkedList<BoltInstructions> drawQueue = new LinkedList<>();
            drawQueue.add(new BoltInstructions(start, 0, new Vector3d(0, 0, 0), null, false));
            while (!drawQueue.isEmpty()) {
                BoltInstructions data = drawQueue.poll();
                Vector3d perpendicularDist = data.perpendicularDist;
                float progress = data.progress + (1F / segments) * (1 - renderInfo.parallelNoise + random.nextFloat() * renderInfo.parallelNoise * 2);
                Vector3d segmentEnd;
                if (progress >= 1) {
                    segmentEnd = end;
                } else {
                    float segmentDiffScale = renderInfo.spreadFunction.getMaxSpread(progress);
                    float maxDiff = renderInfo.spreadFactor * segmentDiffScale * totalDistance * renderInfo.randomFunction.getRandom(random);
                    Vector3d randVec = findRandomOrthogonalVector(diff, random);
                    perpendicularDist = renderInfo.segmentSpreader.getSegmentAdd(perpendicularDist, randVec, maxDiff, segmentDiffScale, progress);
                    // new vector is original + current progress through segments + perpendicular change
                    segmentEnd = start.add(diff.scale(progress)).add(perpendicularDist);
                }
                float boltSize = size * (0.5F + (1 - progress) * 0.5F);
                Pair<BoltQuads, QuadCache> quadData = createQuads(data.cache, data.start, segmentEnd, boltSize);
                quads.add(quadData.getLeft());

                if (segmentEnd == end) {
                    break; // break if we've reached the defined end point
                } else if (!data.isBranch) {
                    // continue the bolt if this is the primary (non-branch) segment
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), false));
                } else if (random.nextFloat() < renderInfo.branchContinuationFactor) {
                    // branch continuation
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), true));
                }

                while (random.nextFloat() < renderInfo.branchInitiationFactor * (1 - progress)) {
                    // branch initiation (probability decreases as progress increases)
                    drawQueue.add(new BoltInstructions(segmentEnd, progress, perpendicularDist, quadData.getRight(), true));
                }
            }
        }
        return quads;
    }

    private static Vector3d findRandomOrthogonalVector(Vector3d vec, Random rand) {
        Vector3d newVec = new Vector3d(-0.5 + rand.nextDouble(), -0.5 + rand.nextDouble(), -0.5 + rand.nextDouble());
        return vec.crossProduct(newVec).normalize();
    }

    private Pair<BoltQuads, QuadCache> createQuads(QuadCache cache, Vector3d startPos, Vector3d end, float size) {
        Vector3d diff = end.subtract(startPos);
        Vector3d rightAdd = diff.crossProduct(new Vector3d(0.5, 0.5, 0.5)).normalize().scale(size);
        Vector3d backAdd = diff.crossProduct(rightAdd).normalize().scale(size), rightAddSplit = rightAdd.scale(0.5F);

        Vector3d start = cache != null ? cache.prevEnd : startPos;
        Vector3d startRight = cache != null ? cache.prevEndRight : start.add(rightAdd);
        Vector3d startBack = cache != null ? cache.prevEndBack : start.add(rightAddSplit).add(backAdd);
        Vector3d endRight = end.add(rightAdd), endBack = end.add(rightAddSplit).add(backAdd);

        BoltQuads quads = new BoltQuads();
        quads.addQuad(start, end, endRight, startRight);
        quads.addQuad(startRight, endRight, end, start);

        quads.addQuad(startRight, endRight, endBack, startBack);
        quads.addQuad(startBack, endBack, endRight, startRight);

        return Pair.of(quads, new QuadCache(end, endRight, endBack));
    }

    private static class QuadCache {

        private final Vector3d prevEnd, prevEndRight, prevEndBack;

        private QuadCache(Vector3d prevEnd, Vector3d prevEndRight, Vector3d prevEndBack) {
            this.prevEnd = prevEnd;
            this.prevEndRight = prevEndRight;
            this.prevEndBack = prevEndBack;
        }
    }

    protected static class BoltInstructions {

        private final Vector3d start;
        private final Vector3d perpendicularDist;
        private final QuadCache cache;
        private final float progress;
        private final boolean isBranch;

        private BoltInstructions(Vector3d start, float progress, Vector3d perpendicularDist, QuadCache cache, boolean isBranch) {
            this.start = start;
            this.perpendicularDist = perpendicularDist;
            this.progress = progress;
            this.cache = cache;
            this.isBranch = isBranch;
        }
    }

    public class BoltQuads {

        private final List<Vector3d> vecs = new ArrayList<>();

        protected void addQuad(Vector3d... quadVecs) {
            vecs.addAll(Arrays.asList(quadVecs));
        }

        public List<Vector3d> getVecs() {
            return vecs;
        }
    }

    public interface SpreadFunction {

        /** A steady linear increase in perpendicular noise. */
        SpreadFunction LINEAR_ASCENT = (progress) -> progress;
        /** A steady linear increase in perpendicular noise, followed by a steady decrease after the halfway point. */
        SpreadFunction LINEAR_ASCENT_DESCENT = (progress) -> (progress - Math.max(0, 2 * progress - 1)) / 0.5F;
        /** Represents a unit sine wave from 0 to PI, scaled by progress. */
        SpreadFunction SINE = (progress) -> (float) Math.sin(Math.PI * progress);

        float getMaxSpread(float progress);
    }

    public interface RandomFunction {

        RandomFunction UNIFORM = Random::nextFloat;
        RandomFunction GAUSSIAN = rand -> (float) rand.nextGaussian();

        float getRandom(Random rand);
    }

    public interface SegmentSpreader {

        /** Don't remember where the last segment left off, just randomly move from the straight-line vector. */
        SegmentSpreader NO_MEMORY = (perpendicularDist, randVec, maxDiff, scale, progress) -> randVec.scale(maxDiff);

        /** Move from where the previous segment ended by a certain memory factor. Higher memory will restrict perpendicular movement. */
        static SegmentSpreader memory(float memoryFactor) {
            return (perpendicularDist, randVec, maxDiff, spreadScale, progress) -> {
                float nextDiff = maxDiff * (1 - memoryFactor);
                Vector3d cur = randVec.scale(nextDiff);
                if (progress > 0.5F) {
                    // begin to come back to the center after we pass halfway mark
                    cur = cur.add(perpendicularDist.scale(-1 * (1 - spreadScale)));
                }
                return perpendicularDist.add(cur);
            };
        }

        Vector3d getSegmentAdd(Vector3d perpendicularDist, Vector3d randVec, float maxDiff, float scale, float progress);
    }

    public interface SpawnFunction {

        /** Allow for bolts to be spawned each update call without any delay. */
        SpawnFunction NO_DELAY = (rand) -> Pair.of(0F, 0F);
        /** Will re-spawn a bolt each time one expires. */
        SpawnFunction CONSECUTIVE = new SpawnFunction() {
            @Override
            public Pair<Float, Float> getSpawnDelayBounds(Random rand) {
                return Pair.of(0F, 0F);
            }
            @Override
            public boolean isConsecutive() {
                return true;
            }
        };

        /** Spawn bolts with a specified constant delay. */
        static SpawnFunction delay(float delay) {
            return (rand) -> Pair.of(delay, delay);
        }

        /**
         * Spawns bolts with a specified delay and specified noise value, which will be randomly applied at either end of the delay bounds.
         */
        static SpawnFunction noise(float delay, float noise) {
            return (rand) -> Pair.of(delay - noise, delay + noise);
        }

        Pair<Float, Float> getSpawnDelayBounds(Random rand);

        default float getSpawnDelay(Random rand) {
            Pair<Float, Float> bounds = getSpawnDelayBounds(rand);
            return bounds.getLeft() + (bounds.getRight() - bounds.getLeft()) * rand.nextFloat();
        }

        default boolean isConsecutive() {
            return false;
        }
    }

    public interface FadeFunction {

        /** No fade; render the bolts entirely throughout their lifespan. */
        FadeFunction NONE = (totalBolts, lifeScale) -> Pair.of(0, totalBolts);

        /** Remder bolts with a segment-by-segment 'fade' in and out, with a specified fade duration (applied to start and finish). */
        static FadeFunction fade(float fade) {
            return (totalBolts, lifeScale) -> {
                int start = lifeScale > (1 - fade) ? (int) (totalBolts * (lifeScale - (1 - fade)) / fade) : 0;
                int end = lifeScale < fade ? (int) (totalBolts * (lifeScale / fade)) : totalBolts;
                return Pair.of(start, end);
            };
        }

        Pair<Integer, Integer> getRenderBounds(int totalBolts, float lifeScale);
    }

    public static class BoltRenderInfo {

        public static final BoltRenderInfo DEFAULT = new BoltRenderInfo();
        public static final BoltRenderInfo ELECTRICITY = electricity();

        /** How much variance is allowed in segment lengths (parallel to straight line). */
        private float parallelNoise = 0.1F;
        /** How much variance is allowed perpendicular to the straight line vector. Scaled by distance and spread function. */
        private float spreadFactor = 0.1F;

        /** The chance of creating an additional branch after a certain segment. */
        private float branchInitiationFactor = 0.0F;
        /** The chance of a branch continuing (post-initiation). */
        private float branchContinuationFactor = 0.0F;

        private Vector4f color = new Vector4f(0.45F, 0.45F, 0.5F, 0.8F);

        private RandomFunction randomFunction = RandomFunction.GAUSSIAN;
        private SpreadFunction spreadFunction = SpreadFunction.SINE;
        private SegmentSpreader segmentSpreader = SegmentSpreader.NO_MEMORY;

        public static BoltRenderInfo electricity() {
            return new BoltRenderInfo(0.5F, 0.25F, 0.25F, 0.15F, new Vector4f(0.70F, 0.45F, 0.89F, 0.8F), 0.8F);
        }

        public BoltRenderInfo(){}

        public BoltRenderInfo(float parallelNoise, float spreadFactor, float branchInitiationFactor, float branchContinuationFactor, Vector4f color, float closeness) {
            this.parallelNoise = parallelNoise;
            this.spreadFactor = spreadFactor;
            this.branchInitiationFactor = branchInitiationFactor;
            this.branchContinuationFactor = branchContinuationFactor;
            this.color = color;
            this.segmentSpreader = SegmentSpreader.memory(closeness);
        }
    }
}