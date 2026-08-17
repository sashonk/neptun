package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import ru.asocial.games.core.behaviours.*;
import ru.asocial.games.core.events.ExplodeEntityEvent;
import ru.asocial.games.core.renderers.AnimatedEntityRenderer;
import ru.asocial.games.core.renderers.DefaultEntityRenderer;

import java.io.BufferedReader;
import java.util.*;

public class EntityFactory {

    private ResourcesManager resourcesManager;

    private Layers layers;

    private Stage stage;

    public EntityFactory(ResourcesManager resourcesManager, Layers layers, Stage stage) {
        this.resourcesManager = resourcesManager;
        this.stage = stage;
        this.layers = layers;
    }

    public Entity newExplosion(Entity center, int offsetX, int offsetY) {
        Entity explosion = new Entity();
        float width = 48, height = 48;
        explosion.setBounds(center.getX() + width * offsetX, center.getY() + height * offsetY, width, height);
        explosion.setRenderer(new AnimatedEntityRenderer());
        Animation<TextureRegion> animation = new Animation<>(0.04f, resourcesManager.getSkin().getRegions("explosion/explosion"));
        explosion.putProperty(PropertyKeys.ANIMATION, animation);
        explosion.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, true);
        explosion.addBehaviour(new LimitedLifeTimeBehavior(0.2f));
        return explosion;
    }

    public Entity newBomb(Entity center) {
        Entity bomb = new Entity();
        float width = 48, height = 48;
        bomb.setBounds(center.getX(), center.getY(), width, height);
        bomb.setRenderer(new AnimatedEntityRenderer());
        Array<TextureRegion> keyFrames = resourcesManager.getSkin().getRegions("bomb/bomb");
        Animation<TextureRegion> animation = new Animation<>(0.1875f, keyFrames);
        bomb.putProperty(PropertyKeys.ANIMATION, animation);
        bomb.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, true);
        bomb.addBehaviour(new LimitedLifeTimeBehavior(3f));
        bomb.setScaleX(0.8f);
        bomb.addAction(Actions.sequence(Actions.delay(3f), new Action() {
            @Override
            public boolean act(float delta) {
                bomb.fire(new ExplodeEntityEvent());
                return true;
            }
        }));
        return bomb;
    }

    public Entity create(MapObject object) {
        TiledMapTileMapObject tileMapObject = (TiledMapTileMapObject) object;
        String type = object.getProperties().get(PropertyKeys.TYPE, String.class);
        Entity entity = new Entity();
        entity.setBounds(tileMapObject.getX(), tileMapObject.getY(),
                tileMapObject.getProperties().get("width", Float.class),
                tileMapObject.getProperties().get("height", Float.class));

        Iterator<String> propIter = object.getProperties().getKeys();
        while (propIter.hasNext()) {
            String propName = propIter.next();
            Object propValue = object.getProperties().get(propName);
            entity.putProperty(propName, propValue);
        }

        entity.setName(object.getName());

        entity.setRenderer(new DefaultEntityRenderer());
        entity.putProperty(PropertyKeys.TEXTURE_REGION, tileMapObject.getTextureRegion());
        //entity.setTouchable(Touchable.disabled);

        if (!object.getProperties().containsKey(PropertyKeys.ORIENTATION)) {
            entity.putProperty(PropertyKeys.ORIENTATION, "front");
        }

        if (object.getProperties().get(PropertyKeys.HAS_ANIMATIONS, false, Boolean.class)) {
            Skin skin = resourcesManager.getSkin();
            Array<TextureRegion> regionsFront = resourcesManager.getSkin().getRegions(type+"/front");
            float frameDur = Config.SINGLE_MOVE_DURATION / regionsFront.size;
            Animation<TextureRegion> front = new Animation<>(frameDur, regionsFront, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsBack = skin.getRegions(type+"/back");
            Animation<TextureRegion> back = new Animation<>(frameDur, regionsBack, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsLeft = skin.getRegions(type+"/left");
            Animation<TextureRegion> left = new Animation<>(frameDur, regionsLeft, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsRight = skin.getRegions(type+"/right");
            Animation<TextureRegion> right = new Animation<>(frameDur, regionsRight, Animation.PlayMode.LOOP);

            Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();
            animationMap.put(EntityOrientation.RIGHT.name(), right);
            animationMap.put(EntityOrientation.LEFT.name(), left);
            animationMap.put(EntityOrientation.BACK.name(), back);
            animationMap.put(EntityOrientation.FRONT.name(), front);
            entity.putProperty(PropertyKeys.ANIMATION, animationMap);
            entity.putProperty(PropertyKeys.ORIENTATION, EntityOrientation.FRONT.name());
            entity.setRenderer(new AnimatedEntityRenderer());
        }
        if (object.getProperties().get(PropertyKeys.IS_ENEMY, false, Boolean.class)) {
            entity.addBehaviour(new EnemyBehavior());
        }

        if (object.getProperties().get(PropertyKeys.IS_WALKING, false, Boolean.class)) {
            entity.addBehaviour(new WalkingBehaviour(layers));
            entity.putProperty(WalkingBehaviour.PropertyKey_direction, new Vector2(1, 0));
        }
        if (object.getProperties().get(PropertyKeys.CHASE_CAMERA, false, Boolean.class)) {
            entity.addBehaviour(new ChaseCamera(stage.getCamera()));
            entity.putProperty(WalkingBehaviour.PropertyKey_direction, new Vector2(1, 0));
        }

        if (object.getProperties().get(PropertyKeys.ATTACH_CONTROLLER, false, Boolean.class)) {
            boolean replay = Gdx.app.getPreferences("neptun").getBoolean("replay");
            if (replay) {
                FileHandle file = Gdx.files.internal("D:\\work\\nice_moves.txt");
                BufferedReader br = new BufferedReader(file.reader());
                List<Vector2> moves = new LinkedList<>();
                try {
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        Vector2 v = new Vector2();
                        v.fromString(line);
                        moves.add(v);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

                ReplayMovesBehavior replayMovesBehavior = new ReplayMovesBehavior(layers, moves);
                entity.addBehaviour(replayMovesBehavior);
            }
        }

        boolean canFall = object.getProperties().get(PropertyKeys.CAN_FALL, false, Boolean.class);
        boolean canRoll = object.getProperties().get(PropertyKeys.CAN_ROLL, false, Boolean.class);
        if (canFall && canRoll) {
            entity.addBehaviour(new RollingStoneBehavior(layers));
        }
        else if (canFall) {
            entity.addBehaviour(new FallingBehavior(layers));
        }
        else if (canRoll) {
            entity.addBehaviour(new RollingBehavior(layers));
        }

        object.getProperties().put("entity", entity);

        return entity;
    }

}
