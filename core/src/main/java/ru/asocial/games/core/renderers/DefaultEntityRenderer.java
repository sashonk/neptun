package ru.asocial.games.core.renderers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.PropertyKeys;

public class DefaultEntityRenderer implements EntityRenderer{
    @Override
    public void render(Entity entity, Batch batch, float parentAlpha) {
        TextureRegion region = entity.getProperty(PropertyKeys.TEXTURE_REGION, TextureRegion.class);
        float angle = entity.getRotation();
        batch.draw(region, entity.getX(), entity.getY(), entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth(), entity.getHeight(), 1, 1, angle);
    }
}
