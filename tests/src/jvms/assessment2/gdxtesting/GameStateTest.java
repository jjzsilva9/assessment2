package jvms.assessment2.gdxtesting;

import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class GameStateTest {
    /*
    * Test that stats begin at maximum and that day advances and the stats are updated
    * */
    @Test
    public void testAdvanceDay() {
        GameState gameState = new GameState();
        //Test starting values are as expected for a new GameState
        assertEquals(7, gameState.daysRemaining);
        assertEquals(GameConstants.MAX_ENERGY, gameState.energyRemaining);
        assertEquals(GameConstants.MAX_HOURS, gameState.hoursRemaining);
        assertEquals(new ArrayList<GameState.Day>(7), gameState.days);
        //Test values after day has advanced
        gameState.advanceDay();
        assertEquals(6, gameState.daysRemaining);
        assertEquals(GameConstants.MAX_ENERGY, gameState.energyRemaining);
        assertEquals(GameConstants.MAX_HOURS, gameState.hoursRemaining);
        assertNotSame(new ArrayList<>(7), gameState.days);
        assertNotNull(gameState.interactionOverlay);
    }

    /*
    * Test that activities complete with sufficient energy remaining and related values
    * are updated correctly.
    * */
    @Test
    public void testDoActivity() {
        GameState gameState = new GameState();
        int timeToUse = 1;
        int energyToUse = 10;
        ActivityType activityType = ActivityType.MEAL;
        String overlayText = "Text";
        String activityName = "food";
        int beforeEnergy = gameState.energyRemaining;
        int beforeHoursRemaining = gameState.hoursRemaining;
        GameState.Day currentDay = gameState.currentDay;
        boolean result = gameState.doActivity(timeToUse, energyToUse, activityType, overlayText, activityName);
        //Test that activity completes and that values are as expected after completion
        assertTrue(result);
        assertEquals(beforeEnergy - energyToUse, gameState.energyRemaining);
        assertEquals(beforeHoursRemaining - timeToUse, gameState.hoursRemaining);
        currentDay.activityStats.merge(activityType, 1, Integer::sum);
        assertEquals(currentDay, gameState.currentDay);
        //Test that activity completion fails when energy is insufficient
        gameState.energyRemaining = 5;
        boolean result2 = gameState.doActivity(timeToUse, energyToUse, activityType, overlayText, activityName);
        assertFalse(result2);
    }

    /*
    * Test that activity count is incremented for each activity type
    * */
    @Test
    public void testGetTotalActivityCount() {
        GameState gameState = new GameState();
        for(ActivityType activityType : ActivityType.values()) {
            assertEquals(0, gameState.getTotalActivityCount(activityType));
            gameState.doActivity(1, 10, activityType, "test", "test");
            assertEquals(1, gameState.getTotalActivityCount(activityType));
        }
    }
}
