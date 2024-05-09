package io.github.uoyeng1g6.models;

import io.github.uoyeng1g6.constants.ActivityType;
import java.util.Arrays;
import java.util.List;

public class ScoreCalculator {

    private static final float MAX_DAY_SCORE = 105.125f;
    /**
     * Theoretical minimum day score. Allows normalising to range 0-100.
     */
    private static final float MIN_DAY_SCORE = 0f;

    /**
     * Calculate the score for a given day based on the number of activities performed. The optimal score
     * is given by studying 5 times, eating 3 times, and doing a recreational activity 3 times.
     *
     * @param studyCount the number of times the player studied during the day.
     * @param mealCount the number of times the player ate during the day.
     * @param recreationCount the number of recreational activities done by the player during the day.
     * @return the computed score given the activity counts.
     */
    public static float getDayScore(int studyCount, int mealCount, int recreationCount) {
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
    public static int calculateExamScore(List<GameState.Day> days) {
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
        int examScore = Math.round(Math.min(100, Math.max(0, totalScore)));
        return examScore;
    }

    public static List<Boolean> calculateAchievements(List<GameState.Day> days) {

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
}
