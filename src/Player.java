import processing.core.PApplet;
import processing.core.PImage;

public class Player {

    public float x;
    public float y;

    public float width = 48;
    public float height = 48;
    float visualScale = 1.5f;
    float reduceSpace = 26;

    public float speed = 7;

    public boolean moveLeft;
    public boolean moveRight;

    public float vx = 0;
    public float vy = 0;
    public float gravity = 1f;
    public float jumpForce = -20;
    public boolean onGround = false;

    public Tileset sprite;

    private boolean facingRight = true;

    private int frame = 0;
    private int frameCounter = 0;
    private int frameDelay = 7;

    private final int cols = 7;

    private final int idleStart = 0 * cols;
    private final int idleLen   = 7;

    private final int walkStart = 1 * cols;
    private final int walkLen   = 7;

    private final int jumpStart = 2 * cols;
    private final int jumpLen   = 7;

    public Player(float x, float y, Tileset sprite) {
        this.x = x;
        this.y = y;

        this.sprite = sprite;
    }

    public void updateAnimation() {
        if (vx > 0) facingRight = true;
        if (vx < 0) facingRight = false;

        if (!onGround) return;

        frameCounter++;
        if (frameCounter < frameDelay) return;

        frameCounter = 0;

        if (vx != 0) {
            frame++;
            if (frame >= walkLen) frame = 0;
        } else {
            frame++;
            if (frame >= idleLen) frame = 0;
        }
    }


    public void draw(PApplet app) {

        int id;

        if (!onGround) {
            int jf;
            if (vy < -1) jf = 1;
            else if (vy > 1) jf = 5;
            else jf = 3;

            id = jumpStart + jf;
            if (id >= jumpStart + jumpLen) id = jumpStart;
        }
        else if (vx != 0) {
            id = walkStart + frame;
        }
        else {
            id = idleStart + frame;
        }

        PImage img = sprite.getImage(id);
        if (img == null) {
            app.fill(255, 0, 0);
            app.rect(x, y, width, height);
            return;
        }

        float dw = img.width * visualScale;
        float dh = img.height * visualScale;

        float drawX = x + width / 2f - dw / 2f;

        float drawY = y + height - dh + reduceSpace;

        app.pushMatrix();

        if (!facingRight) {
            app.translate(drawX + dw, drawY);
            app.scale(-1, 1);
            app.image(img, 0, 0, dw, dh);
        } else {
            app.image(img, drawX, drawY, dw, dh);
        }

        app.popMatrix();
    }

    public void jump() {
        if (onGround) {
            vy = jumpForce;
            onGround = false;
        }
    }
}