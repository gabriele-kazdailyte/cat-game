import processing.core.PApplet;

public class Camera {

    public float x = 0;
    public float y = 0;

    public int viewWidth;
    public int viewHeight;

    public Camera(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public void apply(PApplet app) {
        app.translate(-x, -y);
    }
}