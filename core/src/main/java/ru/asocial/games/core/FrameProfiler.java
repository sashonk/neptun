package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;

/**
 * Rolling frame-time profiler for render loop sections.
 */
public class FrameProfiler {

    private static final int SAMPLE_COUNT = 60;

    private final long[] actSamples = new long[SAMPLE_COUNT];
    private final long[] mapSamples = new long[SAMPLE_COUNT];
    private final long[] drawSamples = new long[SAMPLE_COUNT];
    private final long[] hudSamples = new long[SAMPLE_COUNT];
    private final long[] frameSamples = new long[SAMPLE_COUNT];

    private int sampleIndex;
    private int sampleCount;

    private long actNs;
    private long mapNs;
    private long drawNs;
    private long hudNs;

    private int cacheInvalidations;
    private int cacheInvalidationsPerSecond;

    private float elapsedSinceReport;
    private int invalidationsSinceReport;

    private int mapLayerWidth;
    private int mapLayerHeight;
    private int matrixWidth;
    private int matrixHeight;
    private int lastActorCount;
    private int lastVisibleCount;
    private int lastCulledCount;

    public void beginAct() {
        actNs = System.nanoTime();
    }

    public void endAct() {
        actNs = System.nanoTime() - actNs;
    }

    public void beginMapRender() {
        mapNs = System.nanoTime();
    }

    public void endMapRender() {
        mapNs = System.nanoTime() - mapNs;
    }

    public void beginStageDraw() {
        drawNs = System.nanoTime();
    }

    public void endStageDraw() {
        drawNs = System.nanoTime() - drawNs;
    }

    public void beginHud() {
        hudNs = System.nanoTime();
    }

    public void endHud() {
        hudNs = System.nanoTime() - hudNs;
    }

    public void recordCacheInvalidation() {
        cacheInvalidations++;
        invalidationsSinceReport++;
    }

    public void setMapSize(int width, int height) {
        mapLayerWidth = width;
        mapLayerHeight = height;
    }

    public void setMatrixSize(int width, int height) {
        matrixWidth = width;
        matrixHeight = height;
    }

    public void endFrame(float delta, int actorCount) {
        endFrame(delta, actorCount, 0, 0);
    }

    public void endFrame(float delta, int actorCount, int visibleCount, int culledCount) {
        lastActorCount = actorCount;
        lastVisibleCount = visibleCount;
        lastCulledCount = culledCount;
        long frameNs = (long) (delta * 1_000_000_000L);

        actSamples[sampleIndex] = actNs;
        mapSamples[sampleIndex] = mapNs;
        drawSamples[sampleIndex] = drawNs;
        hudSamples[sampleIndex] = hudNs;
        frameSamples[sampleIndex] = frameNs;

        sampleIndex = (sampleIndex + 1) % SAMPLE_COUNT;
        if (sampleCount < SAMPLE_COUNT) {
            sampleCount++;
        }

        elapsedSinceReport += delta;
        if (elapsedSinceReport >= 1f) {
            cacheInvalidationsPerSecond = invalidationsSinceReport;
            invalidationsSinceReport = 0;
            elapsedSinceReport = 0f;
        }
    }

    public String formatOverlay() {
        float fps = Gdx.graphics.getFramesPerSecond();
        float frameMs = averageMs(frameSamples);
        float actMs = averageMs(actSamples);
        float mapMs = averageMs(mapSamples);
        float drawMs = averageMs(drawSamples);
        float hudMs = averageMs(hudSamples);

        return String.format(
                "FPS: %.0f  frame: %.1fms%n" +
                "act: %.1fms  map: %.1fms  draw: %.1fms  hud: %.1fms%n" +
                "actors: %d  draw: %d vis / %d culled%n" +
                "map: %dx%d  matrix: %dx%d%n" +
                "cache inv/s: %d  total inv: %d",
                fps, frameMs,
                actMs, mapMs, drawMs, hudMs,
                lastActorCount, lastVisibleCount, lastCulledCount,
                mapLayerWidth, mapLayerHeight, matrixWidth, matrixHeight,
                cacheInvalidationsPerSecond, cacheInvalidations
        );
    }

    private float averageMs(long[] samples) {
        if (sampleCount == 0) {
            return 0f;
        }
        long total = 0;
        for (int i = 0; i < sampleCount; i++) {
            total += samples[i];
        }
        return total / (float) sampleCount / 1_000_000f;
    }
}
