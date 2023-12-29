package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;

public class RollingStoneBehavior extends MovingBehavior{

    private final RollingBehavior rollingBehavior;
    private final FallingBehavior fallingBehavior;

    public RollingStoneBehavior(Layers layers) {
        super(layers);

        rollingBehavior = new RollingBehavior(layers);
        fallingBehavior = new FallingBehavior(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        Vector2 move = fallingBehavior.findNextMove(entity);
        return move != null ? move : rollingBehavior.findNextMove(entity);
    }
}
