package io.github.uoyeng1g6.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Class for controlling the background for UI counters
 *
 * Takes in the energy table and day table to set the background of
 */
public class BackgroundManager {

    Table energyTable = null;
    Table dayTable = null;

    public String energyStatus = "white";
    public String dayStatus = "white";

    public BackgroundManager(Table energy, Table day) {
        this.energyTable = energy;
        this.dayTable = day;
    }

    public void updateBackgrounds(int energyRemaining, int hoursRemaining) {
        // Check for energy levels:
        // Above 20: White background
        // 10-20: Orange background
        // 10-0: Red background
        if (20 < energyRemaining) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("background.png")));
            background.setMinWidth(20);
            background.setMinHeight(0);
            energyStatus = "white";

            energyTable.setBackground(background);
        } else if (10 < energyRemaining && energyRemaining <= 20) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("backgroundEnergyMed.png")));
            background.setMinWidth(20);
            background.setMinHeight(0);
            energyStatus = "orange";

            energyTable.setBackground(background);
        } else if (energyRemaining <= 10) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("backgroundEnergyLow.png")));
            background.setMinWidth(20);
            background.setMinHeight(0);
            energyStatus = "red";

            energyTable.setBackground(background);
        }

        // Check for hours left:
        // Above 2: White background
        // 2: Orange background
        // 1-0: Red background
        if (2 < hoursRemaining) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("background.png")));
            background.setMinWidth(18);
            background.setMinHeight(6);
            dayStatus = "white";

            dayTable.setBackground(background);
        } else if (hoursRemaining == 2) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("backgroundEnergyMed.png")));
            background.setMinWidth(18);
            background.setMinHeight(6);
            dayStatus = "orange";

            dayTable.setBackground(background);
        } else if (hoursRemaining <= 1) {
            Drawable background = new TextureRegionDrawable(new TextureRegion(new Texture("backgroundEnergyLow.png")));
            background.setMinWidth(18);
            background.setMinHeight(6);
            dayStatus = "red";

            dayTable.setBackground(background);
        }
    }
}
