package jvms.assessment2.gdxtesting;

import com.badlogic.gdx.Gdx;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class AssetTests {

    @Test
    public void testPlayerAssetExists() {
        assertTrue("The asset for the player exists", Gdx.files.internal("../assets/sprites/player.png").exists());
        assertTrue("The asset for the player exists", Gdx.files.internal("../assets/sprites/player.txt").exists());
    }

    @Test
    public void testTilemapAssetExists() {
        assertTrue("The asset for the TileMap exists", Gdx.files.internal("../assets/maps/campus-east.tmx").exists());
    }

    @Test
    public void testInteractionIconsAssetExists() {
        assertTrue("The asset for the Interaction Icons exists", Gdx.files.internal("../assets/sprites/interaction_icons.png").exists());
        assertTrue("The asset for the Interaction Icons  exists", Gdx.files.internal("../assets/sprites/interaction_icons.txt").exists());
    }

    @Test
    public void testTerrainAssetExists() {
        assertTrue("The asset for the terrain exists", Gdx.files.internal("../assets/terrain.json").exists());
    }

    @Test
    public void testWhitePixelAssetExists() {
        assertTrue("The asset for the white pixel exists", Gdx.files.internal("../assets/white_pixel.png").exists());
    }
}
