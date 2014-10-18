package cmps244;

import java.util.Random;
import dk.itu.mario.level.Level;
import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.engine.LevelFactory;

public class CustomizedLevel extends Level implements LevelInterface {

    public CustomizedLevel(int width, int height, long seed, int difficulty,
                           int type, GamePlay playerMetrics) {
        super(width, height);
    }
}