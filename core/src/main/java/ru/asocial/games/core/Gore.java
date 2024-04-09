package ru.asocial.games.core;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class Gore {
    public static Collection<Actor> generateGore(ResourcesManager resourcesManager, Entity entity, Entity relatedEntity) {
        Random random = new Random(System.currentTimeMillis());
        Collection<Actor> goreList = new LinkedList<>();

/*        List<Integer> goreNumbers = Arrays.asList(1, 2, 8, 1, 1);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < goreNumbers.get(i); j++) {
                float angle = random.nextFloat(2 * MathUtils.PI);
                float v = 150;
                float w = random.nextFloat(1000);
                float vx0 = v * MathUtils.sin(angle);
                float vy0 = v * MathUtils.cos(angle);
                Image gore = new Image();
                gore.setDrawable(resourcesManager.getSkin(), "gore/" + (i + 1));
                gore.setBounds(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                gore.setOrigin(gore.getWidth() / 2, gore.getHeight() /2);
                gore.setScale(0.4f);

                //entity.add
                goreList.add(gore);
                gore.addAction(Actions.forever(new Action() {

                    float vx = vx0, vy = vy0;
                    @Override
                    public boolean act(float delta) {
                        Actor a = getActor();
                        float x = a.getX();
                        float y = a.getY();
                        float dvy = -200 * delta;
                        vy += dvy;
                        float dy = vy * delta;
                        float dx = vx0 * delta;
                        a.setPosition(x + dx, y + dy);
                        float da = w * delta;
                        a.setRotation(a.getRotation() + da);
                        return true;
                    }
                }));
            }
        }*/

/*        {
            Image blood = new Image();
            blood.setDrawable(resourcesManager.getSkin(), "blood/" + (random.nextInt(6) + 1));
            blood.setBounds(entity.getX(), entity.getY() - 48, entity.getWidth(), entity.getHeight());
            blood.setOrigin(blood.getWidth() / 2, blood.getHeight() /2);
            goreList.add(blood);
        }*/
        if (relatedEntity != null){
            Image blood = new Image();
            blood.setDrawable(resourcesManager.getSkin(), "blood/" + (random.nextInt(6) + 1));
            blood.setBounds(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            blood.setOrigin(blood.getWidth() / 2, blood.getHeight() /2);
            goreList.add(blood);
            blood.addAction(Actions.forever(new Action() {
                @Override
                public boolean act(float delta) {
                    getActor().setPosition(relatedEntity.getX(), relatedEntity.getY());
                    getActor().setRotation(relatedEntity.getRotation());
                    return true;
                }
            }));
        }

        return goreList;
    }
}
