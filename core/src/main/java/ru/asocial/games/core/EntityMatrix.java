package ru.asocial.games.core;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Arrays;

public class EntityMatrix extends Actor {

    private Entity[][] entities;


    private TextureRegion dbg_tex;

    public EntityMatrix(int width, int height, ResourcesManager resourcesManager) {

        entities = new Entity[width][];
        for (int i = 0; i< width; i++) {
            entities[i] = new Entity[height];
            Arrays.fill(entities[i], null);
        }

        dbg_tex = resourcesManager.getSkin().getRegion("dbg_frame");
    }

    public void take(int i, int j, Entity e) {
        entities[i][j] = e;
    }

    public void free(int i, int j) {
        entities[i][j] = null;
    }

    public Entity get(int i, int j) {
        return entities[i][j];
    }

    public boolean isFree(int i, int j) {
        return entities[i][j] == null;
    }

    @Override
    public void draw(Batch batch, float parentAlfa) {
        for (int i = 0; i < entities.length; i++) {
            for (int j = 0; j< entities[i].length; j++) {
                if (entities[i][j] != null) {
                    batch.draw(dbg_tex, i * 48, j * 48, 48, 48);
                }
            }
        }
    }
}
