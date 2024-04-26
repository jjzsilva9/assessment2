import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import jvms.assessment2.gdxtesting.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class GameStateTest {
    @Test
    public void testAdvanceDay() {
        GameState gameState = new GameState();
        assertEquals(7, gameState.daysRemaining);
        assertEquals(GameConstants.MAX_ENERGY, gameState.energyRemaining);
        assertEquals(GameConstants.MAX_HOURS, gameState.hoursRemaining);
        assertEquals(new ArrayList<GameState.Day>(7), gameState.days);
        gameState.advanceDay();
        assertEquals(6, gameState.daysRemaining);
        assertEquals(GameConstants.MAX_ENERGY, gameState.energyRemaining);
        assertEquals(GameConstants.MAX_HOURS, gameState.hoursRemaining);
        assertNotSame(new ArrayList<GameState.Day>(7), gameState.days);
        assertNotNull(gameState.interactionOverlay);
    }

    @Test
    public void testDoActivity() {
        GameState gameState = new GameState();
        int timeToUse = 1;
        int energyToUse = 10;
        ActivityType activityType = ActivityType.MEAL;
        String overlayText = "Text";
        int beforeEnergy = gameState.energyRemaining;
        int beforeHoursRemaining = gameState.hoursRemaining;
        GameState.Day currentDay = gameState.currentDay;
        boolean result = gameState.doActivity(timeToUse, energyToUse, activityType, overlayText);
        assertTrue(result);
        assertEquals(beforeEnergy - energyToUse, gameState.energyRemaining);
        assertEquals(beforeHoursRemaining - timeToUse, gameState.hoursRemaining);
        currentDay.activityStats.merge(activityType, 1, Integer::sum);
        assertEquals(currentDay, gameState.currentDay);
        gameState.energyRemaining = 5;
        boolean result2 = gameState.doActivity(timeToUse, energyToUse, activityType, overlayText);
        assertFalse(result2);
    }

    @Test
    public void testGetTotalActivityCount() {
        GameState gameState = new GameState();
        for(ActivityType activityType : ActivityType.values()) {
            assertEquals(0, gameState.getTotalActivityCount(activityType));
            gameState.doActivity(1, 10, activityType, "test");
            assertEquals(1, gameState.getTotalActivityCount(activityType));
        }
    }
}
