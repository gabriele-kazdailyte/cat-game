import processing.core.PImage;

public class Tileset{
    PImage img;
    PImage[] imgs;

    int tileS;
    int rows, cols;

    public Tileset(GameApp app, String fileName, int tileS){
        this.tileS = tileS;

        img = app.loadImage(fileName);
        if(img == null)
        {
            System.out.println("Error loading image " + fileName);
        }

        rows = img.height / tileS;
        cols = img.width / tileS;

        imgs = new PImage[rows * cols];

        createArr();
    }

    public void createArr(){
        for (int i = 0; i < rows; i ++)
        {
            for (int j = 0; j < cols; j ++)
            {
                imgs[i * cols + j] = img.get(j * tileS, i * tileS, tileS, tileS);
            }
        }
    }

    public PImage getImage (int id) {
        if(id < 0 || id >= cols * rows) return null;
        return imgs[id];
    }

    public int getLength () {
        return imgs.length;
    }

}
