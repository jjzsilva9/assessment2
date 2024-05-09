package jvms.assessment2.gdxtesting;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.components.*;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.MoveDirection;
import io.github.uoyeng1g6.constants.PlayerConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.systems.AnimationSystem;
import io.github.uoyeng1g6.systems.PlayerInputSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.uoyeng1g6.systems.PlayerInteractionSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(GdxTestRunner.class)

public class AnimationSystemTest {

    Engine engine;
    GameState gameState;

    @Before
    public void initialiseEngine() {
    }

    @Test
    public void testSuccessfulAddToEngine() {

        engine = new PooledEngine();
        gameState = new GameState();

        TextureAtlas playerTextureAtlas = new TextureAtlas(Gdx.files.internal("sprites/player.txt"));

        float spriteScale = 1f;

        AnimationComponent ac = new AnimationComponent(spriteScale);

        ac.animations.put(
                MoveDirection.STATIONARY,
                new Animation<>(1f, playerTextureAtlas.createSprites("stationary"), Animation.PlayMode.LOOP));
        Entity testEntity = engine.createEntity().add(ac);
        Entity notAnimationEntity = engine.createEntity();
        AnimationSystem as =  new AnimationSystem(mock(SpriteBatch.class), gameState);

        engine.addSystem(as);
        engine.addEntity(testEntity);
        engine.addEntity(notAnimationEntity);

        //Test the system has successfully added the system to the engine.
        assertEquals("Animation system successful addition to engine.", as, engine.getSystem(AnimationSystem.class));

        //Test the entity component with the animation component can be found from within the Animation system.
        assertEquals("Animated entity acessable", ac, engine.getSystem(AnimationSystem.class).getAm().get(testEntity));

    }

}