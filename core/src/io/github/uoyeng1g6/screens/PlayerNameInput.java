package io.github.uoyeng1g6.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.utils.ChangeListener;
import io.github.uoyeng1g6.utils.LeaderboardManager;
import java.util.Objects;

public class PlayerNameInput implements Screen {
    /**
     * The {@code scene2d.ui} stage used to render this screen.
     */
    Stage stage;

    private TextField playerNameField; // New field for player name input

    LeaderboardManager lm;

    public PlayerNameInput(HeslingtonHustle game) {
        var camera = new OrthographicCamera();
        var viewport = new FitViewport(GameConstants.WORLD_WIDTH * 10, GameConstants.WORLD_HEIGHT * 10, camera);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        var root = new Table(game.skin);
        root.setFillParent(true);
        root.pad(0.25f);

        root.setDebug(game.debug);
        stage.addActor(root);

        // root.add("Leaderboard").getActor().setFontScale(2);
        root.row();

        lm = new LeaderboardManager("scores.txt");

        var inner = new Table(game.skin);

        playerNameField = new TextField("", game.skin); // Initialize with an empty string
        playerNameField.setMessageText("Enter your name"); // Display a placeholder text
        inner.add(playerNameField)
                .pad(10)
                .width(Value.percentWidth(0.4f, inner))
                .height(Value.percentHeight(0.1f, inner)); // Add the text field to the stage

        inner.row();

        var leaderboardButton = new TextButton("Leaderboard", game.skin);
        leaderboardButton.addListener(ChangeListener.of((e, a) -> game.setState(HeslingtonHustle.State.LEADERBOARD)));
        inner.add(leaderboardButton)
                .pad(10)
                .width(Value.percentWidth(0.4f, inner))
                .height(Value.percentHeight(0.1f, inner));

        root.add(inner).grow();
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act();
        stage.draw();

        String playerName = playerNameField.getText();
        if (playerName.length() > 14) {
            playerName = playerName.substring(0, 14);
        }
        playerName = playerName.replace(",", "");
        if (!Objects.equals(playerNameField.getText(), playerName)) {
            playerNameField.setText(playerName);
            playerNameField.setCursorPosition(playerName.length());
        }

        if (!Objects.equals(playerName, "")) {
            lm.saveName(playerName);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
    }

    public LeaderboardManager getLeaderboardManager() {
        return lm;
    }
}
