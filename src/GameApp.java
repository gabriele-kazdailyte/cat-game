import processing.core.PApplet;

public class GameApp extends PApplet{

    int tileS = 64;
    int scale = 1;
    int drawS = tileS * scale;


    String[] levelFiles = {
            "data/level1.csv",
            "data/level2.csv",
            "data/level3.csv"
    };
    int levelIndex = 0;

    Tileset tileset;
    Level level;
    Mode currentMode;
    PlayMode playMode;
    EditorMode editorMode;

    public void settings() {
        size(1000, 500);
        noSmooth();
    }

    public void setup() {
        frameRate(60);

        tileset = new Tileset(this, "Tileset.png", tileS);

        level = Level.loadCSV(levelFiles[levelIndex]);

        playMode = new PlayMode(this, level, tileset, drawS);

        editorMode = new EditorMode(this, tileset, drawS);

        currentMode = playMode;

        System.out.println(tileset.getLength());
        System.out.println(TileRules.logic.length);
    }

    public void draw() {
        background(27, 27, 27);

        currentMode.update();
        currentMode.draw();
    }

    public void keyPressed() {
        if (key == ESC) {
            key = 0;
            exit();
        }

        if (key == 'e' || key == 'E') {

            if (currentMode == playMode) {
                currentMode = editorMode;
            } else {
                currentMode = playMode;
            }
            return;
        }

        currentMode.keyPressed(key, keyCode);
    }

    public void keyReleased() {
        currentMode.keyReleased(key, keyCode);
    }

    public void mousePressed() {
        currentMode.mousePressed();
    }

}