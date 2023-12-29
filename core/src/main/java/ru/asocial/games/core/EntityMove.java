package ru.asocial.games.core;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Optional;

public enum EntityMove {

    RIGHT(new GridPoint2(1, 0)),
    LEFT(new GridPoint2(-1, 0)),
    UP(new GridPoint2(0, 1)),
    DOWN(new GridPoint2(0, -1));

    private final GridPoint2 direction;

    public static EntityMove fromVector2(Vector2 vector) {
        GridPoint2 gridPoint2 = new GridPoint2((int) vector.x, (int) vector.y);
        Optional<EntityMove> result = Arrays.stream(values()).filter((d) -> d.direction.equals(gridPoint2)).findFirst();
        return result.orElse(null);
    }

    EntityMove(GridPoint2 direction) {
        this.direction = direction;
    }
}
