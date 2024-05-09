package io.github.uoyeng1g6.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.components.AnimationComponent;
import io.github.uoyeng1g6.components.CounterComponent;
import io.github.uoyeng1g6.components.FixtureComponent;
import io.github.uoyeng1g6.components.HitboxComponent;
import io.github.uoyeng1g6.components.InteractionComponent;
import io.github.uoyeng1g6.components.PlayerComponent;
import io.github.uoyeng1g6.components.PositionComponent;
import io.github.uoyeng1g6.components.TextureComponent;
import io.github.uoyeng1g6.components.TooltipComponent;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.constants.MoveDirection;
import io.github.uoyeng1g6.constants.PlayerConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.models.PhysicsPolygon;
import io.github.uoyeng1g6.systems.AnimationSystem;
import io.github.uoyeng1g6.systems.CounterUpdateSystem;
import io.github.uoyeng1g6.systems.DebugSystem;
import io.github.uoyeng1g6.systems.InteractionOverlayRenderingSystem;
import io.github.uoyeng1g6.systems.MapRenderingSystem;
import io.github.uoyeng1g6.systems.PlayerInputSystem;
import io.github.uoyeng1g6.systems.PlayerInteractionSystem;
import io.github.uoyeng1g6.systems.StaticRenderingSystem;
import io.github.uoyeng1g6.systems.TooltipRenderingSystem;
import java.util.Map;

/**
 * The gameplay screen for the game. Causes a transition to the end screen once all the in-game
 * days have been completed.
 */
public class Playing implements Screen {
    final HeslingtonHustle game;

    final OrthographicCamera camera;
    final Viewport viewport;

    /**
     * The {@code scene2d.ui} stage used to render the ui overlay for this screen.
     */
    Stage stage;

    /**
     * The engine used to handle system updating.
     */
    Engine engine;
    /**
     * The current game state;
     */
    GameState gameState;
    /**
     * The box2d world used for the physics system.
     */
    World world;

    /**
     * The box2d debug renderer used when the game is running in physics debug mode.
     */
    Box2DDebugRenderer debugRenderer = null;

