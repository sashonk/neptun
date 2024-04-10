package ru.asocial.games.core.events;

public class MoveEvent extends EntityEvent {

    private int x, y;

    public MoveEvent(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
