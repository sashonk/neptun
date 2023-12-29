package ru.asocial.games.core.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import ru.asocial.games.core.Config;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.PropertyKeys;

public class RollingStoneRenderer implements EntityRenderer{
    @Override
    public void draw(Entity entity, Batch batch, float parentAlpha) {
        TextureRegion region = entity.getProperty(PropertyKeys.TEXTURE_REGION, TextureRegion.class);

        float stateTime = entity.getPropertyOrDefault(PropertyKeys.ANIMATION_STATE_TIME, Float.class, 0f);

        if (entity.getPropertyOrDefault(PropertyKeys.IS_ROLLING, Boolean.class, false)) {
            stateTime += Gdx.graphics.getDeltaTime();
            entity.putProperty(PropertyKeys.ANIMATION_STATE_TIME, stateTime);
        }

        boolean clockwise = "right".equals(entity.getPropertyOrDefault(PropertyKeys.ROLLING_DIRECTION, String.class, "right"));
        float o = 2 * MathUtils.radiansToDegrees / Config.SINGLE_MOVE_DURATION * (clockwise ? -1 : 1);
        float angle = o * stateTime;


        batch.draw(region, entity.getX(), entity.getY(), entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth(), entity.getHeight(), 1, 1, angle);
    }
}
