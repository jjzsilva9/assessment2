package io.github.uoyeng1g6.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.models.ScoreCalculator;
import io.github.uoyeng1g6.utils.ChangeListener;
import java.util.List;

/**
 * The end screen of the game. Displays the player's score and the total number done of each activity.
 */
public class EndScreen implements Screen {

    Camera camera;
    /**
     * The {@code scene2d.ui} stage used to render this screen.
     */
    Stage stage;

    int examScore;

    public EndScreen(HeslingtonHustle game, GameState endGameState) {
        camera = new OrthographicCamera();
        var viewport = new FitViewport(GameConstants.WORLD_WIDTH * 10, GameConstants.WORLD_HEIGHT * 10, camera);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        var root = new Table(game.skin);
        root.setFillParent(true);
        root.pad(0.25f);

        root.setDebug(game.debug);
        stage.addActor(root);

        root.add("Game Over").getActor().setFontScale(2);
        root.row();

        var inner = new Table(game.skin);

        examScore = ScoreCalculator.calculateExamScore(endGameState.days);

        inner.add(String.format("Exam Score: %d / 100", examScore)).padBottom(50);
        inner.row();
        inner.add("Times Studied: " + endGameState.getTotalActivityCount(ActivityType.STUDY));
        inner.row();
        inner.add("Meals Eaten: " + endGameState.getTotalActivityCount(ActivityType.MEAL));
        inner.row();
        inner.add("Recreational Activities Done: " + endGameState.getTotalActivityCount(ActivityType.RECREATION))
                .padBottom(50);
        inner.row();
        inner.row();
        List<Boolean> achievements = ScoreCalculator.calculateAchievements(endGameState.days);

        // If failed due to not catching up study, replace achievements section with failure
        if (achievements.get(3)) {
            inner.add("Failed").padBottom(20);
            inner.row();
            inner.add("You missed a day of study and didn't catch up :(");
            inner.row();
        } else {
            inner.add("Achievements").padBottom(20);
            inner.row();
            if (achievements.get(0)) {
                inner.add("Movie Marathon: Watch at least 3 movies in a single day +5");
                inner.row();
            }
            if (achievements.get(1)) {
                inner.add("You really went to town on that...: Go to town at least 5 times in a single day +5");
                inner.row();
            }
            if (achievements.get(2)) {
                inner.add("Gymbro: Go to the gym at least once a day, every day +5");
                inner.row();
            }
            if (!(achievements.get(0) || achievements.get(1) || achievements.get(2))) {
                inner.add("You found no achievements.");
                inner.row();
            }
        }

        var nextButton = new TextButton("Next", game.skin);
        nextButton.addListener(ChangeListener.of((e, a) -> game.setState(HeslingtonHustle.State.PLAYER_NAME_INPUT)));
        inner.add(nextButton)
                .padTop(50)
                .width(Value.percentWidth(0.4f, inner))
                .height(Value.percentHeight(0.1f, inner));

        inner.row();

        root.add(inner).grow();
    }

    public int getExamScore() {
        return examScore;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

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
