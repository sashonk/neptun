package ru.asocial.games.core;

public enum EntityOrientation {

    FRONT,
    RIGHT,
    BACK,
    LEFT;

    public static EntityOrientation fromMoveDirection(EntityMove direction) {
        if (direction == null) {
            return null;
        }

        switch (direction) {
            case UP:
                return BACK;
            case DOWN:
                return FRONT;
            case RIGHT:
                return RIGHT;
            case LEFT:
                return LEFT;
            default:
                throw new IllegalArgumentException("Unexpected direction: " + direction);
        }
    }
}
