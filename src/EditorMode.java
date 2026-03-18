import processing.core.PImage;

public class EditorMode implements Mode {

    private final GameApp app;
    private final Tileset tileset;

    private final int tileSize;

    private int[][] map;
    private int rows;
    private int cols;
    private float worldW;
    private float worldH;

    private final int paletteWidth = 300;
    private final int mapOffsetX;
    private final int paletteX = 20;
    private final int paletteY = 20;
    private final int paletteTileDraw = 48;
    private final int paletteCols = 5;

    private int selectedTileId = 0;

    private int paletteRowOffset = 0;

    private float camX = 0;
    private float camY = 0;
    private float camSpeed = 16;

    private float zoom = 1.0f;
    private final float zoomMin = 0.5f;
    private final float zoomMax = 3.0f;
    private final float zoomStep = 1.10f;

    private int lastPaintRow = Integer.MIN_VALUE;
    private int lastPaintCol = Integer.MIN_VALUE;

    public EditorMode(GameApp app, Tileset tileset, int tileSize) {
        this.app = app;
        this.tileset = tileset;
        this.tileSize = tileSize;

        this.mapOffsetX = paletteWidth;

        syncLevel();
    }

    private void syncLevel() {
        if (app.level == null || app.level.map == null) return;

        if (this.map != app.level.map) {
            this.map = app.level.map;
            this.rows = map.length;
            this.cols = map[0].length;
            this.worldW = cols * (float) tileSize;
            this.worldH = rows * (float) tileSize;

            clampPaletteScroll();
            clampCamera();

            lastPaintRow = Integer.MIN_VALUE;
            lastPaintCol = Integer.MIN_VALUE;
        }
    }

    @Override
    public void update() {
        syncLevel();
        if (map == null) return;

        clampPaletteScroll();
        clampCamera();

        if (app.mousePressed && app.mouseX >= paletteWidth) {
            paintAtMouse(app.mouseButton);
        } else {
            lastPaintRow = Integer.MIN_VALUE;
            lastPaintCol = Integer.MIN_VALUE;
        }
    }

    @Override
    public void draw() {
        syncLevel();
        app.background(36, 36, 36);

        drawLeftPanel();

        if (map != null) {
            app.clip(mapOffsetX, 0, app.width - mapOffsetX, app.height);
            app.pushMatrix();
            app.translate(mapOffsetX, 0);
            app.scale(zoom);
            app.translate(-camX, -camY);

            drawMap();
            drawGrid();

            app.popMatrix();
            app.noClip();
        }

        drawPalette();
        drawGhostTile();

        app.fill(0);
        app.text(
                "Editor | Level: " + (app.levelIndex + 1)
                        + " | Selected: " + selectedTileId
                        + " | LMB=select(palette)/place(map) | RMB=erase"
                        + " | drag to paint | WASD=scroll map | ↑↓=scroll tiles | +=zoom in | -=zoom out | P=save current level",
                10, app.height - 15
        );
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (app.keyCode == app.UP)   paletteRowOffset -= 1;
        if (app.keyCode == app.DOWN) paletteRowOffset += 1;

        if (app.key == 'a' || app.key == 'A') camX -= camSpeed;
        if (app.key == 'd' || app.key == 'D') camX += camSpeed;
        if (app.key == 'w' || app.key == 'W') camY -= camSpeed;
        if (app.key == 's' || app.key == 'S') camY += camSpeed;

        if (key == 'p' || key == 'P') {
            saveNextLevel();
            return;
        }

        if (key == '+' || key == '=') {
            zoom *= zoomStep;
            if (zoom > zoomMax) zoom = zoomMax;
            clampCamera();
            return;
        }

        if (key == '-') {
            zoom /= zoomStep;
            if (zoom < zoomMin) zoom = zoomMin;
            clampCamera();
        }
    }

    @Override
    public void keyReleased(char key, int keyCode) { }

    @Override
    public void mousePressed() {
        syncLevel();
        if (map == null) return;

        int mx = app.mouseX;
        int my = app.mouseY;

        if (mx < paletteWidth) {
            int pc = (mx - paletteX) / paletteTileDraw;
            int pr = (my - paletteY) / paletteTileDraw;
            pr += paletteRowOffset;

            if (pc >= 0 && pr >= 0 && pc < paletteCols) {
                int id = pr * paletteCols + pc;
                if (id >= 0 && id < tileset.getLength()) {
                    selectedTileId = id;
                }
            }
            return;
        }

        paintAtMouse(app.mouseButton);
    }

    private void paintAtMouse(int mouseButton) {
        if (map == null) return;

        float worldX = ((app.mouseX - mapOffsetX) / zoom) + camX;
        float worldY = (app.mouseY / zoom) + camY;

        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);

        if (row < 0 || row >= rows || col < 0 || col >= cols) return;

        if (row == lastPaintRow && col == lastPaintCol) return;
        lastPaintRow = row;
        lastPaintCol = col;

