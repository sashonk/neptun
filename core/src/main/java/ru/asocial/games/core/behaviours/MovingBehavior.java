package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.*;

public abstract class MovingBehavior implements Behaviour {

    private final TiledMapTileLayer wallsLayer;
    private final TiledMapTileLayer dirtLayer;
    private final MapLayer objectLayer;

    private static EntityMatrix matrix;

    private int prevX, prevY;

    public static void setObjectMatrix(EntityMatrix m) {
        matrix = m;
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
        return dirtLayer.getCell(cellX, cellY) != null;
    }

    protected void removeDirtAtCell(int cellX, int cellY) {
        dirtLayer.setCell(cellX, cellY, null);
    }

    protected void freeObjectAtCell(Entity entity, Vector2 dir) {
        matrix.free((int) entity.getX() / (int) entity.getWidth() + (int) dir.x,(int) entity.getY() / (int) entity.getHeight() + (int) dir.y);
    }

    protected void freeObjectAtCell(int cellX, int cellY) {
        matrix.free(cellX,cellY);
    }

    public MovingBehavior(Layers layers) {
        this.wallsLayer = layers.getWallLayer();
        this.objectLayer = layers.getObjectLayer();
        this.dirtLayer = layers.getDirtLayer();
    }

    @Override
    public void act(Entity entity, float delta) {
        boolean isMoving = entity.getPropertyOrDefault(PropertyKeys.IS_MOVING, Boolean.class, false);
        if (entity.getParent() == null && isMoving) {
            //matrix.free(prevX, prevY);
            Vector2 movingTo = entity.getProperty(PropertyKeys.MOVING_TO, Vector2.class);
            if (entity == getObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()))) {
                freeObjectAtCell((int) (movingTo.x / entity.getWidth()), (int) (movingTo.y / entity.getHeight()));
            }

            entity.clear();
        }
        if (!isMoving) {
            Vector2 nextMove = findNextMove(entity);
            if (nextMove != null) {
                entity.putProperty(PropertyKeys.IS_MOVING, true);
                EntityMove direction = EntityMove.fromVector2(nextMove);
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
                Action moveToAction = Actions.sequence(Actions.moveTo(entity.getX() + nextMove.x * entity.getWidth(), entity.getY() + nextMove.y * entity.getHeight(), Config.SINGLE_MOVE_DURATION), new Action() {
                    @Override
                    public boolean act(float delta) {
                        entity.putProperty(PropertyKeys.IS_MOVING, false);
                        entity.putProperty(PropertyKeys.IS_ROLLING, false);
                        return true;
                    }
                });
                entity.addAction(moveToAction);
                matrix.take((int) entity.getX() / (int) entity.getWidth()  + (int)nextMove.x,(int) entity.getY() / (int) entity.getHeight() + (int) nextMove.y, entity);
            }
        }
    }

    protected abstract Vector2 findNextMove(Entity entity);
}
