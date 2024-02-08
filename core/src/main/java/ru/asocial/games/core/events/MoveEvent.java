package ru.asocial.games.core.events;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class MoveEvent extends EntityEvent {

    private String type;

    public MoveEvent(String type) {
        this.type = type;
    }
    @Override
    public String toString(){
        Actor target = getTarget();
        if (target != null) {
            String targetName = target.getName();
            String name = targetName != null ? targetName : "Unk";
            return name + ":" + type + ":" + target.getX() + ":" + target.getY();
        }
        return "";
    }
}
