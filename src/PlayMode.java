import processing.core.PApplet;
import processing.core.PImage;

public class PlayMode implements Mode{

    private GameApp app;
    private Level level;
    private Tileset tileset;
    private Tileset playerTiles;
    private Camera camera;
    private Player player;

    private int drawS;

    public boolean win = false;
    private PImage winImage;

    public PlayMode(GameApp app, Level level, Tileset tileset, int drawS) {
        this.app = app;
        this.level = level;
        app.level = this.level;
        this.tileset = tileset;
        this.drawS = drawS;

        camera = new Camera(app.width, app.height);

        playerTiles = new Tileset(app, "clean.png", 64);
        player = new Player(level.startX * drawS, level.startY * drawS, playerTiles);

        winImage = app.loadImage("win.jpg");
    }

    public void update() {

        player.onGround = false;

        player.vx = 0;
        if (player.moveLeft)  player.vx = -player.speed;
        if (player.moveRight) player.vx = player.speed;

        player.x += player.vx;

        int rightCol = (int)((player.x + player.width - 1) / drawS);
        int leftCol = (int)(player.x / drawS);
        int topRow = (int)(player.y / drawS);
        int bottomRow = (int)((player.y + player.height - 1) / drawS);

        if (player.vx > 0) {
            for (int r = topRow; r <= bottomRow; r++) {
                if (r >= 0 && r < level.rows() &&
                        rightCol >= 0 && rightCol < level.cols()) {

                    if (TileRules.get(level.map[r][rightCol]) == 1) {
                        player.x = rightCol * drawS - player.width;
                        break;
                    }
                    else if (TileRules.get(level.map[r][rightCol]) == 2) {
                        player.x = level.startX * drawS; player.y = level.startY * drawS; player.vx = 0;
                        return;
                    }
                }
            }
        }

        else if (player.vx < 0) {
            for (int r = topRow; r <= bottomRow; r++) {
                if (r >= 0 && r < level.rows() &&
                        leftCol >= 0 && leftCol < level.cols()) {

                    if (TileRules.get(level.map[r][leftCol]) == 1) {
                        player.x = (leftCol + 1) * drawS;
                        break;
                    }
                    else if (TileRules.get(level.map[r][leftCol]) == 2) {
                        player.x = level.startX * drawS; player.y = level.startY * drawS; player.vx = 0;
                        return;
                    }
                }
            }
        }

        player.vy += player.gravity;
        player.y += player.vy;
        topRow = (int)(player.y / drawS);

        int row = (int)((player.y + player.height) / drawS);

        leftCol  = (int)((player.x + 5) / drawS);
        rightCol = (int)((player.x + player.width - 5) / drawS);

        if (row >= 0 && row < level.rows()) {

            boolean landed = false;

            if (leftCol >= 0 && leftCol < level.cols()) {
                int tileId = level.map[row][leftCol];
                if (TileRules.get(tileId) == 1) landed = true;
                if (TileRules.get(tileId) == 2) {
                    player.x = level.startX * drawS; player.y = level.startY * drawS; player.vy = 0;
                    return;
                }
                if (TileRules.get(tileId) == 3) {
                    nextLevel();
                    return;
                }
            }

            if (!landed && rightCol >= 0 && rightCol < level.cols()) {
                int tileId = level.map[row][rightCol];
                if (TileRules.get(tileId) == 1) landed = true;
                if (TileRules.get(tileId) == 2) {
                    player.x = level.startX * drawS; player.y = level.startY * drawS; player.vy = 0;
                    return;
                }
                if (TileRules.get(tileId) == 3) {
                    nextLevel();
                    return;
                }
            }

            if (landed && player.vy > 0) {
                player.y = row * drawS - player.height;
                player.vy = 0;
                player.onGround = true;
            }
        }

        if (player.vy < 0) {
            if (topRow >= 0 && topRow < level.rows()) {
                boolean hitHead = false;

                if (leftCol >= 0 && leftCol < level.cols()) {
                    if (TileRules.get(level.map[topRow][leftCol]) == 1)
                        hitHead = true;
                    else if (TileRules.get(level.map[topRow][leftCol]) == 2) {
                        player.x = level.startX * drawS; player.y = level.startY * drawS; player.vy = 0;
                        return;
                    }
                }

                if (!hitHead && rightCol >= 0 && rightCol < level.cols()) {
                    if (TileRules.get(level.map[topRow][rightCol]) == 1)
                        hitHead = true;
                    else if (TileRules.get(level.map[topRow][leftCol]) == 2) {
                        player.x = level.startX * drawS; player.y = level.startY * drawS; player.vy = 0;
                        return;
                    }
                }

                if (hitHead) {
                    player.y = (topRow + 1) * drawS;
                    player.vy = 0;
                }
            }
        }

        camera.x = player.x - app.width / 3;
        camera.y = player.y - app.height / 3;

        float worldWidth = level.cols() * drawS;
        if (player.x < 0) player.x = 0;
        if (player.x > worldWidth - player.width)
            player.x = worldWidth - player.width;

        float worldHeight = level.rows() * drawS;
        if (player.y < 0) player.y = 0;
        if (player.y > worldHeight - player.height)
            player.y = worldHeight - player.height;

        float maxCamx = Math.max(0, worldWidth - app.width);
        float maxCamy = Math.max(0, worldHeight - app.height);
        if (camera.x < 0) camera.x = 0;
        if (camera.x > maxCamx) camera.x = maxCamx;
        if (camera.y < 0) camera.y = 0;
        if (camera.y > maxCamy) camera.y = maxCamy;

        player.updateAnimation();
    }

    public void draw() {

        app.pushMatrix();
        camera.apply(app);

        for (int r = 0; r < level.rows(); r++) {
            for (int c = 0; c < level.cols(); c++) {

                int id = level.map[r][c];
                if (id < 0) continue;

                PImage tile = tileset.getImage(id);
                if (tile != null) {
                    app.image(tile, c * drawS, r * drawS, drawS, drawS);
                }
            }
        }
        player.draw(app);

        app.popMatrix();

        app.fill(255, 255, 255);
        app.text("Press e to enter edit mode | Level " + (app.levelIndex + 1) + " / " + app.levelFiles.length, 20, 30);

        if(win) {
            if (winImage != null) {
                app.image(winImage, 0, 0, app.width, app.height);
            }
            return;
        }

    }

    void nextLevel () {
        if (win) return;

        app.levelIndex ++;

        if (app.levelIndex >= app.levelFiles.length) {
            win = true;
            return;
        }



        level = Level.loadCSV(app.levelFiles[app.levelIndex]);

        app.level = level;

        player.x = level.startX * drawS;
        player.y = level.startY * drawS;
        player.vx = 0;
        player.vy = 0;

    }

    public void keyPressed(char key, int keyCode) {
        if (key == 'a' || key == 'A') player.moveLeft = true;
        if (key == 'd' || key == 'D') player.moveRight = true;

        if (key == ' ' || key == 'w' || key == 'W') {
            player.jump();
        }

        if (key == 'n' || key == 'N') {
            nextLevel();
        }

        if (key == 'r' || key == 'R') {
            win = false;
            app.levelIndex = -1;
            nextLevel();
        }
    }

    public void keyReleased(char key, int keyCode) {
        if (key == 'a' || key == 'A') player.moveLeft = false;
        if (key == 'd' || key == 'D') player.moveRight = false;
    }

    public void mousePressed(){};

}