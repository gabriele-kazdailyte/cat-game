import java.io.*;
import java.util.ArrayList;

public class Level {

    public int[][] map;
    public int startX = 0;
    public int startY = 0;

    public int rows() {
        return map.length;
    }
    public int cols() {
        return map[0].length;
    }

    public static Level loadCSV(String filePath) {
        ArrayList<int[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while((line = br.readLine()) != null)
            {
                line = line.trim();
                if(line.isEmpty()) continue;

                String[] fields = line.split(",");
                int[] row = new int[fields.length];

                for(int i = 0; i < fields.length; i++)
                {
                    row[i] = Integer.parseInt(fields[i].trim());
                }
                rows.add(row);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading: " + filePath, e);
        }

        if (rows.isEmpty()) {
            throw new RuntimeException("File is empty: " + filePath);
        }

        int cols = rows.get(0).length;
        int[][] map = new int[rows.size()][cols];

        for(int i = 0; i < rows.size(); i++) {
            if(rows.get(i).length != cols)
            {
                throw new RuntimeException("CSV lines differ in size: " + filePath);
            }
            map[i] = rows.get(i);
        }

        Level level = new Level();
        level.map = map;

    for (int i = 0; i < level.map.length; i++) {
        for (int j = 0; j < level.map[0].length; j++) {
            int tileId = map[i][j];
            if (TileRules.get(tileId) == 4) {
                level.startX = j;
                level.startY = i;
            }
        }
    }

        return level;
    }

}
