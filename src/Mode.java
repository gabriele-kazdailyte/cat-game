public interface Mode {
    void update();
    void draw();
    void keyPressed(char key, int keyCode);
    void keyReleased(char key, int keyCode);
    void mousePressed();
}