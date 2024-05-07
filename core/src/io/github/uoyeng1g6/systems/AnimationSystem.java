package io.github.uoyeng1g6.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.uoyeng1g6.components.AnimationComponent;
import io.github.uoyeng1g6.components.FixtureComponent;
import io.github.uoyeng1g6.components.PositionComponent;
import io.github.uoyeng1g6.models.GameState;

/**
 * System that draws animated entities. In order for an entity to be animate-able it must have an
 * {@link AnimationComponent}, as well as either a {@link FixtureComponent} or {@link PositionComponent}.
 */
public class AnimationSystem extends IteratingSystem {
    /**
     * The sprite batch to use to draw the animation frames.
     */
    private final SpriteBatch batch;
    /**
     * The game state.
     */
    private final GameState gameState;

    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<FixtureComponent> fm = ComponentMapper.getFor(FixtureComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public AnimationSystem(SpriteBatch batch, GameState gameState) {
        super(Family.all(AnimationComponent.class)
                .one(PositionComponent.class, FixtureComponent.class)
                .get());

        this.batch = batch;
        this.gameState = gameState;
    }

    public ComponentMapper<AnimationComponent> getAm() {
        return am;
    }

    @Override
    public void update(float deltaTime) {
        if (gameState.interactionOverlay != null) {
            // Don't show animations beneath interaction overlay
            return;
        }

        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var ac = am.get(entity);

        if (ac.currentAnimation == -1) {
            // This item is currently invisible
            return;
        }

        var animation = ac.animations.get(ac.currentAnimation);
        if (animation == null) {
            throw new IllegalStateException("current animation does not map to an animation");
        }

        ac.time += deltaTime;
        var sprite = animation.getKeyFrame(ac.time);

        var pc = pm.get(entity);
        if (pc != null) {
            batch.draw(sprite, pc.x, pc.y, sprite.getWidth() * ac.spriteScale, sprite.getHeight() * ac.spriteScale);
            return;
        }

        var fc = fm.get(entity);
        if (fc != null) {
            var body = fc.fixture.getBody();
            batch.draw(
                    sprite,
                    body.getPosition().x - (float) body.getUserData(),
                    body.getPosition().y - (float) body.getUserData(),
                    sprite.getWidth() * ac.spriteScale,
                    sprite.getHeight() * ac.spriteScale);
        }
    }
}
