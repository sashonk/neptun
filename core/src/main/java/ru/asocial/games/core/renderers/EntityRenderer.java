package ru.asocial.games.core.renderers;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.asocial.games.core.Entity;

public interface EntityRenderer {

    void render(Entity entity, Batch batch, float parentAlpha);
}
