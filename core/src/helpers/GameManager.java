package helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class GameManager {
    private static GameManager ourInstance = new GameManager();


    public GameData gameData;
    private Json json = new Json();
    private FileHandle fileHandle = Gdx.files.local("bin/GameData.json");

    public boolean gameStartedFromMainMenu, isPaused = true;
    public int lifeScore, coinScore, score;

    private GameManager(){

    }
    public void initializeGameData(){
        if (!fileHandle.exists()){
            gameData = new GameData();
            gameData.setCoinHighscore(0);
            gameData.setCoinHighscore(0);
            gameData.setEasyDifficulty(false);
            gameData.setHardDifficulty(false);
            gameData.setMediumDifficulty(true);
            gameData.setMusicOn(false);
            saveData();

        } else {
            loadData();
        }

    }
    public void saveData(){
        if(gameData != null){
            fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(gameData)), false);
        }
    }

    public void loadData(){
        gameData = json.fromJson(GameData.class, Base64Coder.decodeString(fileHandle.readString()));
    }

    public void checkForNewHighscores(){
        int oldHighScore = gameData.getHighscore();
        int oldCoinScore = gameData.getCoinHighscore();
        if(oldHighScore< coinScore){
            gameData.setHighscore(score);
        }
        if(oldCoinScore < coinScore){
            gameData.setCoinHighscore(coinScore);
        }

    }
    public static GameManager getInstance(){
        return ourInstance;
    }



}//game manager
