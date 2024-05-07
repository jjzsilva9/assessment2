/*
package jvms.assessment2.gdxtesting;

import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import io.github.uoyeng1g6.screens.EndScreen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class EndScreenTest {

    GameState gameState;

    @Before
    public void initialiseGameState() {
        gameState = new GameState();
    }
    @Test
    public void idealScoreTest() {
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=1; i<3; i++) {
                gameState.doActivity(1, 0, ActivityType.MEAL, new String());
            }
            for (int i=1; i<8; i++) {
                gameState.doActivity(1, 0, ActivityType.STUDY, new String());
            }
            for (int i=1; i<3; i++) {
                gameState.doActivity(1, 0, ActivityType.RECREATION, new String());
            }
            gameState.advanceDay();
        }
        EndScreen endScreen = new EndScreen(new HeslingtonHustle(), gameState);
        System.out.println(endScreen.calculateExamScore(gameState.days));
        assertTrue(true);
    }

    @Test
    public void badScoreTest() {
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=1; i<5; i++) {
                gameState.doActivity(1, 0, ActivityType.MEAL, new String());
            }
            for (int i=1; i<2; i++) {
                gameState.doActivity(1, 0, ActivityType.STUDY, new String());
            }
            for (int i=1; i<6; i++) {
                gameState.doActivity(1, 0, ActivityType.RECREATION, new String());
            }
            gameState.advanceDay();
        }
        EndScreen endScreen = new EndScreen(new HeslingtonHustle(), gameState);
        System.out.println(endScreen.calculateExamScore(gameState.days));
        assertTrue(true);
    }

    @Test
    public void midScoreTest() {
        for (int day_count = 1; day_count < 7; day_count++) {
            for (int i=1; i<4; i++) {
                gameState.doActivity(1, 0, ActivityType.MEAL, new String());
            }
            for (int i=1; i<5; i++) {
                gameState.doActivity(1, 0, ActivityType.STUDY, new String());
            }
            for (int i=1; i<4; i++) {
                gameState.doActivity(1, 0, ActivityType.RECREATION, new String());
            }
            gameState.advanceDay();
        }
        EndScreen endScreen = new EndScreen(new HeslingtonHustle(), gameState);
        System.out.println(endScreen.calculateExamScore(gameState.days));
        assertTrue(true);
    }

}
*/
