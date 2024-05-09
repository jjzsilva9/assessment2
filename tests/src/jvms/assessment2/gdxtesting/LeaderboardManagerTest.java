package jvms.assessment2.gdxtesting;

import io.github.uoyeng1g6.utils.LeaderboardManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;

public class LeaderboardManagerTest {

    /**
     * Creates a test leaderboard file before each test populated as such:
     * Adam,100
     * Eva,50
     * John,0
     */
    @Before
    public void createTestFile() {
        try {
            File file = new File("test.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter("test.txt");
            writer.write("Adam,100\n");
            writer.write("Eva,50\n");
            writer.write("John,0\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the test file after each test
     */
    @After
    public void deleteTestFile() {
        File file = new File("test.txt");
        file.delete();
    }

    @Test
    public void testLeaderboardCreation() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");

        String[][] expectedRanking = {{"Adam", "100"},
                                      {"Eva", "50"},
                                      {"John", "0"}};
        assertEquals("Leaderboard is not created properly internally", expectedRanking, lm.getRanking());
    }

    @Test
    public void testLeaderboardOrderValidation() {
        try {
            File file = new File("testValid.txt");

            FileWriter writer = new FileWriter(file);
            writer.write("Adam,0\n");
            writer.write("Eva,50\n");
            writer.write("John,0\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LeaderboardManager lm = new LeaderboardManager("testValid.txt");

        String[][] expectedRanking = {{"Eva","50"},
                                      {"Adam","0"},
                                      {"John","0"}};
        assertEquals("Leaderboard creation should resort entries", expectedRanking, lm.getRanking());

    }

    @Test
    public void testLeaderboardLengthValidation() {
        try {
            File file = new File("testValid.txt");

            FileWriter writer = new FileWriter(file);
            writer.write("Adam,100\n");
            writer.write("Eva,50\n");
            writer.write("Eva,50\n");
            writer.write("Eva,50\n");
            writer.write("Eva,50\n");
            writer.write("Eva,50\n");
            writer.write("Eva,50\n");
            writer.write("John,0\n");
            writer.write("John,0\n");
            writer.write("John,0\n");
            writer.write("John,0\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LeaderboardManager lm = new LeaderboardManager("testValid.txt");

        String[][] expectedRanking = {{"Adam","100"},
                                      {"Eva", "50"},
                                      {"Eva", "50"},
                                      {"Eva", "50"},
                                      {"Eva", "50"},
                                      {"Eva", "50"},
                                      {"Eva", "50"},
                                      {"John","0"},
                                      {"John","0"},
                                      {"John","0"}};
        assertEquals("Leaderboard creation should crop entries to 10", expectedRanking, lm.getRanking());

    }

    @Test
    public void testLeaderboardInsertionName() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");

        lm.addEntry("TestName", 100);
        String[][] expectedRanking = {{"Adam", "100"},
                                      {"TestName", "100"},
                                      {"Eva", "50"},
                                      {"John", "0"}};
        assertEquals("Leaderboard Insertion should be sorted by score, then name", expectedRanking, lm.getRanking());
    }

    @Test
    public void testLeaderboardInsertionScore() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");

        lm.addEntry("TestName", 32);
        String[][] expectedRanking = {{"Adam", "100"},
                                      {"Eva", "50"},
                                      {"TestName", "32"},
                                      {"John", "0"}};
        assertEquals("Leaderboard Insertion should be sorted by score, then name", expectedRanking, lm.getRanking());
    }

    @Test
    public void testLeaderboardReplacement() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");

        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        lm.addEntry("TestName", 32);
        String[][] expectedRanking = {{"Adam", "100"},
                                      {"Eva", "50"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"},
                                      {"TestName", "32"}};
        assertEquals("Leaderboard should remove the original last entry", expectedRanking, lm.getRanking());
    }

    @Test
    public void testLeaderboardSaveName() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");
        lm.saveName("Joe");
        String expectedName = "Joe";
        assertEquals("Current player name should be saved", expectedName, lm.getName());
    }

    @Test
    public void testLeaderboardUpdateScore() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");
        lm.saveName("Joe");
        lm.addEntry(65);

        String[][] expectedRanking = {{"Adam", "100"},
                                      {"Joe", "65"},
                                      {"Eva", "50"},
                                      {"John", "0"}};
        assertEquals("Current player score should be added properly", expectedRanking, lm.getRanking());
    }

    @Test
    public void testLeaderboardNameReset() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");
        lm.saveName("Joe");
        lm.addEntry(65);

        assertEquals("Name should be reset after an entry is added", "Unknown", lm.getName());
    }

    @Test
    public void testLeaderboardNameValidation() {
        LeaderboardManager lm = new LeaderboardManager("test.txt");
        lm.saveName("toolongnamenow");

        assertEquals("Name should be trimmed to 11 characters", "toolongname", lm.getName());
    }
}