        if (mouseButton == app.RIGHT) {
            map[row][col] = -1;
        } else {
            map[row][col] = selectedTileId;
        }
    }

    private void drawLeftPanel() {
        app.noStroke();
        app.fill(220);
        app.rect(0, 0, paletteWidth, app.height);

        app.stroke(0, 120);
        app.line(paletteWidth, 0, paletteWidth, app.height);
        app.stroke(0);
    }

    private void drawMap() {
        float viewWWorld = (app.width - mapOffsetX) / zoom;
        float viewHWorld = app.height / zoom;

        int startCol = Math.max(0, (int) (camX / tileSize));
        int endCol = Math.min(cols - 1, (int) ((camX + viewWWorld) / tileSize) + 1);
        int startRow = Math.max(0, (int) (camY / tileSize));
        int endRow = Math.min(rows - 1, (int) ((camY + viewHWorld) / tileSize) + 1);

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                int id = map[r][c];
                if (id < 0) continue;

                PImage tile = tileset.getImage(id);
                if (tile != null) {
                    app.image(tile, c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    private void drawGrid() {
        app.noFill();
        app.stroke(0, 50);

        float viewWWorld = (app.width - mapOffsetX) / zoom;
        float viewHWorld = app.height / zoom;

        int startCol = Math.max(0, (int) (camX / tileSize));
        int endCol = Math.min(cols - 1, (int) ((camX + viewWWorld) / tileSize) + 1);
        int startRow = Math.max(0, (int) (camY / tileSize));
        int endRow = Math.min(rows - 1, (int) ((camY + viewHWorld) / tileSize) + 1);

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                app.rect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        app.stroke(0);
    }

    private void drawPalette() {
        int availableH = app.height - 40;
        int visibleRows = Math.max(1, (availableH - paletteY) / paletteTileDraw);

        int startIndex = paletteRowOffset * paletteCols;
        int endIndex = Math.min(tileset.getLength(), startIndex + visibleRows * paletteCols);

        int drawRow = 0;
        int drawCol = 0;

        for (int i = startIndex; i < endIndex; i++) {
            int x = paletteX + drawCol * paletteTileDraw;
            int y = paletteY + drawRow * paletteTileDraw;

            PImage tile = tileset.getImage(i);
            if (tile != null) {
                app.image(tile, x, y, paletteTileDraw, paletteTileDraw);
            }

            if (i == selectedTileId) {
                app.noFill();
                app.stroke(255, 0, 0);
                app.rect(x, y, paletteTileDraw, paletteTileDraw);
                app.stroke(0);
            }

            drawCol++;
            if (drawCol >= paletteCols) {
                drawCol = 0;
                drawRow++;
            }
        }
    }

    private void drawGhostTile() {
        if (app.mouseX < paletteWidth) return;

        PImage tile = tileset.getImage(selectedTileId);
        if (tile == null) return;

        int s = 36;
        int x = app.mouseX + 12;
        int y = app.mouseY + 12;

        app.pushStyle();
        app.tint(255, 180);
        app.image(tile, x, y, s, s);
        app.noTint();
        app.popStyle();
    }

    private void clampPaletteScroll() {
        int availableH = app.height - 40;
        int visibleRows = Math.max(1, (availableH - paletteY) / paletteTileDraw);

        int totalRows = (int) Math.ceil(tileset.getLength() / (float) paletteCols);
        int maxOffset = Math.max(0, totalRows - visibleRows);

        if (paletteRowOffset < 0) paletteRowOffset = 0;
        if (paletteRowOffset > maxOffset) paletteRowOffset = maxOffset;
    }

    private void clampCamera() {
        if (map == null) return;

        float viewWWorld = (app.width - mapOffsetX) / zoom;
        float viewHWorld = app.height / zoom;

        float maxX = Math.max(0, worldW - viewWWorld);
        float maxY = Math.max(0, worldH - viewHWorld);

        if (camX < 0) camX = 0;
        if (camY < 0) camY = 0;
        if (camX > maxX) camX = maxX;
        if (camY > maxY) camY = maxY;
    }

    private void saveCSV(String filenameInDataFolder) {
        if (map == null) return;

        String[] lines = new String[rows];
        for (int r = 0; r < rows; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                sb.append(map[r][c]);
                if (c < cols - 1) sb.append(",");
            }
            lines[r] = sb.toString();
        }

        app.saveStrings(app.dataPath(filenameInDataFolder), lines);
        System.out.println("Saved: data/" + filenameInDataFolder);
    }

    private void saveNextLevel() {
        if (map == null) return;

        int n = 1;
        while (true) {
            String name = "level" + n + ".csv";
            java.io.File f = new java.io.File(app.dataPath(name));
            if (!f.exists()) {
                saveCSV(name);
                break;
            }
            n++;
        }
    }

    public int[][] getMap() {
        return map;
    }
}