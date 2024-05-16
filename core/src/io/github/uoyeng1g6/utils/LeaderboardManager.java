package io.github.uoyeng1g6.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Controls access to the score text file
 */
public class LeaderboardManager {

    /**
     * Class for each entry in the leaderboard
     */
    class Ranking {
        String player;
        int score;
    }

    String currentPlayer = "Unknown";

    ArrayList<Ranking> leaderboard = new ArrayList<Ranking>();

    File file;

    /**
     * Controls access, ranking and updating of the leaderboard file
     * @param filepath File path of a .txt that stores the leaderboard
     */
    public LeaderboardManager(String filepath) {
        try {
            file = new File(filepath);

            // Creates a new file if it doesn't exist
            file.createNewFile();
            Scanner scanner = new Scanner(file);

            // Read through the text file, splitting on commas
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] res = line.split(",");

                Ranking entry = new Ranking();
                entry.player = res[0];
                entry.score = Integer.parseInt(res[1]);

                // Store each entry in the list
                leaderboard.add(entry);
            }

            // Sort and then crop the leaderboard
            sortLeaderboard();
            if (leaderboard.size() > 10) {
                leaderboard.subList(9, leaderboard.size() - 1).clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sorts based on score, then alphabetically
    public void sortLeaderboard() {
        leaderboard.sort(new SortByScore().thenComparing(new SortByName()));
    }

    public String[][] getRanking() {

        // Returns the ranking as a 2D string array
        String[][] stringRanking = new String[leaderboard.size()][2];
        for (int i = 0; i < leaderboard.size(); i++) {
            stringRanking[i][0] = leaderboard.get(i).player;
            stringRanking[i][1] = Integer.toString(leaderboard.get(i).score);
        }
        return stringRanking;
    }

    public String[][] addEntry(String playerName, int score) {
        // Creates a new Ranking
        Ranking newEntry = new Ranking();
        newEntry.player = playerName;
        newEntry.score = score;

        // If the leaderboard is not full, add the entry
        if (leaderboard.size() < 10) {
            leaderboard.add(newEntry);
            sortLeaderboard();
        } else {
            // Otherwise, add the entry, then sort and remove the lowest element
            leaderboard.add(newEntry);
            sortLeaderboard();
            leaderboard.remove(10);
        }
        writeToFile();
        return getRanking();
    }

    public String[][] addEntry(int score) {
        // To create a new entry when the player name is already known
        Ranking newEntry = new Ranking();

        newEntry.player = currentPlayer;
        newEntry.score = score;

        // If the leaderboard is not full, add the entry
        if (leaderboard.size() < 10) {
            leaderboard.add(newEntry);
            sortLeaderboard();
        } else {
            // Otherwise, add the entry, then sort and remove the lowest element
            leaderboard.add(newEntry);
            sortLeaderboard();
            leaderboard.remove(10);
        }
        writeToFile();
        currentPlayer = "Unknown";
        return getRanking();
    }

    public void saveName(String player) {
        // Check that the name is 13 characters or less
        if (player.length() > 14) {
            player = player.substring(0, 14);
        }
        // Remove commas
        player = player.replace(",", "");
        currentPlayer = player;
    }

    public String getName() {
        return currentPlayer;
    }

    public void writeToFile() {
        try {
            FileWriter writer = new FileWriter(file.getPath());
            for (Ranking entry : leaderboard) {
                writer.write(entry.player + "," + Integer.toString(entry.score) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SortByScore implements Comparator<Ranking> {
        @Override
        public int compare(Ranking o1, Ranking o2) {
            return o2.score - o1.score;
        }
    }

    class SortByName implements Comparator<Ranking> {

        @Override
        public int compare(Ranking o1, Ranking o2) {
            return o1.player.toLowerCase().compareTo(o2.player.toLowerCase());
        }
    }
}
