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

        float stateTime = entity.getPropertyOrDefault(PropertyKeys.ANIMATION_STATE_TIME, Float.class, 0f);
        if (entity.getPropertyOrDefault(PropertyKeys.IS_ANIMATION_RUNNING, Boolean.class, false)) {
            stateTime += Gdx.graphics.getDeltaTime();
            entity.putProperty(PropertyKeys.ANIMATION_STATE_TIME, stateTime);
        }
        else {
            stateTime = currentAnimation.getFrameDuration() + 0.01f;
        }
        return currentAnimation.getKeyFrame(stateTime);
    };

    @Override
    public void render(Entity entity, Batch batch, float parentAlpha) {
        TextureRegion region = getCurrentFrame(entity);
        float angle = entity.getRotation();
        batch.draw(region, entity.getX(), entity.getY(), entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth(), entity.getHeight(), entity.getScaleX(), entity.getScaleY(), angle);
    }
}
