package ru.asocial.games.core.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import ru.asocial.games.core.PropertyKeys;
import ru.asocial.games.core.Entity;

import java.util.Map;

public class AnimatedEntityRenderer implements EntityRenderer{

    protected TextureRegion getCurrentFrame(Entity entity) {
        float stateTime = entity.getPropertyOrDefault(PropertyKeys.ANIMATION_STATE_TIME, Float.class, 0f);

        if (entity.getPropertyOrDefault(PropertyKeys.IS_ANIMATION_RUNNING, Boolean.class, false)) {
            stateTime += Gdx.graphics.getDeltaTime();
        }
        else {
            stateTime = 0.5f;
        }

        entity.putProperty(PropertyKeys.ANIMATION_STATE_TIME, stateTime);

        Object animationsObj = entity.getProperty(PropertyKeys.ANIMATION, Object.class);
        Animation<TextureRegion> currentAnimation;
        if (animationsObj instanceof Map) {
            Map<String, Animation<TextureRegion>> animations = (Map<String, Animation<TextureRegion>>) animationsObj;
            String orientation = entity.getProperty(PropertyKeys.ORIENTATION, String.class);
            currentAnimation = animations.get(orientation);
        }
        else if (animationsObj instanceof Animation) {
            currentAnimation = (Animation<TextureRegion>)animationsObj;
        }
        else {
            throw new GdxRuntimeException("animation not found");
        }
        return currentAnimation.getKeyFrame(stateTime);
    };

    @Override
    public void render(Entity entity, Batch batch, float parentAlpha) {
        TextureRegion region = getCurrentFrame(entity);

        batch.draw(region, entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }
}
