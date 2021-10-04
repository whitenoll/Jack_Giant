package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import helpers.GameInfo;
import net.christopherwhite.jackthegiant.GameMain;

public class UIHud {
    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;

    private Image coinImage,lifeImage, scoreImage;

    private Label coinLabel, lifeLabel,scoreLabel;

    private ImageButton pauseBtn;

    public UIHud(GameMain game){
        this.game=game;

        gameViewport = new FitViewport(GameInfo.Width, GameInfo.Height, new OrthographicCamera());
        stage = new Stage(gameViewport, game.getBatch());

        createLabels();
        createImages();
        createBtnAndAddListener();

        Table lifeAndCoinTable = new Table();
        lifeAndCoinTable.top().left();
        lifeAndCoinTable.setFillParent(true);

        lifeAndCoinTable.add(lifeImage).padLeft(10).padTop(10);
        lifeAndCoinTable.add(lifeLabel).padLeft(5);
        lifeAndCoinTable.row();
        lifeAndCoinTable.add(coinImage).padLeft(10).padTop(10);
        lifeAndCoinTable.add(coinLabel).padLeft(5);

        Table scoreTable = new Table();
        scoreTable.top().right();
        scoreTable.setFillParent(true);

        scoreTable.add(scoreImage).padRight(20).padTop(15);
        scoreTable.row();
        scoreTable.add(scoreLabel).padRight(20).padTop(15);

        stage.addActor(lifeAndCoinTable);
        stage.addActor(scoreTable);
        stage.addActor(pauseBtn);


    }
    void createLabels(){

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/blow.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 40;

        BitmapFont font = generator.generateFont(parameter);
        coinLabel = new Label("x0", new Label.LabelStyle(font, Color.WHITE));
        lifeLabel = new Label("x2", new Label.LabelStyle(font, Color.WHITE));
        scoreLabel = new Label("x100", new Label.LabelStyle(font, Color.WHITE));



    }
    void createImages(){
        coinImage = new Image(new Texture("Collectables/Coin.png"));
        lifeImage = new Image(new Texture("Collectables/Life.png"));
        scoreImage = new Image(new Texture("Buttons/Gameplay/Score.png"));

    }
    void createBtnAndAddListener() {
        pauseBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Gameplay/Pause.png"))));

        pauseBtn.setPosition(470,17, Align.bottomRight);

        pauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //code for pausing the game
                System.out.println("PAUSE");
            }
        });
    }
    public Stage getStage(){
        return this.stage;
    }

























} // ui hud
