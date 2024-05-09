package io.github.uoyeng1g6;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.uoyeng1g6.screens.*;
import io.github.uoyeng1g6.utils.LeaderboardManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * The main game class. Handles the switching between different screens as well as most of the asset
 * loading and cleanup that is required for the rest of the game.
 */
public class HeslingtonHustle extends Game {
    /**
     * Enum representing all possible game states.
     */
    public enum State {
        /**
         * The game is currently on the main menu screen.
         */
        MAIN_MENU,
        /**
         * The game is currently being played.
         */
        PLAYING,
        /**
         * The game is currently on the game over screen.
         */
        END_SCREEN,
        /**
         * The game is currently on the player name input screen.
         */
        PLAYER_NAME_INPUT,
        /**
         * The game is currently on the leaderboard screen.
         */
        LEADERBOARD,
        /**
         * The game is going from main menu to leaderboard.
         */
        MAIN_TO_LEADERBOARD
    }

    /**
     * Whether debug mode is enabled - if {@code true} then hitbox outlines will be shown for the player
     * and any interactables, as well as showing layout debug lines for {@code scene2d.ui}.
     */
    public final boolean debug;
    /**
     * Whether physics debug mode is enabled - if {@code true} then hitbox outlines will be shown for any physics
     * objects handled by {@code box2d}.
     */
    public final boolean physicsDebug;

    /**
     * The skin to use when rendering {@code scene2d.ui} components.
     */
    public Skin skin;

    /**
     * Texture atlas containing all images for the player's sprite.
     */
    public TextureAtlas playerTextureAtlas;
    /**
     * Texture atlas containing all icons used to mark interaction locations on the map.
     */
    public TextureAtlas interactionIconsTextureAtlas;
    /**
     * Texture which is just a single white pixel. Required for {@link ShapeDrawer} to work properly.
     */
    public Texture whitePixel;

    /**
     * The tilemap to use for the game's background.
     */
    public TiledMap tiledMap;

    /**
     * The font to use when rendering tooltips.
     */
    public BitmapFont tooltipFont;
    /**
     * The font to use when rendering the screen overlay that is drawn while an interaction is in progress.
     */
    public BitmapFont overlayFont;

    /**
     * The global spritebatch to use for the game.
     */
    public SpriteBatch spriteBatch;
    /**
     * The global shapedrawer to use for the game.
     */
    public ShapeDrawer shapeDrawer;

    /**
     * The main menu screen instance.
     */
    MainMenu mainMenu;
    /**
     * The gameplay screen instance. A new one is required to be created each time the
     * player starts a new game.
     */
    Playing playing = null;
    /**
     * The end screen instance. A new one is required to be created each time the
     * player finishes a game.
     */
    EndScreen endScreen = null;
    /**
     * The name input screen instance. A new one is required to be created each time the
     * player finishes a game.
     */
    PlayerNameInput playerNameInputScreen = null;
    /**
     * The leaderboard instance. A new one is required to be created each time the
     * player finishes a game.
     */
    Leaderboard leaderboard = null;

    /**
     * The game's current state.
     */
    private State currentState = State.MAIN_MENU;

    public HeslingtonHustle() {
        // Properties retrieved from command-line to allow enabling of different debug modes to help with development
        debug = System.getProperty("game.debug", "false").equals("true");
        physicsDebug = System.getProperty("game.physicsDebug", "false").equals("true");
    }

    /**
     * Quit the game to desktop.
     */
    public void quit() {
        Gdx.app.exit();
    }

    /**
     * Set the current game state. Handles switching between the different screens and cleanup between
     * screen changes.
     *
     * @param state the state that the game should transition to.
     */
    public void setState(State state) {
        switch (state) {
            case MAIN_MENU:
                if (mainMenu != null) {
                    mainMenu.dispose();
                }
                mainMenu = new MainMenu(this);
                this.setScreen(mainMenu);
                break;
            case PLAYING:
                if (playing != null) {
                    playing.dispose();
                }
                playing = new Playing(this);
                this.setScreen(playing);
                break;
            case END_SCREEN:
                if (endScreen != null) {
                    endScreen.dispose();
                }
                endScreen = new EndScreen(this, playing.getGameState());
                this.setScreen(endScreen);
                break;
            case PLAYER_NAME_INPUT:
                if (playerNameInputScreen != null) {
                    playerNameInputScreen.dispose();
                }
                playerNameInputScreen = new PlayerNameInput(this);
                this.setScreen(playerNameInputScreen);
                break;
            case LEADERBOARD:
                if (leaderboard != null) {
                    leaderboard.dispose();
                }
                leaderboard =
                        new Leaderboard(this, playerNameInputScreen.getLeaderboardManager(), endScreen.getExamScore());
                this.setScreen(leaderboard);
                break;
            case MAIN_TO_LEADERBOARD:
                if (leaderboard != null) {
                    leaderboard.dispose();
                }
                leaderboard = new Leaderboard(this, new LeaderboardManager("scores.txt"), -1);
                this.setScreen(leaderboard);
                break;
        }
        currentState = state;
    }

    @Override
    public void create() {
        Box2D.init();

        skin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));

        playerTextureAtlas = new TextureAtlas(Gdx.files.internal("sprites/player.txt"));
        interactionIconsTextureAtlas = new TextureAtlas(Gdx.files.internal("sprites/interaction_icons.txt"));

        tiledMap = new TmxMapLoader().load("maps/campus-east.tmx");

        tooltipFont = new BitmapFont();
        tooltipFont.getData().setScale(0.07f);
        tooltipFont.setUseIntegerPositions(false);
        tooltipFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tooltipFont.setColor(Color.BLACK);

        overlayFont = new BitmapFont();
        overlayFont.getData().setScale(0.2f);
        overlayFont.setUseIntegerPositions(false);
        overlayFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        overlayFont.setColor(Color.WHITE);

        spriteBatch = new SpriteBatch();
        whitePixel = new Texture(Gdx.files.internal("white_pixel.png"));
        shapeDrawer = new ShapeDrawer(spriteBatch, new TextureRegion(whitePixel, 0, 0, 1, 1));

        mainMenu = new MainMenu(this);

        this.setScreen(mainMenu);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        mainMenu.dispose();

        if (playing != null) {
            playing.dispose();
        }
        if (endScreen != null) {
            endScreen.dispose();
        }

        skin.dispose();

        playerTextureAtlas.dispose();
        interactionIconsTextureAtlas.dispose();
        whitePixel.dispose();

        tiledMap.dispose();

        tooltipFont.dispose();
        spriteBatch.dispose();
    }
}
