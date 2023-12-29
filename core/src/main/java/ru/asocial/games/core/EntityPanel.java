package ru.asocial.games.core;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Collection;
import java.util.Map;

public class EntityPanel extends Table {

    private Skin skin;

    public EntityPanel(Skin skin) {
        this.skin = skin;
    }

    public void init(Entity entity) {
        clearChildren();
        for (Map.Entry<String, Object> e : entity.getAllProperties().entrySet()) {
            Label key = new Label(e.getKey(), skin);
            String text = e.getValue() != null ? e.getValue().toString() : "";
            if (e.getValue() instanceof TextureRegion) {
                text = "[TextureRegion]";
            }
            else if (e.getValue() instanceof Collection) {
                text = "[Collection]";
            }
            else if (e.getValue() instanceof Map) {
                text = "[Map]";
            }
            Label value = new Label(text, skin);
            this.add(key, value).row();
        }
    }
}
