package ru.asocial.games.core;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import ru.asocial.games.core.behaviours.*;
import ru.asocial.games.core.events.EntityEvent;
import ru.asocial.games.core.renderers.AnimatedEntityRenderer;
import ru.asocial.games.core.renderers.DefaultEntityRenderer;
import ru.asocial.games.core.renderers.RollingStoneRenderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityFactory {

    private ResourcesManager resourcesManager;

    private Layers layers;

    private Stage stage;

    private IMessagingService messagingService;

    public EntityFactory(ResourcesManager resourcesManager, Layers layers, Stage stage, IMessagingService messagingService) {
        this.resourcesManager = resourcesManager;
        this.stage = stage;
        this.layers = layers;
        this.messagingService = messagingService;
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
            float frameDur = 0.3f;
            Array<TextureRegion> regionsFront = resourcesManager.getSkin().getRegions(type+"/front");
            Animation<TextureRegion> front = new Animation<>(frameDur, regionsFront, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsBack = skin.getRegions(type+"/back");
            Animation<TextureRegion> back = new Animation<>(frameDur,regionsBack, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsLeft = skin.getRegions(type+"/left");
            Animation<TextureRegion> left = new Animation<>(frameDur,regionsLeft, Animation.PlayMode.LOOP);

            Array<TextureRegion> regionsRight = skin.getRegions(type+"/right");
            Animation<TextureRegion> right = new Animation<>(frameDur,regionsRight, Animation.PlayMode.LOOP);

            Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();
            animationMap.put(EntityOrientation.RIGHT.name(), right);
            animationMap.put(EntityOrientation.LEFT.name(), left);
            animationMap.put(EntityOrientation.BACK.name(), back);
            animationMap.put(EntityOrientation.FRONT.name(), front);
            entity.putProperty(PropertyKeys.ANIMATIONS, animationMap);
            entity.putProperty(PropertyKeys.ORIENTATION, EntityOrientation.FRONT.name());
            entity.setRenderer(new AnimatedEntityRenderer());
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
            entity.addBehaviour(new WasdController(layers));
        }

        boolean canFall = object.getProperties().get(PropertyKeys.CAN_FALL, false, Boolean.class);
        boolean canRoll = object.getProperties().get(PropertyKeys.CAN_ROLL, false, Boolean.class);
        if (canFall && canRoll) {
            entity.setRenderer(new RollingStoneRenderer());
            entity.addBehaviour(new RollingStoneBehavior(layers));
        }
        else if (canFall) {
            entity.addBehaviour(new FallingBehavior(layers));
        }
        else if (canRoll) {
            entity.addBehaviour(new RollingBehavior(layers));
            entity.setRenderer(new RollingStoneRenderer());
        }

        entity.addListener(event -> {
            if (event instanceof EntityEvent) {
                messagingService.writeMessage(event.getClass().getName(), event.toString());
                return true;
            }
            return false;
        });

        return entity;
    }
}
