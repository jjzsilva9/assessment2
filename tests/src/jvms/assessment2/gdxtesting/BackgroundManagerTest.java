package jvms.assessment2.gdxtesting;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.uoyeng1g6.constants.ActivityType;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.models.GameState;
import com.badlogic.ashley.core.Engine;
import io.github.uoyeng1g6.utils.BackgroundManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(GdxTestRunner.class)
public class BackgroundManagerTest {

    Engine engine;
    GameState gameState;
    BackgroundManager bm;
    @Before
    public void initialiseEngine() {

        gameState = new GameState();
        Table energy = new Table();
        Table day = new Table();
        bm = new BackgroundManager(energy, day);
    }

    @Test
    public void testBackgrounds() {
        int totalHours = GameConstants.MAX_HOURS;
        int totalEnergy = GameConstants.MAX_ENERGY;

        assertEquals("Energy background should be white", "white", bm.energyStatus);
        assertEquals("Day background should be white", "white", bm.dayStatus);

        gameState.doActivity(5, 5, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Energy background should be white", "white", bm.energyStatus);
        assertEquals("Day background should be white", "white", bm.dayStatus);

        gameState.doActivity(0, 75, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Energy background should be orange", "orange", bm.energyStatus);

        gameState.doActivity(9, 0, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Day background should be orange", "orange", bm.dayStatus);


        gameState.doActivity(0, 10, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Energy background should be red", "red", bm.energyStatus);

        gameState.doActivity(1, 0, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Day background should be red", "red", bm.dayStatus);
    }

    @Test
    public void testAdvanceDay() {
        gameState.doActivity(14, 80, ActivityType.STUDY, "test", "test");
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        gameState.advanceDay();
        bm.updateBackgrounds(gameState.energyRemaining, gameState.hoursRemaining);

        assertEquals("Energy background should be reset", "white", bm.energyStatus);
        assertEquals("Day background should be reset", "white", bm.dayStatus);
    }




}
