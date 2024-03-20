package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourcesManager {


    private AssetManager manager;

    public void finishLoading() {
        manager.finishLoading();
    }

    boolean update() {
        return manager.update();
    }

    void loadResources() {
        Gdx.app.debug(this.getClass().getName(), "begin loading resources");
        manager = new AssetManager();

        manager.load("fonts/calibri24.fnt", BitmapFont.class);
        manager.load("images.atlas", TextureAtlas.class);
    }

    void init() {
        Gdx.app.debug(this.getClass().getName(), "initializing");

        skin = new Skin();

        BitmapFont bitmapFont = manager.get("fonts/calibri24.fnt");
        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.fontColor = Color.WHITE;
        defaultLabelStyle.font = bitmapFont;

        skin.add("default", defaultLabelStyle);

        skin.addRegions(getAtlas());
    }

    public Skin getSkin() {
        return skin;
    }

    public TextureAtlas getAtlas() {
        return manager.get("images.atlas");
    }

    private Skin skin;

    private void disposeInternal() {
        if (skin != null)
            skin.dispose();

        if (manager != null) {
            manager.dispose();
        }
    }


    public void dispose() {

        disposeInternal();
    }
}
