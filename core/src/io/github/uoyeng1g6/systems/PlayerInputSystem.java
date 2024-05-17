package io.github.uoyeng1g6.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.uoyeng1g6.components.AnimationComponent;
import io.github.uoyeng1g6.components.FixtureComponent;
import io.github.uoyeng1g6.components.PlayerComponent;
import io.github.uoyeng1g6.constants.MoveDirection;
import io.github.uoyeng1g6.constants.PlayerConstants;
import io.github.uoyeng1g6.models.GameState;

/**
 * System to process the player's inputs and set the movement velocity, as well as set a flag
 * on the player component if they are attempting to interact with something.
 */
public class PlayerInputSystem extends EntitySystem {
    /**
     * The game state.
     */
    private final GameState gameState;

    private final ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<FixtureComponent> fm = ComponentMapper.getFor(FixtureComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    private final Vector2 velocity = new Vector2();

    private Entity playerEntity;

    public PlayerInputSystem(GameState gameState) {
        this.gameState = gameState;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public int getCurrentAnimation() {
        return am.get(playerEntity).currentAnimation;
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerEntity = engine.getEntitiesFor(
                        Family.all(PlayerComponent.class, AnimationComponent.class, FixtureComponent.class)
                                .get())
                .first();
    }

    @Override
    public void update(float deltaTime) {
        if (gameState.interactionOverlay != null) {
            // User input is disabled as an interaction is currently happening
            var fixture = fm.get(playerEntity).fixture;
            // Stop the player - prevents bug where player would continue to move if direction
            // key was held at same frame that interaction was triggered
            fixture.getBody().setLinearVelocity(0, 0);
            return;
        }

        velocity.set(0, 0);

        boolean left, right, up, down;
        left = right = up = down = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -PlayerConstants.PLAYER_SPEED;
            left = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = PlayerConstants.PLAYER_SPEED;
            right = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y = PlayerConstants.PLAYER_SPEED;
            up = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y = -PlayerConstants.PLAYER_SPEED;
            down = true;
        }

        if ((left && right) || (!left && !right)) {
            velocity.x = 0;
        }
        if ((up && down) || (!up && !down)) {
            velocity.y = 0;
        }

        var fixture = fm.get(playerEntity).fixture;
        fixture.getBody().setLinearVelocity(velocity);

        var ac = am.get(playerEntity);
        if (velocity.x == 0 && velocity.y == 0) {
            ac.currentAnimation = MoveDirection.STATIONARY;
        } else if (velocity.x != 0 && velocity.y == 0) {
            ac.currentAnimation = velocity.x > 0 ? MoveDirection.RIGHT : MoveDirection.LEFT;
        } else {
            ac.currentAnimation = velocity.y > 0 ? MoveDirection.UP : MoveDirection.DOWN;
        }

        pm.get(playerEntity).isInteracting = Gdx.input.isKeyJustPressed(Input.Keys.E);
    }
}
