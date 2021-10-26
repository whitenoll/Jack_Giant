package clouds;

import collectables.Collectable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import helpers.GameInfo;
import player.Player;

import java.util.Random;

public class CloudsController {
    private World world;

    private Array<Cloud> clouds = new Array<Cloud>();
    private Array<Collectable> collectables = new Array<Collectable>();
    private final float DISTANCE_BETWEEN_CLOUDS = 250f;
    private float minX;
    private float maxX;
    private float cameraY;
    private float lastCloudPositionY;

    private Random random = new Random();

    public CloudsController(World world) {
        this.world = world;
        minX = GameInfo.WIDTH / 2f - 110;
        maxX = GameInfo.WIDTH / 2f + 110;
        createClouds();
        positionClouds(true);
    }

    void createClouds() {
        for (int i = 0; i < 2; i++) {
            clouds.add(new Cloud(world, "Dark Cloud"));
        }
        int index = 1;
        for (int i = 0; i < 6; i++) {
            clouds.add(new Cloud(world, "Cloud " + index));
            index++;
            if (index == 4) {
                index = 1;
            }

            clouds.shuffle();
        }

    }

    public void positionClouds(Boolean firstTimeArranging) {

        while (clouds.get(0).getCloudName() == "Dark Cloud"){
            clouds.shuffle();
        }
        float positionY = 0;
        if (firstTimeArranging){
            positionY = GameInfo.HEIGHT / 2f;
        }else {
            positionY = lastCloudPositionY;
        }
        int controlX = 0;
        for (Cloud c : clouds) {
            if (c.getX() == 0 && c.getY() == 0){
                float tempX = 0;
                if(controlX == 0) {
                    tempX = randomBetweenNumbers(maxX -60, maxX);
                    controlX = 1;
                    c.setDrawLeft(false);
                } else if(controlX == 1){
                    tempX = randomBetweenNumbers(minX +60, minX);
                    controlX = 0;
                    c.setDrawLeft(true);
                }
                c.setSpritePosition(tempX, positionY);

                positionY -= DISTANCE_BETWEEN_CLOUDS;
                lastCloudPositionY = positionY;

                if(!firstTimeArranging & c.getCloudName() != "Dark Cloud"){
                    int rand = random.nextInt(10);
                    if(rand > 5) {
                        int randomCollectable = random.nextInt(2);
                        if (randomCollectable == 0){
                            // spawn life, if the life count is lower than 2
                            Collectable collectable = new Collectable(world, "Life");
                            collectable.setCollectablePosition(c.getX(), c.getY() + 40);
                            collectables.add(collectable);
                        } else {
                            //spawn coin
                            Collectable collectable = new Collectable(world, "Coin");
                            collectable.setCollectablePosition(c.getX(), c.getY() + 40);
                            collectables.add(collectable);
                        }
                    }

                }
            }



        }
    }
    public void drawClouds(SpriteBatch batch){
        for (Cloud c: clouds){
            if(c.getDrawLeft()) {
                batch.draw(c, c.getX() - c.getWidth() / 2f - 20, c.getY() - c.getHeight() / 2f);
            }else {
                batch.draw(c, c.getX() - c.getWidth() / 2f + 10, c.getY() - c.getHeight() / 2f);
            }
        }
    }
    public void drawCollectables(SpriteBatch batch){
        for (Collectable c: collectables) {
            c.updateCollectable();
            batch.draw(c, c.getX(), c.getY());
        }
    }
    public void removeCollectables(){
        for (int i = 0; i < collectables.size; i++) {
            if (collectables.get(i).getFixture().getUserData() == "REMOVE"){
                collectables.get(i).changeFilter();
                collectables.get(i).getTexture().dispose();
                collectables.removeIndex(i);
            }
        }
    }

    public void createAndArrangeNewClouds(){
        for (int i = 0; i < clouds.size; i++) {
            if((clouds.get(i).getY()-GameInfo.HEIGHT / 2 - 15) > cameraY){
                //cloud is out of bounds remove it
                clouds.get(i).getTexture().dispose();
                clouds.removeIndex(i);
            }

        }
        if(clouds.size == 4){
            createClouds();
            positionClouds(false);
        }
    }
    public void setCameraY(float cameraY){
        this.cameraY = cameraY;
    }
    public Player positionThePlayer(Player player){
        player = new Player(world, clouds.get(0).getX() - 60, clouds.get(0).getY()+78);
        return player;
    }
    private float randomBetweenNumbers(float min, float max){
            return random.nextFloat() *(max - min) +min;
    }

}//CloudsController
