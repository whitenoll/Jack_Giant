package scenes;

import clouds.Cloud;
import clouds.CloudsController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import helpers.GameInfo;
import helpers.GameManager;
import huds.UIHud;
import javafx.stage.Stage;
import net.christopherwhite.jackthegiant.GameMain;
import player.Player;

public class Gameplay implements Screen,ContactListener {
    private GameMain game;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private OrthographicCamera box2DCamera;
    private Box2DDebugRenderer debugRenderer;

    private World world;

    private Sprite[] bgs;
    private float lastYPosition;

    private float cameraSpeed = 10;
    private float maxSpeed = 10;
    private float acceleration = 10;

    private boolean touchedForTheFirstTime;

    private UIHud hud;

    private CloudsController cloudsController;

    private Player player;

    private float lastPlayerY;

    private Sound  coinSound, lifeSound;

    public Gameplay(GameMain game) {
        this.game = game;
        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);
        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM, GameInfo.HEIGHT / GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        world = new World(new Vector2(0, -9.8f), true);

        //inform the world that the contact listener is the gameplay class
        world.setContactListener(this);

        cloudsController = new CloudsController(world);
        player = cloudsController.positionThePlayer(player);
        createBackgrounds();
        setCameraSpeed();

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Coin Sound.wav"));
        lifeSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Life Sound.wav"));
    }

    void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.movePlayer(-2);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.movePlayer(2);
        } else {
            player.setWalking(false);
        }

    }

    void handleInputAndroid(){
        if(Gdx.input.isTouched()){
           if(Gdx.input.getX() > (GameInfo.WIDTH / 2)) {
                player.movePlayer(2f);
            }else {
                player.movePlayer(-2f);
           }
        }else {
            player.setWalking(false);
        }
    }
    void checkForFirstTouch(){
        if(!touchedForTheFirstTime){
            if(Gdx.input.justTouched()){
                touchedForTheFirstTime = true;
                GameManager.getInstance().isPaused = false;
                lastPlayerY = player.getY();
            }
        }
    }
    void update(float dt) {
        checkForFirstTouch();
        if(!GameManager.getInstance().isPaused){
            handleInput(dt);
//            handleInputAndroid(); //Enable for android
            moveCamera(dt);
            checkBackgroundOutOfBounds();
            cloudsController.setCameraY(mainCamera.position.y);
            cloudsController.createAndArrangeNewClouds();
            cloudsController.removeOffScreenCollectables();
            checkPlayersBounds();
            countScore();
        }
    }
    void moveCamera(float delta) {
        mainCamera.position.y -= cameraSpeed * delta;

        cameraSpeed += acceleration * delta;

        if(cameraSpeed > maxSpeed) {
            cameraSpeed = maxSpeed;
        }
    }

    void setCameraSpeed(){
        if(GameManager.getInstance().gameData.isEasyDifficulty()){
            cameraSpeed = 80;
            maxSpeed = 100;
        }
        if(GameManager.getInstance().gameData.isMediumDifficulty()){
            cameraSpeed = 100;
            maxSpeed = 120;
        }
        if(GameManager.getInstance().gameData.isHardDifficulty()){
            cameraSpeed = 140;
            maxSpeed = 160;
        }
    }

    void createBackgrounds() {
        bgs = new Sprite[3];
        for (int i = 0; i < bgs.length; i++) {
            bgs[i] = new Sprite(new Texture("Backgrounds/Game BG.png"));
            bgs[i].setPosition(0, -(i * bgs[i].getHeight()));
            lastYPosition = Math.abs(bgs[i].getY());

        }
    }

    void drawBackgrounds() {
        for (int i = 0; i < bgs.length; i++) {
            game.getBatch().draw(bgs[i], bgs[i].getX(), bgs[i].getY());
        }
    }

    void checkBackgroundOutOfBounds() {
        for (int i = 0; i < bgs.length; i++) {
            if ((bgs[i].getY() - bgs[i].getHeight() / 2f - 5) > mainCamera.position.y) {
                float newPosition = bgs[i].getHeight() + lastYPosition;
                bgs[i].setPosition(0, -newPosition);
                lastYPosition = Math.abs(newPosition);
            }
        }
    }
    void checkPlayersBounds(){
        if (player.getY() - GameInfo.HEIGHT / 2f - player.getHeight() / 2f > mainCamera.position.y){
            if(!player.isDead()){
                playerDied();
            }
        }
        if (player.getY() + GameInfo.HEIGHT / 2f + player.getHeight() / 2f < mainCamera.position.y){
            if(!player.isDead()){
                playerDied();
            }
        }
        if((player.getX() - 25 > GameInfo.WIDTH)||(player.getX() + 60 < 0)){
            if(!player.isDead()){
                playerDied();
            }
        }
    }

    void countScore(){
        if(lastPlayerY > player.getY()){
            hud.incrementScore(1);
            lastPlayerY = player.getY();
        }
    }

    void playerDied(){
        GameManager.getInstance().isPaused = true;

        hud.decrementLife();
        player.setDead(true);
        player.setPosition(1000,1000);

        if(GameManager.getInstance().lifeScore < 0){
            //player has no more lives
            //check if we have a new highscore
            GameManager.getInstance().checkForNewHighscores();
            // show final score to user
            hud.createGameOverPanel();
            //load main menu
            RunnableAction run = new RunnableAction();

            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainMenu(game));
                }
            });
            SequenceAction sa = new SequenceAction();
            sa.addAction(Actions.delay(3f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(Actions.fadeIn(1f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(Actions.fadeIn(1f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(run);

            hud.getStage().addAction(sa);

        } else {
            //reload game to continue to play
            RunnableAction run = new RunnableAction();

            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new Gameplay(game));
                }
            });
            SequenceAction sa = new SequenceAction();
            sa.addAction(Actions.delay(3f));
            sa.addAction(Actions.fadeOut(1f));
            sa.addAction(run);

            hud.getStage().addAction(sa);
        }
    }

    @Override
    public void show() {

    }



    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        drawBackgrounds();
        cloudsController.drawClouds(game.getBatch());
        cloudsController.drawCollectables(game.getBatch());

        player.drawPlayerIdle(game.getBatch());
        player.drawPlayerAnimation(game.getBatch());
        game.getBatch().end();

        //debugRenderer.render(world, box2DCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();
        game.getBatch().setProjectionMatrix(mainCamera.combined);
        mainCamera.update();

        player.updatePlayer();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        for (int i = 0; i < bgs.length; i++) {
            bgs[i].getTexture().dispose();
        }
        player.getTexture().dispose();
        debugRenderer.dispose();
        coinSound.dispose();
        lifeSound.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
    Fixture body1, body2;
    if(contact.getFixtureA().getUserData() == "Player"){
        body1 = contact.getFixtureA();
        body2 = contact.getFixtureB();
    } else {
        body1 = contact.getFixtureB();
        body2 = contact.getFixtureA();

    }
    if(body1.getUserData() == "Player" && body2.getUserData() == "Coin"){
        // collided with the coin
       hud.incrementCoins();
       coinSound.play();
        body2.setUserData("REMOVE");
        cloudsController.removeCollectables();
    }
    if(body1.getUserData() == "Player" && body2.getUserData() == "Life"){
            // collided with the life
        hud.incrementLives();
        lifeSound.play();
        body2.setUserData("REMOVE");
        cloudsController.removeCollectables();
        }


    if(body1.getUserData() == "Player" && body2.getUserData() == "Dark Cloud"){
        if (!player.isDead()){
            playerDied();
        }
    }
}

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
} //Gameplay
