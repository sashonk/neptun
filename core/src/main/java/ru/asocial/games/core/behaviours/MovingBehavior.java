package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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

    private float delta;

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
        this.delta = delta;
        boolean isMoving = entity.getPropertyOrDefault(PropertyKeys.IS_MOVING, Boolean.class, false);
        if (entity.getParent() == null) {
            //matrix.free(prevX, prevY);
            if (isMoving) {
                Vector2 movingTo = entity.getProperty(PropertyKeys.MOVING_TO, Vector2.class);
                if (entity == getObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()))) {
                    freeObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()));
                }
            }

            entity.clear();
            return;
        }

        if (!isMoving) {
            Vector2 nextMove = findNextMove(entity);
            if (nextMove != null) {
                entity.putProperty(PropertyKeys.IS_MOVING, true);
                EntityMove direction = EntityMove.fromVector2(nextMove);
                entity.putProperty("next_move", nextMove);
                EntityOrientation orientation = EntityOrientation.fromMoveDirection(direction);
                entity.putProperty(PropertyKeys.ORIENTATION, orientation.name());
                prevX = (int) entity.getX() / (int) entity.getWidth();
                prevY = (int) entity.getY() / (int) entity.getHeight();
                Vector2 moveTo = new Vector2(entity.getX() + nextMove.x * entity.getWidth(), entity.getY() + nextMove.y * entity.getHeight());
                entity.putProperty(PropertyKeys.MOVING_TO, moveTo);
                Entity prev = matrix.get(prevX, prevY);
                if (prev == entity) {
                    matrix.free(prevX, prevY);
                }

                Action moveToAction  = Actions.sequence(Actions.moveTo(entity.getX() + nextMove.x * entity.getWidth(), entity.getY() + nextMove.y * entity.getHeight(), Config.SINGLE_MOVE_DURATION, Interpolation.linear), new Action() {
                    @Override
                    public boolean act(float delta) {
                        entity.putProperty(PropertyKeys.IS_MOVING, false);
                        entity.putProperty(PropertyKeys.IS_ROLLING, false);
                        MovingBehavior.this.act( entity, delta);
                        return true;
                    }
                });

                entity.addAction(moveToAction);

                int tx = (int) entity.getX() / (int) entity.getWidth()  + (int)nextMove.x;
                int ty = (int) entity.getY() / (int) entity.getHeight() + (int) nextMove.y;
                matrix.take(tx,ty, entity);
                entity.fire(new MoveEvent(tx, ty));

                if (moveCallback != null) {
                    moveCallback.onMove(nextMove);
                }
            }
        }
    }

    protected abstract Vector2 findNextMove(Entity entity);
}
