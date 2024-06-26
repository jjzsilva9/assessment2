package io.github.uoyeng1g6.models;

import io.github.uoyeng1g6.constants.ActivityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreCalculator {

    private static final float MAX_DAY_SCORE = 168f;
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
        // Calculate study points
        // Each hour of study is worth 10 points, up to 8 hours, after which it is reduced by 5
        var studyPoints = 0;
        for (int i = 1; i <= studyCount; i++) {
            studyPoints += i <= 8 ? 10 : -5;
        }
        // If they do not study at all, a heavy penalty of -75 is incurred
        if (studyCount == 0) {
            studyPoints = -75;
        }

        // Calculate meal points
        // Each meal is worth 16 points, up to 3 meals, after which it is reduced by 5
        var mealPoints = 0;
        for (var i = 1; i <= mealCount; i++) {
            mealPoints += i <= 3 ? 16 : -5;
        }
        // If they do not eat at all, a penalty of -50 is incurred
        if (mealCount == 0) {
            mealPoints = -50;
        }

        // Calculate recreation multiplier
        // Each activity is worth 8 points, up to 5 activities, after which it is reduced by 4
        var recreationPoints = 0;
        for (var i = 1; i <= recreationCount; i++) {
            recreationPoints += i <= 5 ? 8 : 4;
        }
        // If they do not relax at all, a penalty of -30 is incurred
        if (recreationPoints == 0) {
            recreationPoints = -30;
        }

        // Calculate day score
        return studyPoints + mealPoints + recreationPoints;
    }

    /**
     * Calculate the aggregate score of all the days.
     *
     * @param days the days to calculate the score for.
     * @return the computed game score.
     */
    public static int calculateExamScore(List<GameState.Day> days) {
        float totalScore = 0;

        // Calculate the score for each day, add to toal score
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

        // Add points for each achievement
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

        // Set total score to 0 if they fail on studying
        if (studyFailure) {
            totalScore = 0;
        }

        // Clamp total score from 0-100
        int examScore = Math.round(Math.min(100, Math.max(0, totalScore)));
        return examScore;
    }

    // Get missed activities for each day
    // Used in the EndScreen to display tips if they fail.
    public static List<ActivityType[]> calculateMissedDays(List<GameState.Day> days) {
        List<ActivityType[]> missedActivities = new ArrayList<ActivityType[]>();
        for (var day : days) {
            int studyCount = day.statFor(ActivityType.STUDY);
            int mealCount = day.statFor(ActivityType.MEAL);
            int recreationCount = day.statFor(ActivityType.RECREATION);

            ActivityType[] dayType = new ActivityType[3];
            if (studyCount == 0) {
                dayType[0] = ActivityType.STUDY;
            }
            if (mealCount == 0) {

                dayType[1] = ActivityType.MEAL;
            }
            if (recreationCount == 0) {
                dayType[2] = ActivityType.RECREATION;
            }
            missedActivities.add(dayType);
        }
        return missedActivities;
    }

    public static List<Boolean> calculateAchievements(List<GameState.Day> days) {

        // Movie Marathon: For watching 3 movies in a day
        boolean movieAchievement = false;

        // You really went to town on that... : For going to town 5 times in a day
        boolean townAchievement = false;

        // Gymbro : Go to the gym at least once a day, every day
        boolean sportAchievement = true;

        // Failure : You missed a day of study without catching up

        // Val for ensuring catchup can only happen once
        boolean failedStudyOnce = false;

        // Val for when they have missed one day of study but not 2 (and not caught up yet)
        boolean studyFailCheck = false;

        // Val for when they have failed due to missing a day and not catching up the next day
        boolean studyFailure = false;

        // Various checks throughout the days to set achievements
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
            } else if (studyFailCheck && day.statFor(ActivityType.STUDY) >= 2) {
                studyFailCheck = false;
            }
            if (day.statFor(ActivityType.STUDY) == 0 && !studyFailCheck) {
                if (failedStudyOnce) {
                    studyFailure = true;
                }
                failedStudyOnce = true;
                studyFailCheck = true;
            }
        }

        return Arrays.asList(movieAchievement, townAchievement, sportAchievement, studyFailure);
    }
}
