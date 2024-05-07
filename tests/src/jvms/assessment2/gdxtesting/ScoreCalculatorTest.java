package jvms.assessment2.gdxtesting;

import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.models.ScoreCalculator;
import io.github.uoyeng1g6.screens.EndScreen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ScoreCalculatorTest {

    GameState gameState;

    private void doActivities(int meal_count, int study_count, int recreation_count) {
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=0; i<meal_count; i++) {
                gameState.doActivity(1, 0, ActivityType.MEAL, "", "");
            }
            for (int i=0; i<study_count; i++) {
                gameState.doActivity(1, 0, ActivityType.STUDY, "", "");
            }
            for (int i=0; i<recreation_count; i++) {
                gameState.doActivity(1, 0, ActivityType.RECREATION, "", "");
            }
            gameState.advanceDay();
        }
    }

    @Before
    public void initialiseGameState() {
        gameState = new GameState();
    }

    @Test
    public void idealScoreTest() {
        //3 Meal, 8 Study, 3 Recreation
        doActivities(3, 8, 3);

        int score = ScoreCalculator.calculateExamScore(gameState.days);

        //Testing the score you get is high if you play in the intended way.
        assertTrue(score >= 90 && score <= 100);
    }

    @Test
    public void midScoreTest() {
        //1 Meal, 4 Study, 2 Recreation
        doActivities(1, 4, 2);
        int score = ScoreCalculator.calculateExamScore(gameState.days);

        //Testing the score you get is mid if you don't play in the intended way but you still do some study.
        assertTrue(score >= 45 && score <= 55);
    }

    @Test
    public void badScoreTest() {
        //4 Meal, 1 Study, 0 Recreation
        doActivities(4, 1, 0);
        int score = ScoreCalculator.calculateExamScore(gameState.days);

        System.out.println(score);
        //Testing the score you get is low if you don't play in the intended way.
        assertTrue(score >= 5 && score <= 15);
    }

    @Test
    public void zeroStudyTest() {
        //3 Meal, 0 Study, 4 Recreation
        doActivities(3, 0 ,4);
        int score = ScoreCalculator.calculateExamScore(gameState.days);

        //Testing the score you get is zero if you don't study every day.
        assertTrue(score == 0);
    }

    @Test
    public void noStudyCatchupScoreTest() {
        //5 Meal, 6 Study, 1 Recreation
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=1; i<5; i++) {
                gameState.doActivity(1, 0, ActivityType.MEAL, "", "");
            }
            if (day_count != 2 && day_count != 3) {
                for (int i=1; i<4; i++) {
                    gameState.doActivity(1, 0, ActivityType.STUDY, "", "");
                }
            }
            for (int i=1; i<1; i++) {
                gameState.doActivity(1, 0, ActivityType.RECREATION, "", "");
            }
            gameState.advanceDay();
        }
        int score = ScoreCalculator.calculateExamScore(gameState.days);

        //Testing the score you get is 0 if you don't catch up on study.
        assertTrue(score == 0);
    }

    @Test
    public void MovieAchievementTest() {

        //Watching a movie three times in one day.
        for (int day_count = 1; day_count < 7; day_count++) {
            if (day_count == 2) {
                for (int i=0; i<3; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "movie");
                }
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you get the movie achievement under the correct conditions.
        assertTrue((Arrays.asList(true, false, false, true)).equals(achievements));
    }

    @Test
    public void NoMovieAchievementTest() {

        //Watching a movie two times in one day.
        for (int day_count = 1; day_count < 7; day_count++) {
            if (day_count == 2) {
                for (int i=0; i<2; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "movie");
                }
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you don't get the movie achievement under the incorrect conditions.
        assertTrue((Arrays.asList(false, false, false, true)).equals(achievements));
    }

    @Test
    public void TownAchievementTest() {

        //Going to town five times in one day.
        for (int day_count = 1; day_count < 7; day_count++) {
            if (day_count == 2) {
                for (int i=0; i<5; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "town");
                }
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you get the town achievement under the correct conditions.
        assertTrue((Arrays.asList(false, true, false, true)).equals(achievements));
    }

    @Test
    public void NoTownAchievementTest() {

        //Going to town four times in one day.
        for (int day_count = 1; day_count < 7; day_count++) {
            if (day_count == 2) {
                for (int i=0; i<4; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "town");
                }
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you don't get the town achievement under the incorrect conditions.
        assertTrue((Arrays.asList(false, false, false, true)).equals(achievements));
    }

    @Test
    public void SportsAchievementTest() {

        //Doing sports 1 time every day.
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=0; i<1; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "sports");
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you get the town achievement under the correct conditions.
        assertTrue((Arrays.asList(false, false, true, true)).equals(achievements));
    }

    @Test
    public void NoSportsAchievementTest() {

        //Doing sports 1 time every day except for one day.
        for (int day_count = 1; day_count < 7; day_count++) {
            if (day_count != 2) {
                for (int i=0; i<1; i++) {
                    gameState.doActivity(1, 0, ActivityType.RECREATION, "", "sports");
                }
            }
            gameState.advanceDay();
        }

        List<Boolean> achievements = ScoreCalculator.calculateAchievements(gameState.days);

        //Testing you get the town achievement under the correct conditions.
        assertTrue((Arrays.asList(false, false, false, true)).equals(achievements));
    }

}
