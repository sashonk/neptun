package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;

public class WASDPlayerController extends Group implements IPlayerController {

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean bombPressed;

    @Override
    public void act(float delta) {
        super.act(delta);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.D);
        bombPressed = Gdx.input.isKeyPressed(Input.Keys.B);
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
