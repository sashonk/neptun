package ru.asocial.games.core;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class HUDPlayerController extends Group implements IPlayerController {

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean bombPressed;

    public HUDPlayerController(Skin skin, float width, float height) {
        Drawable d = skin.getDrawable("dbg_frame");
        this.setSize(width, height);
        this.setTouchable(Touchable.childrenOnly);
        float w = width / 4, h = height / 10;
        float dx = 1.2f, dy = 1.2f;
        Image arrowUp = new Image(d);
        arrowUp.setName("up");

        arrowUp.setSize(  w, h);
        arrowUp.setPosition(0 -w / 2, dy * h);
        addActor(arrowUp);
        arrowUp.setTouchable(Touchable.enabled);
        arrowUp.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image arrowDown = new Image(d);
        arrowUp.setName("down");
        arrowDown.setSize(w, h);
        arrowDown.setPosition(0 -w / 2, -dy * h);
        addActor(arrowDown);
        arrowDown.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image arrowRight = new Image(d);
        arrowUp.setName("right");
        arrowRight.setSize(w, h);
        arrowRight.setPosition(dx * w -w / 2, 0);
        addActor(arrowRight);
        arrowRight.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image arrowLeft = new Image(d);
        arrowUp.setName("left");
        arrowLeft.setSize(w, h);
        arrowLeft.setPosition(-dx * w -w / 2, 0);
        addActor(arrowLeft);
        arrowLeft.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image bomb = new Image(d);
        bomb.setName("bomb");
        bomb.setSize(w, h);
        bomb.setPosition(-w / 2, 0);
        addActor(bomb);
        bomb.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                bombPressed = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                bombPressed = false;
            }
        });
    }

    @Override
    public boolean isUpPressed() {
        return upPressed;
    }

    @Override
    public boolean isDownPressed() {
        return downPressed;
    }

    @Override
    public boolean isLeftPressed() {
        return leftPressed;
    }

    @Override
    public boolean isRightPressed() {
        return rightPressed;
    }

    @Override
    public boolean isBombPressed() {
        return bombPressed;
    }
}
