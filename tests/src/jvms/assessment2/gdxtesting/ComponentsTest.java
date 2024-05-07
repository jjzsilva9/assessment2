package jvms.assessment2.gdxtesting;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.components.*;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.MoveDirection;
import io.github.uoyeng1g6.constants.PlayerConstants;
import io.github.uoyeng1g6.models.GameState;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class ComponentsTest {

    Engine engine;
    GameState gameState;

    Game game;

    @Before
    public void initialiseEngine() {
        engine = new PooledEngine();
        gameState = new GameState();
        game = new HeslingtonHustle();
    }

    @Test
    public void AnimationComponentTest() {

        TextureAtlas playerTextureAtlas = new TextureAtlas(Gdx.files.internal("sprites/player.txt"));

        float spriteScale = 1f;

        AnimationComponent ac = new AnimationComponent(spriteScale);

        ac.animations.put(
                MoveDirection.STATIONARY,
                new Animation<>(1f, playerTextureAtlas.createSprites("stationary"), Animation.PlayMode.LOOP));
        Entity testEntity = engine.createEntity().add(ac);

        assertEquals("Animation component successfully added to entity and engine", ac, testEntity.getComponent(AnimationComponent.class));
    }

    @Test
    public void CounterComponentTest() {

        var daysLabel = new Label("Monday", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        CounterComponent cc = new CounterComponent(daysLabel, new CounterComponent.CounterValueResolver() {
            // spotless:off
            private final Map<Integer, String> dayNameMap = Map.of(
                    7, "Monday", 6, "Tuesday", 5, "Wednesday",
                    4, "Thursday", 3, "Friday", 2, "Saturday",
                    1, "Sunday - Exam Tomorrow"
            );
            // spotless:on

            @Override
            public String resolveValue(GameState gameState) {
                return dayNameMap.get(gameState.daysRemaining);
            }
        });

        Entity testEntity = engine.createEntity().add(cc);

        assertEquals("Counter component successfully added to entity and engine", cc, testEntity.getComponent(CounterComponent.class));
    }

    @Test
    public void FixtureComponentTest() {

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

        FixtureComponent fc = new FixtureComponent(playerFixture);

        Entity testEntity = engine.createEntity().add(fc);

        assertEquals("Fixture component successfully added to entity and engine", fc, testEntity.getComponent(FixtureComponent.class));
    }

    @Test
    public void HitboxComponentTest() {

        HitboxComponent hc = new HitboxComponent(new Rectangle(25, 14, 10f, 10f));

        Entity testEntity = engine.createEntity().add(hc);

        assertEquals("Hitbox component successfully added to entity and engine", hc, testEntity.getComponent(HitboxComponent.class));
    }

    @Test
    public void InteractionComponentTest() {

        InteractionComponent ic = new InteractionComponent(state -> {
            if (!state.doActivity(1, 10, ActivityType.STUDY, "Studying...", "")) {
                // Notify insufficient time/energy
            }});

        Entity testEntity = engine.createEntity().add(ic);

        assertEquals("Interaction component successfully added to entity and engine", ic, testEntity.getComponent(InteractionComponent.class));
    }

    @Test
    public void PlayerComponentTest() {

        PlayerComponent pc = new PlayerComponent();

        Entity testEntity = engine.createEntity().add(pc);

        assertEquals("Player component successfully added to entity and engine", pc, testEntity.getComponent(PlayerComponent.class));
    }

    @Test
    public void PositionComponentTest() {

        PositionComponent pc = new PositionComponent(1f, 1f);

        Entity testEntity = engine.createEntity().add(pc);

        assertEquals("Position component successfully added to entity and engine", pc, testEntity.getComponent(PositionComponent.class));
    }

    @Test
    public void TextureComponentTest() {

        TextureComponent tc = new TextureComponent(new TextureAtlas(Gdx.files.internal("sprites/interaction_icons.txt")).findRegion("book_icon"), 10f);

        Entity testEntity = engine.createEntity().add(tc);

        assertEquals("Texture component successfully added to entity and engine", tc, testEntity.getComponent(TextureComponent.class));
    }

    @Test
    public void TooltipComponentTest() {

        TooltipComponent tc = new TooltipComponent(new BitmapFont(), "tooltip message");
        Entity testEntity = engine.createEntity().add(tc);

        assertEquals("Tooltip component successfully added to entity and engine", tc, testEntity.getComponent(TooltipComponent.class));
    }

}
