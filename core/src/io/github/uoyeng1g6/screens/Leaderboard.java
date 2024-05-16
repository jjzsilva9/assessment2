package io.github.uoyeng1g6.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.utils.ChangeListener;
import io.github.uoyeng1g6.utils.LeaderboardManager;

public class Leaderboard implements Screen {
    /**
     * The {@code scene2d.ui} stage used to render this screen.
     */
    Stage stage;

    public Leaderboard(HeslingtonHustle game, LeaderboardManager lm, int score) {
        var camera = new OrthographicCamera();
        var viewport = new FitViewport(GameConstants.WORLD_WIDTH * 10, GameConstants.WORLD_HEIGHT * 10, camera);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        var root = new Table(game.skin);
        root.setFillParent(true);
        root.pad(0.25f);

        root.setDebug(game.debug);
        stage.addActor(root);

        root.add("Leaderboard").getActor().setFontScale(2);
        root.row();

        var inner = new Table(game.skin);

        // If they have just finished playing, add their score
        if (score >= 0) {
            lm.addEntry(score);
        }

        // Display the leaderboard from lm
        String[][] rankings = lm.getRanking();
        var leaderboard = new Table(game.skin);
        leaderboard.add("Rank").padRight(20);
        leaderboard.add("Player").pad(0, 20, 0, 20);
        leaderboard.add("Score").padLeft(20);

        for (int i = 0; i < rankings.length; i++) {
            leaderboard.row();
            leaderboard.add(Integer.toString(i + 1)).padRight(20);
            leaderboard.add(rankings[i][0]).pad(0, 20, 0, 20);
            leaderboard.add(rankings[i][1]).padLeft(20);
        }

        root.add(leaderboard);

        root.row();

        var menuButton = new TextButton("Main Menu", game.skin);
        menuButton.addListener(ChangeListener.of((e, a) -> game.setState(HeslingtonHustle.State.MAIN_MENU)));
        inner.add(menuButton).pad(10).width(Value.percentWidth(0.4f, inner)).height(Value.percentHeight(0.1f, inner));

        inner.row();

        var quitButton = new TextButton("Quit", game.skin);
        quitButton.addListener(ChangeListener.of((e, a) -> game.quit()));
        inner.add(quitButton).pad(10).width(Value.percentWidth(0.4f, inner)).height(Value.percentHeight(0.1f, inner));

        root.add(inner).grow();
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act();
        stage.draw();
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
}
