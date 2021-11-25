package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import helpers.GameInfo;
import helpers.GameManager;
import net.christopherwhite.jackthegiant.GameMain;
import scenes.Gameplay;
import scenes.Highscore;
import scenes.MainMenu;
import scenes.Options;

public class MainMenuButtons {
private GameMain game;
private Stage stage;
private Viewport gameViewport;
private ImageButton playBtn;
private ImageButton highscoreBtn;
private ImageButton optionsBTn;
private ImageButton quitBTn;
private ImageButton musicBTn;

public MainMenuButtons(GameMain game){
    this.game = game;
    gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
    stage = new Stage(gameViewport, game.getBatch());

    Gdx.input.setInputProcessor(stage);

    createAndPositionButtons();
    addAllListeners();

    stage.addActor(playBtn);
    stage.addActor(highscoreBtn);
    stage.addActor(optionsBTn);
    stage.addActor(quitBTn);
    stage.addActor(musicBTn);

    checkMusic();

}
void createAndPositionButtons(){
    playBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu/Start Game.png"))));
    highscoreBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu/Highscore.png"))));
   optionsBTn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu/Options.png"))));
   quitBTn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu/Quit.png"))));
   musicBTn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu/Music On.png"))));

    playBtn.setPosition(GameInfo.WIDTH / 2f - 80, GameInfo.HEIGHT / 2f +50, Align.center);
    highscoreBtn.setPosition(GameInfo.WIDTH / 2f - 60, GameInfo.HEIGHT / 2f -20, Align.center);
    optionsBTn.setPosition(GameInfo.WIDTH / 2f - 40, GameInfo.HEIGHT / 2f -90, Align.center);
    quitBTn.setPosition(GameInfo.WIDTH / 2f - 20, GameInfo.HEIGHT / 2f -160, Align.center);
    musicBTn.setPosition(GameInfo.WIDTH - 13,13, Align.bottomRight);
}
void addAllListeners() {
    playBtn.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            // any code hat we type will be executed when we press the play button
            GameManager.getInstance().gameStartedFromMainMenu = true;
            RunnableAction run = new RunnableAction();

            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new Gameplay(game));
                }
            });
            SequenceAction sa = new SequenceAction();
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(run);
            stage.addAction(sa);
        }
    });
    highscoreBtn.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            game.setScreen(new Highscore(game));
        }
    });
    optionsBTn.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            game.setScreen(new Options(game));
        }
    });
    quitBTn.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            Gdx.app.exit();
        }
    });
    musicBTn.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            if(GameManager.getInstance().gameData.isMusicOn()){
                GameManager.getInstance().gameData.setMusicOn(false);
                GameManager.getInstance().stopMusic();
            } else {
                GameManager.getInstance().gameData.setMusicOn(true);
                GameManager.getInstance().playMusic();
            }
            GameManager.getInstance().saveData();

        }
    });
}
    void checkMusic(){
        if(GameManager.getInstance().gameData.isMusicOn()){
            GameManager.getInstance().playMusic();
        }
    }
public Stage getStage(){
    return this.stage;
}





}//MainMenuButtons
