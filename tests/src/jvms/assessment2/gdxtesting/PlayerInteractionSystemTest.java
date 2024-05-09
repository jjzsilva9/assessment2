package jvms.assessment2.gdxtesting;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.uoyeng1g6.components.*;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.PlayerConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.systems.PlayerInputSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.uoyeng1g6.systems.PlayerInteractionSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class PlayerInteractionSystemTest {

    Engine engine;
    GameState gameState;
    Entity playerEntity;
    Entity interactableEntity;

    @Before
    public void initialiseEngine() {

        engine = new PooledEngine();
        gameState = new GameState();

        World world = new World(new Vector2(), true);

        var player = new BodyDef();
        player.type = BodyDef.BodyType.DynamicBody;
        player.position.set(PlayerConstants.START_POSITION);
        var playerBody = world.createBody(player);
        playerBody.setUserData(PlayerConstants.HITBOX_RADIUS);
        var playerCircle = new CircleShape();
        playerCircle.setRadius(PlayerConstants.HITBOX_RADIUS);
        var playerFixture = playerBody.createFixture(playerCircle, 1f);
        playerCircle.dispose();

        var playerAnimations = new AnimationComponent(0.05f);

        playerEntity = engine.createEntity().add(new PlayerComponent()).add(playerAnimations).add(new FixtureComponent(playerFixture));
        engine.addEntity(playerEntity);

        interactableEntity = engine.createEntity()
                .add(new PositionComponent(54, 2.5f))
                .add(new HitboxComponent(new Rectangle(
                        54, 2.5f, 10f, 10f)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 5, ActivityType.MEAL, "Eating...", "food")) {
                        // Notify insufficient time/energy
                    }
                }));

        engine.addEntity(interactableEntity);

        Entity nonInteractableEntity = engine.createEntity()
                .add(new PositionComponent(54, 2.5f))
                .add(new HitboxComponent(new Rectangle(
                        54, 2.5f, 10f, 10f)));

        engine.addEntity(nonInteractableEntity);
    }

    @Test
    public void testSuccessfulAddToEngine() {
        engine.addSystem(new PlayerInteractionSystem(gameState));

        //Test the system has successfully found the player entity.
        assertEquals("Interaction System found player entity", playerEntity, engine.getSystem(PlayerInteractionSystem.class).getPlayerEntity());

        //Test the array Interactables contains the correct Entities.
        assertEquals(1, engine.getSystem(PlayerInteractionSystem.class).getInteractables().size());
        assertEquals("Interactables contains the correct Entities", interactableEntity, engine.getSystem(PlayerInteractionSystem.class).getInteractables().get(0));
    }


}