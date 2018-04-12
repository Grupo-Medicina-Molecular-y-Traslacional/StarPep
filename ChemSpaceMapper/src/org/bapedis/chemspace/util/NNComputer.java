package org.bapedis.chemspace.util;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Vector2f;

/**
 *
 * @author mguetlein
 */
public class NNComputer {

    int nn[];
    Vector2f[] v;
    float minDist = Float.MAX_VALUE;
    float minMinDist = Float.MAX_VALUE;
    float maxMinDist = -Float.MAX_VALUE;
    Boolean neigborFound;

    public NNComputer(Vector2f[] v, float minDist) {
        this.v = v;
        this.minDist = minDist;
    }

    public NNComputer(Vector2f[] v) {
        this.v = v;
    }

    /**
     * creates a grid with cube-width = minDist to search only adjacent 9 cubes
     * for possible neighbors instead of the complete dataset speed-up for large
     * sets with small minDist is about factor 10 compared to naive computation
     */
    @SuppressWarnings("unchecked")
    public void computeFast() {
        if (minDist == Float.MAX_VALUE) {
            throw new IllegalStateException();
        }

        neigborFound = false;
        //		int num = 0;

        /*
		 * compute min and max values for each dimension to determine grid-size
         */
        float min[] = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        float max[] = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE};
        for (int i = 0; i < v.length; i++) {
            if (v[i].x < min[0]) {
                min[0] = v[i].x;
            }
            if (v[i].y < min[1]) {
                min[1] = v[i].y;
            }
            if (v[i].x > max[0]) {
                max[0] = v[i].x;
            }
            if (v[i].y > max[1]) {
                max[1] = v[i].y;
            }
        }
        int gridLength[] = new int[2];
        for (int i = 0; i < gridLength.length; i++) {
            gridLength[i] = Math.max(1, (int) Math.ceil((max[i] - min[i]) / minDist));
        }
        //		System.out.println("grid-size is " + ArrayUtil.toString(gridLength));
        Set<Integer> grid[][] = (HashSet<Integer>[][]) new HashSet[gridLength[0]][gridLength[1]];

        /*
		 * assign each element to a cube in the grid
         */
        for (int i = 0; i < v.length; i++) {
            int x = Math.max(0, Math.min(gridLength[0] - 1, (int) ((v[i].x - min[0]) / minDist)));
            int y = Math.max(0, Math.min(gridLength[1] - 1, (int) ((v[i].y - min[1]) / minDist)));
            if (grid[x][y] == null) {
                grid[x][y] = new HashSet<Integer>();
            }
            grid[x][y].add(i);
        }
        nn = new int[v.length];

        /*
		 * determine neighbors for each element 
         */
        for (int i = 0; i < v.length; i++) {
            //			System.out.println("\ni:" + i);
            nn[i] = -1;
            int minIdx = -1;
            float minD = minDist;
            int x = Math.max(0, Math.min(gridLength[0] - 1, (int) ((v[i].x - min[0]) / minDist)));
            int y = Math.max(0, Math.min(gridLength[1] - 1, (int) ((v[i].y - min[1]) / minDist)));

            for (int x_i = x - 1; x_i < x + 2; x_i++) {
                if (x_i < 0 || x_i >= gridLength[0]) {
                    continue; //outside of grid
                }
                for (int y_i = y - 1; y_i < y + 2; y_i++) {
                    if (y_i < 0 || y_i >= gridLength[1]) {
                        continue; //outside of grid
                    }
                    if (grid[x_i][y_i] == null) {
                        continue; //cube is empty
                    }
                    for (int j : grid[x_i][y_i]) {
                        if (i == j) {
                            continue; //do not measure distance to self
                        }							//										System.out.println("dist to j:" + j);
                        //										num++;
                        float d = dist(v[i], v[j]);
                        if (d < minD) {
                            minD = d;
                            minIdx = j;
                            neigborFound = true;
                        }
                    }
                }
            }
            nn[i] = minIdx;
        }
        //		System.out.println("num " + num);
    }

    public void computeNaive() {
        //		int num = 0;
        float dist[][] = new float[v.length][v.length];
        for (int i = 0; i < v.length; i++) {
            dist[i][i] = Float.MAX_VALUE;
        }
        for (int i = 0; i < v.length - 1; i++) {
            dist[i][i] = Float.MAX_VALUE;
            for (int j = i + 1; j < v.length; j++) {
                //				num++;
                float d = dist(v[i], v[j]);
                dist[i][j] = d;
                dist[j][i] = d;
            }
        }
        nn = new int[v.length];
        for (int i = 0; i < v.length; i++) {
            nn[i] = -1;
            int idx = getMinIndex(dist[i]);
            if (dist[i][idx] < minMinDist) {
                minMinDist = dist[i][idx];
            }
            if (dist[i][idx] > maxMinDist) {
                maxMinDist = dist[i][idx];
            }
            nn[i] = idx;
        }
    }

    public int[] getNeigbohrs() {
        return nn;
    }

    /**
     * only available after computeNaive
     */
    public float getMinMinDist() {
        if (minMinDist == Float.MAX_VALUE) {
            throw new IllegalStateException();
        }
        return minMinDist;
    }

    /**
     * only available after computeNaive
     */
    public float getMaxMinDist() {
        if (maxMinDist == -Float.MAX_VALUE) {
            throw new IllegalStateException();
        }
        return maxMinDist;
    }

    /**
     * only available after computeFast
     */
    public boolean isNeighborFound() {
        if (neigborFound == null) {
            throw new IllegalStateException();
        }
        return neigborFound;
    }

    public static float dist(Vector2f v1, Vector2f v2) {
        float sum = 0;
        float dp = v1.x - v2.x;
        sum += dp * dp;
        dp = v1.y - v2.y;
        sum += dp * dp;
        return (float) Math.sqrt(sum);
    }

    public static int getMinIndex(float[] array) {
        float min = Float.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                idx = i;
            }
        }
        return idx;
    }
}
