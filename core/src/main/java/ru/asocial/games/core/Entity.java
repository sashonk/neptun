package ru.asocial.games.core;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.asocial.games.core.behaviours.Behaviour;
import ru.asocial.games.core.renderers.EntityRenderer;

import java.util.*;

public class Entity extends Actor {

    private List<Behaviour> behaviours = new LinkedList<>();
    private EntityRenderer renderer;

    private Map<String, Object> properties = new HashMap<>();

    public boolean hasBehaviour(Class<? extends Behaviour> cls) {
        return behaviours.stream().anyMatch(behaviour -> cls.isAssignableFrom(behaviour.getClass()));
    }

    public String getType() {
        return (String) properties.get("type");
    }

    public void removeBehaviours(Class<? extends Behaviour> cls) {
        behaviours.removeIf(behaviour -> cls.isAssignableFrom(behaviour.getClass()));
    }

    public void setRenderer(EntityRenderer renderer) {
        this.renderer = renderer;
    }

    public void addBehaviour(Behaviour behaviour) {
        behaviours.add(behaviour);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){

        renderer.draw(this, batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        behaviours.forEach(behaviour -> behaviour.act(this, delta));
    }

    public Map<String, Object> getAllProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void putProperty(String key, Object value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key, Class<T> cls) {
        Object value = properties.get(key);
        if (value == null) {
            return null;
        }
        if (cls.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        throw new ClassCastException(value.getClass() + " can not be assigned to " + cls);
    }

    public <T> T getPropertyOrDefault(String key, Class<T> cls, T def) {
        T value = getProperty(key, cls);
        return value != null ? value : def;
    }

}
