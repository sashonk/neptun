package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.*;
import ru.asocial.games.core.events.MoveEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class MovingBehavior implements Behaviour {

    private final TiledMapTileLayer wallsLayer;
    private final TiledMapTileLayer dirtLayer;
    private final MapLayer objectLayer;

    private static EntityMatrix matrix;
    private static TileLayerChangedListener tileLayerChangedListener;

    private int prevX, prevY;

    private MoveCallback moveCallback;

    private static Set<Entity> fallingEntities = new HashSet<>();

    public static int getFallingEntitiesCounter() {

        return fallingEntities.size();
    }

    public static void incrementFallingEntitiesCounter(Entity e){
        fallingEntities.add(e);
    }

    public static void decrementFallingEntitiesCounter(Entity e){
       fallingEntities.remove(e);
    }


    public static void setObjectMatrix(EntityMatrix m) {
        matrix = m;
    }

    public static void setTileLayerChangedListener(TileLayerChangedListener listener) {
        tileLayerChangedListener = listener;
    }

    public interface TileLayerChangedListener {
        void onTileLayerChanged();
    }

    protected TiledMapTileLayer getWallsLayer() {
        return wallsLayer;
    }

    protected MapLayer getObjectLayer() {
        return objectLayer;
    }

    protected boolean isCellFree(int cx, int cy) {
        return getWallsLayer().getCell(cx, cy) == null && matrix.isFree(cx, cy) && !isDirtAtCell(cx, cy);
    }

    protected Entity getObjectAtCell(Entity entity, Vector2 dir) {
        return matrix.get((int) entity.getX() / (int) entity.getWidth() + (int) dir.x,(int) entity.getY() / (int) entity.getHeight() + (int) dir.y);
    }

    protected Entity getObjectAtCell(int cellX, int cellY) {
        return matrix.get(cellX,cellY);
    }

    protected boolean isDirtAtCell(int cellX, int cellY) {
        return CellUtils.isDirtAtCell(dirtLayer, cellX, cellY);
    }

    protected void removeDirtAtCell(int cellX, int cellY) {
        dirtLayer.setCell(cellX, cellY, null);
        if (tileLayerChangedListener != null) {
            tileLayerChangedListener.onTileLayerChanged();
        }
    }

    protected void freeObject(Entity entity) {
        EntityMatrixUtils.freeObject(matrix, entity);
    }

    protected void freeObjectAtCell(int cellX, int cellY) {
        matrix.free(cellX,cellY);
    }

    public MovingBehavior(Layers layers) {
        this.wallsLayer = layers.getWallLayer();
        this.objectLayer = layers.getObjectLayer();
        this.dirtLayer = layers.getDirtLayer();
    }

    public void setMoveCallback(MoveCallback callback) {
        this.moveCallback = callback;
    }

    public interface MoveCallback {
        void onMove(Vector2 move);
    }

    @Override
    public void act(Entity entity, float delta) {
        if (entity.getParent() == null) {
            if (entity.getPropertyOrDefault(PropertyKeys.IS_MOVING, Boolean.class, false)) {
                Vector2 movingTo = entity.getProperty(PropertyKeys.MOVING_TO, Vector2.class);
                if (entity == getObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()))) {
                    freeObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()));
                }
            }

            entity.clear();
            return;
        }

        long frameId = Gdx.graphics.getFrameId();
        Long lastActFrame = entity.getProperty(PropertyKeys.MOVE_ACT_FRAME, Long.class);
        if (lastActFrame != null && lastActFrame == frameId) {
            return;
        }
        entity.putProperty(PropertyKeys.MOVE_ACT_FRAME, frameId);

        boolean isMoving = entity.getPropertyOrDefault(PropertyKeys.IS_MOVING, Boolean.class, false);
        if (!isMoving) {
            Vector2 nextMove = findNextMove(entity);
            if (nextMove == null) {
                return;
            }
            beginMove(entity, nextMove);
            entity.putProperty(PropertyKeys.MOVE_PROGRESS, 0f);
        }

        float progress = entity.getPropertyOrDefault(PropertyKeys.MOVE_PROGRESS, Float.class, 0f);
        progress += delta / Config.SINGLE_MOVE_DURATION;

        while (progress >= 1f) {
            progress -= 1f;
            Vector2 to = entity.getProperty(PropertyKeys.MOVING_TO, Vector2.class);
            entity.setPosition(to.x, to.y);
            entity.putProperty(PropertyKeys.IS_ROLLING, false);

            Vector2 nextMove = findNextMove(entity);
            if (nextMove == null) {
                entity.putProperty(PropertyKeys.IS_MOVING, false);
                entity.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, false);
                entity.putProperty(PropertyKeys.MOVE_PROGRESS, 0f);
                return;
            }
            beginMove(entity, nextMove);
        }

        entity.putProperty(PropertyKeys.MOVE_PROGRESS, progress);
        Vector2 from = entity.getProperty(PropertyKeys.MOVE_FROM, Vector2.class);
        Vector2 to = entity.getProperty(PropertyKeys.MOVING_TO, Vector2.class);
        entity.setPosition(
                from.x + (to.x - from.x) * progress,
                from.y + (to.y - from.y) * progress
        );
    }

    private void beginMove(Entity entity, Vector2 nextMove) {
        entity.putProperty(PropertyKeys.IS_MOVING, true);
        entity.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, true);

        EntityMove direction = EntityMove.fromVector2(nextMove);
        entity.putProperty("next_move", nextMove);
        EntityOrientation orientation = EntityOrientation.fromMoveDirection(direction);
        entity.putProperty(PropertyKeys.ORIENTATION, orientation.name());

        int cellWidth = (int) entity.getWidth();
        int cellHeight = (int) entity.getHeight();
        prevX = (int) entity.getX() / cellWidth;
        prevY = (int) entity.getY() / cellHeight;

        entity.putProperty(PropertyKeys.MOVE_FROM, new Vector2(entity.getX(), entity.getY()));
        entity.putProperty(PropertyKeys.MOVING_TO, new Vector2(
                entity.getX() + nextMove.x * cellWidth,
                entity.getY() + nextMove.y * cellHeight
        ));

        Entity prev = matrix.get(prevX, prevY);
        if (prev == entity) {
            matrix.free(prevX, prevY);
        }

        int tx = prevX + (int) nextMove.x;
        int ty = prevY + (int) nextMove.y;
        matrix.take(tx, ty, entity);
        entity.fire(new MoveEvent(tx, ty));

        if (moveCallback != null) {
            moveCallback.onMove(nextMove);
        }
    }

    protected abstract Vector2 findNextMove(Entity entity);
}
