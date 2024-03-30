package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Layers;

import java.util.Iterator;
import java.util.List;

public class ReplayMovesBehavior extends PlayerBehavior {

    Iterator<Vector2> moves;

    int counter = 1;

    float delay;

    public ReplayMovesBehavior(Layers layers, List<Vector2> moves) {
        super(layers);
        this.moves = moves.iterator();
    }

    protected Vector2 doFindNextMove() {
/*        if (counter % 11 == 0 && getFallingEntitiesCounter() == 0) {
            if (delay >= 0.5f) {
                counter++;
                delay = 0;
            }
            delay += Gdx.graphics.getDeltaTime();
            return null;
        }*/

        //return moves.hasNext() ? moves.next() : null;

        counter++;
        return moves.hasNext() ? moves.next() : null;
    }
}