    public Playing(HeslingtonHustle game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        viewport = new FitViewport(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT, camera);

        stage = new Stage(viewport);

        var labelStyle = new Label.LabelStyle(game.tooltipFont, Color.BLACK);

        var uiTop = new Table();
        uiTop.setFillParent(true);
        uiTop.setDebug(game.debug);
        stage.addActor(uiTop);
        uiTop.center().top();

        var daysLabel = new Label("Monday", labelStyle);
        daysLabel.setFontScale(0.17f);
        uiTop.add(daysLabel);
        uiTop.row();
        var timeLabel = new Label("07:00", labelStyle);
        timeLabel.setFontScale(0.17f);
        uiTop.add(timeLabel);

        var counters = new Table(game.skin);
        counters.setFillParent(true);
        counters.pad(1);
        counters.setDebug(game.debug);
        stage.addActor(counters);

        var studyIcon = game.interactionIconsTextureAtlas.findRegion("book_icon");
        var eatIcon = game.interactionIconsTextureAtlas.findRegion("food_icon");
        var recreationIcon = game.interactionIconsTextureAtlas.findRegion("popcorn_icon");
        var studyImage = new Image(studyIcon);
        var eatImage = new Image(eatIcon);
        var recreationImage = new Image(recreationIcon);

        var todayLabel = new Label("Today:", labelStyle);
        todayLabel.setFontScale(0.08f);
        var totalLabel = new Label("Total:", labelStyle);
        totalLabel.setFontScale(0.08f);

        var dayStudyLabel = new Label("0", labelStyle);
        dayStudyLabel.setFontScale(0.15f);
        var totalStudyLabel = new Label("0", labelStyle);
        totalStudyLabel.setFontScale(0.15f);

        var dayEatLabel = new Label("0", labelStyle);
        dayEatLabel.setFontScale(0.15f);
        var totalEatLabel = new Label("0", labelStyle);
        totalEatLabel.setFontScale(0.15f);

        var dayRecreationLabel = new Label("0", labelStyle);
        dayRecreationLabel.setFontScale(0.15f);
        var totalRecreationLabel = new Label("0", labelStyle);
        totalRecreationLabel.setFontScale(0.15f);

        counters.top().right();
        counters.add();
        counters.add(todayLabel).padRight(0.5f);
        counters.add(totalLabel);
        counters.row();
        counters.add(studyImage).width(3).height(3).padRight(0.25f);
        counters.add(dayStudyLabel);
        counters.add(totalStudyLabel);
        counters.row();
        counters.add(eatImage).width(3).height(3).padRight(0.25f);
        counters.add(dayEatLabel);
        counters.add(totalEatLabel);
        counters.row();
        counters.add(recreationImage).width(3).height(3).padRight(0.25f);
        counters.add(dayRecreationLabel);
        counters.add(totalRecreationLabel);

        var energy = new Table(game.skin);
        energy.setFillParent(true);
        energy.pad(1);
        energy.setDebug(game.debug);
        stage.addActor(energy);

        var energyLabel = new Label("Energy Remaining:", labelStyle);
        energyLabel.setFontScale(0.08f);
        var energyAmount = new Label(String.valueOf(GameConstants.MAX_ENERGY), labelStyle);
        energyAmount.setFontScale(0.15f);

        energy.top().left();
        energy.add(energyLabel);
        energy.row();
        energy.add(energyAmount);

        this.engine = new PooledEngine();
        this.gameState = new GameState();
        this.world = new World(new Vector2(), true);

        initTerrain();

        engine.addEntity(initPlayerEntity(engine));

        for (var entity : initInteractionLocations(engine)) {
            engine.addEntity(entity);
        }

        engine.addEntity(
                engine.createEntity().add(new CounterComponent(daysLabel, new CounterComponent.CounterValueResolver() {
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
                })));
        engine.addEntity(engine.createEntity().add(new CounterComponent(timeLabel, state -> {
            var newHour = 7 + (GameConstants.MAX_HOURS - state.hoursRemaining);
            return String.format("%s%d:00", newHour < 10 ? "0" : "", newHour);
        })));

        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        dayStudyLabel, state -> String.valueOf(state.currentDay.statFor(ActivityType.STUDY)))));
        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        dayEatLabel, state -> String.valueOf(state.currentDay.statFor(ActivityType.MEAL)))));
        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        dayRecreationLabel,
                        state -> String.valueOf(state.currentDay.statFor(ActivityType.RECREATION)))));

        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        totalStudyLabel, state -> String.valueOf(state.getTotalActivityCount(ActivityType.STUDY)))));
        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        totalEatLabel, state -> String.valueOf(state.getTotalActivityCount(ActivityType.MEAL)))));
        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(
                        totalRecreationLabel,
                        state -> String.valueOf(state.getTotalActivityCount(ActivityType.RECREATION)))));

        engine.addEntity(engine.createEntity()
                .add(new CounterComponent(energyAmount, state -> String.valueOf(state.energyRemaining))));

        engine.addSystem(new PlayerInputSystem(gameState));
        engine.addSystem(new PlayerInteractionSystem(gameState));
        engine.addSystem(new MapRenderingSystem(game.tiledMap, camera));
        engine.addSystem(new StaticRenderingSystem(game.spriteBatch));
        engine.addSystem(new AnimationSystem(game.spriteBatch, gameState));
        engine.addSystem(new TooltipRenderingSystem(game.tooltipFont, game.shapeDrawer, game.spriteBatch, gameState));
        engine.addSystem(new CounterUpdateSystem(gameState));
        if (game.debug) {
            engine.addSystem(new DebugSystem(game.shapeDrawer));
        }
        engine.addSystem(
                new InteractionOverlayRenderingSystem(game.spriteBatch, game.overlayFont, game.shapeDrawer, gameState));

        if (game.physicsDebug) {
            debugRenderer = new Box2DDebugRenderer();
        }
    }

    /**
     * Get the current game state object.
     *
     * @return the game state for this playthrough.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Load the terrain polygons from the data json file and create them in the box2d world.
     */
    void initTerrain() {
        var json = new Json();
        var objects = json.fromJson(PhysicsPolygon[].class, Gdx.files.internal("terrain.json"));

        for (var object : objects) {
            var bodyDef = new BodyDef();
            bodyDef.type = object.getType();
            bodyDef.position.set(object.getPosition());

            var body = world.createBody(bodyDef);
            var shape = new PolygonShape();
            shape.set(object.getVertices());

            // We know that these will always be static bodies so will always have a density of 0
            body.createFixture(shape, 0f);
            shape.dispose();
        }
    }

    /**
     * Initialise the entities for the interaction locations on the map
     *
     * @param engine the engine to create the entities for.
     * @return the created entities.
     */
    Entity[] initInteractionLocations(Engine engine) {
        final var iconSize = 2 / 64f;

        var studyIcon = game.interactionIconsTextureAtlas.findRegion("book_icon");
        var cs = engine.createEntity()
                .add(new TextureComponent(studyIcon, iconSize).show())
                .add(new PositionComponent(25, 14))
                .add(new HitboxComponent(new Rectangle(
                        25, 14, studyIcon.getRegionWidth() * iconSize, studyIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 6, ActivityType.STUDY, "Studying...", "cs")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Study for exams\nTime: -1h\nEnergy: -6"));

        var library = engine.createEntity()
                .add(new TextureComponent(studyIcon, iconSize).show())
                .add(new PositionComponent(72.5f, 21.5f))
                .add(new HitboxComponent(new Rectangle(
                        72.5f, 21.5f, studyIcon.getRegionWidth() * iconSize, studyIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 6, ActivityType.STUDY, "Studying...", "library")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Study for exams\nTime: -1h\nEnergy: -6"));

        var foodIcon = game.interactionIconsTextureAtlas.findRegion("food_icon");
        var food = engine.createEntity()
                .add(new TextureComponent(foodIcon, iconSize).show())
                .add(new PositionComponent(54, 2.5f))
                .add(new HitboxComponent(new Rectangle(
                        54, 2.5f, foodIcon.getRegionWidth() * iconSize, foodIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 5, ActivityType.MEAL, "Eating...", "food")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Eat at Piazza\nTime: -1h\nEnergy: -5"));

        var popcornIcon = game.interactionIconsTextureAtlas.findRegion("popcorn_icon");
        var recreation = engine.createEntity()
                .add(new TextureComponent(popcornIcon, iconSize).show())
                .add(new PositionComponent(19, 60.5f))
                .add(new HitboxComponent(new Rectangle(
                        19, 60.5f, popcornIcon.getRegionWidth() * iconSize, popcornIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(2, 14, ActivityType.RECREATION, "Watching films...", "movie")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Watch films with mates\nTime: -2h\nEnergy: -14"));

        var footballIcon = game.interactionIconsTextureAtlas.findRegion("football_icon");
        var sports = engine.createEntity()
                .add(new TextureComponent(footballIcon, iconSize).show())
                .add(new PositionComponent(65.5f, 57.5f))
                .add(new HitboxComponent(new Rectangle(
                        65.5f,
                        57.5f,
                        footballIcon.getRegionWidth() * iconSize,
                        footballIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(2, 16, ActivityType.RECREATION, "Playing sports...", "sports")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Play some sports\nTime: -2h\nEnergy: -16"));

        var gooseIcon = game.interactionIconsTextureAtlas.findRegion("goose_icon");
        var ducks = engine.createEntity()
                .add(new TextureComponent(gooseIcon, iconSize).show())
                .add(new PositionComponent(33.5f, 5.5f))
                .add(new HitboxComponent(new Rectangle(
                        33.5f, 5.5f, gooseIcon.getRegionWidth() * iconSize, gooseIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 6, ActivityType.RECREATION, "Feeding ducks...", "ducks")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Feed some ducks\nTime: -1h\nEnergy: -6"));

        var sofaIcon = game.interactionIconsTextureAtlas.findRegion("sofa_icon");
        var chill = engine.createEntity()
                .add(new TextureComponent(sofaIcon, iconSize).show())
                .add(new PositionComponent(40.5f, 27.5f))
                .add(new HitboxComponent(new Rectangle(
                        40.5f, 27.5f, sofaIcon.getRegionWidth() * iconSize, sofaIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 6, ActivityType.RECREATION, "Relaxing...", "chill")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Relax with friends\nTime: -1h\nEnergy: -6"));

        var busIcon = game.interactionIconsTextureAtlas.findRegion("bus_icon");
        var town = engine.createEntity()
                .add(new TextureComponent(busIcon, iconSize).show())
                .add(new PositionComponent(12, 60.5f))
                .add(new HitboxComponent(new Rectangle(
                        12, 60.5f, busIcon.getRegionWidth() * iconSize, busIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 7, ActivityType.RECREATION, "Travelling...", "town")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Spend time in town\nTime: -1h\nEnergy: -7"));

        var society = engine.createEntity()
                .add(new TextureComponent(sofaIcon, iconSize).show())
                .add(new PositionComponent(60.5f, 19.5f))
                .add(new HitboxComponent(new Rectangle(
                        60.5f, 19.5f, sofaIcon.getRegionWidth() * iconSize, sofaIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(state -> {
                    if (!state.doActivity(1, 5, ActivityType.RECREATION, "Socialising...", "society")) {
                        // Notify insufficient time/energy
                    }
                }))
                .add(new TooltipComponent(game.tooltipFont, "[E] Go to your society\nTime: -1h\nEnergy: -5"));

        var sleepIcon = game.interactionIconsTextureAtlas.findRegion("bed_icon");
        var sleep = engine.createEntity()
                .add(new TextureComponent(sleepIcon, iconSize).show())
                .add(new PositionComponent(3.5f, 26.5f))
                .add(new HitboxComponent(new Rectangle(
                        3.5f, 26.5f, sleepIcon.getRegionWidth() * iconSize, sleepIcon.getRegionHeight() * iconSize)))
                .add(new InteractionComponent(GameState::advanceDay))
                .add(new TooltipComponent(game.tooltipFont, "[E] Go to sleep\nEnds the current day"));

        return new Entity[] {cs, library, food, recreation, sleep, sports, ducks, chill, society, town};
    }

    /**
     * Initialise the player's physics object in the box2d world.
     *
     * @return the fixture for the player's physics object.
     */
    Fixture initPlayerBody() {
        var player = new BodyDef();
        player.type = BodyDef.BodyType.DynamicBody;
        player.position.set(PlayerConstants.START_POSITION);
        var playerBody = world.createBody(player);
        playerBody.setUserData(PlayerConstants.HITBOX_RADIUS);
        var playerCircle = new CircleShape();
        playerCircle.setRadius(PlayerConstants.HITBOX_RADIUS);
        var playerFixture = playerBody.createFixture(playerCircle, 1f);
        playerCircle.dispose();

        return playerFixture;
    }

    /**
     * Initialise the entity that represents the player character.
     *
     * @param engine the engine to create the entity for.
     * @return the created entity.
     */
    Entity initPlayerEntity(Engine engine) {
        var playerAnimations = new AnimationComponent(0.05f);
        playerAnimations.animations.put(
                MoveDirection.STATIONARY,
                new Animation<>(1f, game.playerTextureAtlas.createSprites("stationary"), Animation.PlayMode.LOOP));
        playerAnimations.animations.put(
                MoveDirection.UP,
                new Animation<>(0.12f, game.playerTextureAtlas.createSprites("walk_up"), Animation.PlayMode.LOOP));
        playerAnimations.animations.put(
                MoveDirection.DOWN,
                new Animation<>(0.12f, game.playerTextureAtlas.createSprites("walk_down"), Animation.PlayMode.LOOP));
        playerAnimations.animations.put(
                MoveDirection.LEFT,
                new Animation<>(0.12f, game.playerTextureAtlas.createSprites("walk_left"), Animation.PlayMode.LOOP));
        playerAnimations.animations.put(
                MoveDirection.RIGHT,
                new Animation<>(0.12f, game.playerTextureAtlas.createSprites("walk_right"), Animation.PlayMode.LOOP));

        return engine.createEntity()
                .add(new PlayerComponent())
                .add(playerAnimations)
                .add(new FixtureComponent(initPlayerBody()));
    }

    @Override
    public void render(float delta) {
        // Allow the final interaction (day transition) to complete before showing the end screen
        if (gameState.daysRemaining == 0 && gameState.interactionOverlay == null) {
            game.setState(HeslingtonHustle.State.END_SCREEN);
            return;
        }

        ScreenUtils.clear(0, 0, 0, 1);

        // This makes transpacency work properly
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        game.spriteBatch.setProjectionMatrix(camera.combined);
        game.spriteBatch.begin();

        engine.update(delta);
        if (game.physicsDebug) {
            debugRenderer.render(world, camera.combined);
        }
        game.spriteBatch.end();

        stage.act();
        stage.draw();

        world.step(delta, 8, 3);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
    }
}
