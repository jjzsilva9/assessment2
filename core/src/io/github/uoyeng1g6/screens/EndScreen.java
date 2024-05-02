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
import io.github.uoyeng1g6.utils.ChangeListener;
import java.util.Arrays;
import java.util.List;

/**
 * The end screen of the game. Displays the player's score and the total number done of each activity.
 */
public class EndScreen implements Screen {
    /**
     * Theoretical maximum day score. Allows normalising to range 0-100.
     */
    private static final float MAX_DAY_SCORE = 105.125f;
    /**
     * Theoretical minimum day score. Allows normalising to range 0-100.
     */
    private static final float MIN_DAY_SCORE = 0f;

    private int examScore;

    Camera camera;
    /**
     * The {@code scene2d.ui} stage used to render this screen.
     */
    Stage stage;

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

        inner.add(String.format("Exam Score: %.0f / 100", calculateExamScore(endGameState.days)))
                .padBottom(50);
        inner.row();
        inner.add("Times Studied: " + endGameState.getTotalActivityCount(ActivityType.STUDY));
        inner.row();
        inner.add("Meals Eaten: " + endGameState.getTotalActivityCount(ActivityType.MEAL));
        inner.row();
        inner.add("Recreational Activities Done: " + endGameState.getTotalActivityCount(ActivityType.RECREATION))
                .padBottom(50);
        inner.row();
        inner.row();
        List<Boolean> achievements = calculateAchievements(endGameState.days);

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

    /**
     * Calculate the score for a given day based on the number of activities performed. The optimal score
     * is given by studying 5 times, eating 3 times, and doing a recreational activity 3 times.
     *
     * @param studyCount the number of times the player studied during the day.
     * @param mealCount the number of times the player ate during the day.
     * @param recreationCount the number of recreational activities done by the player during the day.
     * @return the computed score given the activity counts.
     */
    float getDayScore(int studyCount, int mealCount, int recreationCount) {
        var studyPoints = 0;
        for (int i = 1; i <= studyCount; i++) {
            studyPoints += i <= 8 ? 10 : -5;
        }
        studyPoints = Math.max(0, studyPoints);

        // Calculate meal multiplier
        float mealMultiplier = 1;
        for (var i = 1; i <= mealCount; i++) {
            mealMultiplier += i <= 3 ? 0.15f : -0.025f;
        }
        mealMultiplier = Math.max(1, mealMultiplier);

        // Calculate recreation multiplier
        float recreationMultiplier = 1;
        for (var i = 1; i <= recreationCount; i++) {
            recreationMultiplier += i <= 5 ? 0.15f : -0.025f;
        }
        recreationMultiplier = Math.max(1, recreationMultiplier);

        // Calculate day score
        return studyPoints * mealMultiplier * recreationMultiplier;
    }

    /**
     * Calculate the aggregate score of all the days.
     *
     * @param days the days to calculate the score for.
     * @return the computed game score.
     */
    public float calculateExamScore(List<GameState.Day> days) {
        float totalScore = 0;

        for (var day : days) {
            int studyCount = day.statFor(ActivityType.STUDY);
            int mealCount = day.statFor(ActivityType.MEAL);
            int recreationCount = day.statFor(ActivityType.RECREATION);

            var dayScore = getDayScore(studyCount, mealCount, recreationCount);
            // Normalise day score between 0 and 100, round up to nearest whole number
            var normalisedDayScore = Math.ceil(((dayScore - MIN_DAY_SCORE) * 100) / (MAX_DAY_SCORE - MIN_DAY_SCORE));

            // Increase total score
            totalScore += (float) (normalisedDayScore * (1 / 7f));
        }

        List<Boolean> achievements = calculateAchievements(days);
        boolean movieAchievement = achievements.get(0);
        boolean townAchievement = achievements.get(1);
        boolean sportAchievement = achievements.get(2);
        boolean studyFailure = achievements.get(3);

        // Add achievement bonuses
        if (movieAchievement) {
            totalScore += 5;
        }
        if (townAchievement) {
            totalScore += 5;
        }
        if (sportAchievement) {
            totalScore += 5;
        }

        if (studyFailure) {
            totalScore = 0;
        }

        // Clamp total score from 0-100
        examScore = Math.round(Math.min(100, Math.max(0, totalScore)));
        return examScore;
    }

    List<Boolean> calculateAchievements(List<GameState.Day> days) {

        // Movie Marathon: For watching 3 movies in a day
        boolean movieAchievement = false;

        // You really went to town on that... : For going to town 5 times in a day
        boolean townAchievement = false;

        // Gymbro : Go to the gym at least once a day, every day
        boolean sportAchievement = true;

        // Failure : You missed a day of study without catching up
        boolean studyFailCheck = false;
        boolean studyFailure = false;

        for (var day : days) {

            if (day.statForName("movie") >= 3 && !movieAchievement) {
                movieAchievement = true;
            }
            if (day.statForName("town") >= 5 && !townAchievement) {
                townAchievement = true;
            }
            if (sportAchievement && day.statForName("sports") == 0) {
                sportAchievement = false;
            }
            if (studyFailCheck && day.statFor(ActivityType.STUDY) < 2) {
                studyFailure = true;
            }
            if (day.statFor(ActivityType.STUDY) == 0 && !studyFailCheck) {
                studyFailCheck = true;
            }
        }

        return Arrays.asList(movieAchievement, townAchievement, sportAchievement, studyFailure);
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
