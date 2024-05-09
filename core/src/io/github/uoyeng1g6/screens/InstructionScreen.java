package io.github.uoyeng1g6.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.uoyeng1g6.HeslingtonHustle;
import io.github.uoyeng1g6.constants.GameConstants;
import io.github.uoyeng1g6.utils.ChangeListener;

/**
 * The end screen of the game. Displays the player's score and the total number done of each activity.
 */
public class InstructionScreen implements Screen {

    Camera camera;
    /**
     * The {@code scene2d.ui} stage used to render this screen.
     */
    Stage stage;

    int examScore;

    public InstructionScreen(HeslingtonHustle game) {
        camera = new OrthographicCamera();
        var viewport = new FitViewport(GameConstants.WORLD_WIDTH * 10, GameConstants.WORLD_HEIGHT * 10, camera);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        var root = new Table(game.skin);
        root.setFillParent(true);
        root.pad(0.25f);

        root.setDebug(game.debug);
        stage.addActor(root);

        root.add("Instructions").getActor().setFontScale(2);
        root.row();

        var inner = new Table(game.skin);

        inner.add("Use WASD to move around, Press E to Interact with buildings")
                .padBottom(20);
        inner.row();
        inner.add("Study for your exams! Make sure to get in enough rest, sleep and food!")
                .padBottom(20);
        inner.row();
        inner.add("Avoid overdoing a type of activity").padBottom(20);
        inner.row();

        var nextButton = new TextButton("Play!", game.skin);
        nextButton.addListener(ChangeListener.of((e, a) -> game.setState(HeslingtonHustle.State.PLAYING)));
        inner.add(nextButton)
                .padTop(50)
                .width(Value.percentWidth(0.4f, inner))
                .height(Value.percentHeight(0.1f, inner));

        inner.row();

        root.add(inner).grow();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
