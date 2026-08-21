package ru.asocial.games.core;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class HUDPlayerController extends Group implements IPlayerController {

    private static final float BUTTON_SIZE = 72f;
    private static final float MARGIN = 24f;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean bombPressed;

    private final LevelState levelState;
    private final Image bombButton;
    private final Label bombStatusLabel;

    public HUDPlayerController(Skin skin, float viewportWidth, float viewportHeight, LevelState levelState) {
        this.levelState = levelState;
        setSize(viewportWidth, viewportHeight);
        setTouchable(Touchable.childrenOnly);

        Group dpad = new Group();
        float pad = 8f;
        float dpadW = BUTTON_SIZE * 3 + pad * 2;
        float dpadH = BUTTON_SIZE * 2 + pad;
        dpad.setSize(dpadW, dpadH);
        dpad.setPosition(MARGIN, MARGIN);

        addDpadButton(dpad, skin, "▲", BUTTON_SIZE + pad, BUTTON_SIZE + pad,
                () -> upPressed = true, () -> upPressed = false);
        addDpadButton(dpad, skin, "▼", BUTTON_SIZE + pad, 0,
                () -> downPressed = true, () -> downPressed = false);
        addDpadButton(dpad, skin, "◀", 0, pad,
                () -> leftPressed = true, () -> leftPressed = false);
        addDpadButton(dpad, skin, "▶", BUTTON_SIZE * 2 + pad * 2, pad,
                () -> rightPressed = true, () -> rightPressed = false);
        addActor(dpad);

        Drawable fallback = skin.getDrawable("dbg_frame");
        bombButton = createBombButton(skin, fallback);
        bombButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        bombButton.setPosition(viewportWidth - BUTTON_SIZE - MARGIN, MARGIN);
        addActor(bombButton);

        bombStatusLabel = new Label("3", skin);
        bombStatusLabel.setAlignment(Align.center);
        bombStatusLabel.setSize(BUTTON_SIZE, BUTTON_SIZE);
        bombStatusLabel.setPosition(bombButton.getX(), bombButton.getY());
        bombStatusLabel.setTouchable(Touchable.disabled);
        addActor(bombStatusLabel);
    }

    private void addDpadButton(Group parent, Skin skin, String text, float x, float y,
                               Runnable onDown, Runnable onUp) {
        Label label = new Label(text, skin);
        label.setFontScale(1.4f);
        label.setSize(BUTTON_SIZE, BUTTON_SIZE);
        label.setAlignment(Align.center);
        label.setPosition(x, y);
        label.setTouchable(Touchable.enabled);
        label.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onDown.run();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onUp.run();
            }
        });
        parent.addActor(label);
    }

    private Image createBombButton(Skin skin, Drawable fallback) {
        Drawable bombDrawable = skin.has("bomb_icon", Drawable.class)
                ? skin.getDrawable("bomb_icon")
                : fallback;
        Image bomb = new Image(bombDrawable);
        bomb.setName("bomb");
        bomb.setTouchable(Touchable.enabled);
        bomb.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (levelState.canPlaceBomb()) {
                    bombPressed = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                bombPressed = false;
            }
        });
        return bomb;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateBombUi();
    }

    private void updateBombUi() {
        boolean canBomb = levelState.canPlaceBomb();
        bombButton.setTouchable(canBomb ? Touchable.enabled : Touchable.disabled);
        bombButton.setColor(1f, 1f, 1f, canBomb ? 1f : 0.45f);

        if (levelState.getBombCooldown() > 0f) {
            bombStatusLabel.setText(String.format("%.1f", levelState.getBombCooldown()));
        } else {
            bombStatusLabel.setText(Integer.toString(levelState.getBombsRemaining()));
        }
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
