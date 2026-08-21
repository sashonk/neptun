package ru.asocial.games.core;

public class LevelState {

    public static final int BOMBS_PER_LEVEL = 3;
    public static final float BOMB_COOLDOWN_SEC = 3f;

    private int levelNumber;
    private int bombsRemaining;
    private float bombCooldown;
    private boolean hasKey;

    public LevelState(int levelNumber) {
        this.levelNumber = levelNumber;
        reset();
    }

    public void reset() {
        bombsRemaining = BOMBS_PER_LEVEL;
        bombCooldown = 0f;
        hasKey = false;
    }

    public void act(float delta) {
        if (bombCooldown > 0f) {
            bombCooldown = Math.max(0f, bombCooldown - delta);
        }
    }

    public boolean canPlaceBomb() {
        return bombsRemaining > 0 && bombCooldown <= 0f;
    }

    public void onBombPlaced() {
        if (bombsRemaining > 0) {
            bombsRemaining--;
            bombCooldown = BOMB_COOLDOWN_SEC;
        }
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getBombsRemaining() {
        return bombsRemaining;
    }

    public float getBombCooldown() {
        return bombCooldown;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
}
