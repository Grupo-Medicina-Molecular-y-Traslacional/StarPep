/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

import java.util.Random;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import org.apache.commons.math3.util.FastMath;

public class VectorUtil {

    public static Vector2f negate(Vector2f vector2f) {
        Vector2f v = new Vector2f(vector2f);
        v.negate();
        return v;
    }

    public static Vector3f negate(Vector3f vector3f) {
        Vector3f v = new Vector3f(vector3f);
        v.negate();
        return v;
    }

    public static Vector2f random2DVector(float radius, Random random) {
        float max = radius;
        float x = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        max = (float) Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        float y = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        max = (float) Math.sqrt(Math.pow(radius, 2) - (Math.pow(x, 2) + Math.pow(y, 2)));
        float z = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        float[] vec = new float[]{x, y};
        scramble(vec, random);
        return new Vector2f(vec);
    }

    public static Vector3f random3DVector(float radius, Random random) {
        float max = radius;
        float x = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        max = (float) Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        float y = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        max = (float) Math.sqrt(Math.pow(radius, 2) - (Math.pow(x, 2) + Math.pow(y, 2)));
        float z = random.nextFloat() * max * (random.nextBoolean() ? 1 : -1);

        float[] vec = new float[]{x, y, z};
        scramble(vec, random);
        return new Vector3f(vec);
    }

    public static void scramble(float[] array) {
        scramble(array, new Random());
    }

    public static void scramble(float[] array, Random r) {
        for (int i = 0; i < array.length; i++) {
            int j = r.nextInt(array.length);
            float tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void normalize(Vector2f[] v) {
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (Vector2f v1 : v) {
            minX = Math.min(minX, v1.getX());
            maxX = Math.max(maxX, v1.getX());
            minY = Math.min(minY, v1.getY());
            maxY = Math.max(maxY, v1.getY());
        }

        float x, y;
        for (Vector2f v1 : v) {
            x = v1.getX();
            if (x != 0.0) {
                v1.setX((x - minX) / (maxX - minX));
            }

            y = v1.getY();
            if (y != 0.0) {
                v1.setY((y - minY) / (maxY - minY));
            }
        }
    }

    public static Vector2f direction(Vector2f v1, Vector2f v2) {
        Vector2f v = new Vector2f(v1);
        v.sub(v2);
        return v;
    }

    public static Vector3f direction(Vector3f v1, Vector3f v2) {
        Vector3f v = new Vector3f(v1);
        v.sub(v2);
        return v;
    }

    public static float dist(Vector2f v1, Vector2f v2) {
        float sum = 0;
        float dp = v1.x - v2.x;
        sum += dp * dp;
        dp = v1.y - v2.y;
        sum += dp * dp;
        return (float) FastMath.sqrt(sum);
    }

    public static float dist(Vector3f v1, Vector3f v2) {
        float sum = 0;
        float dp = v1.x - v2.x;
        sum += dp * dp;
        dp = v1.y - v2.y;
        sum += dp * dp;
        dp = v1.z - v2.z;
        sum += dp * dp;
        return (float) FastMath.sqrt(sum);
    }

    public static float avgMinDist(Vector2f[] vectors) {
        float dist = 0;
        for (int i = 0; i < vectors.length; i++) {
            float min = Float.MAX_VALUE;
            for (int j = 0; j < vectors.length; j++) {
                if (i != j) {
                    min = Math.min(min, dist(vectors[i], vectors[j]));
                }
            }
            dist += min;
        }
        dist /= vectors.length;
        return dist;
    }

    public static float maxDist(Vector2f[] vectors) {
        float max = 0;
        for (int i = 0; i < vectors.length - 1; i++) {
            for (int j = i + 1; j < vectors.length; j++) {
                max = Math.max(max, dist(vectors[i], vectors[j]));
            }
        }
        return max;
    }

    public static Vector2f[] arrayCopy(Vector2f[] vectors) {
        Vector2f[] v = new Vector2f[vectors.length];
        for(int i=0; i<vectors.length; i++){
            v[i] = (Vector2f)vectors[i].clone();
        }
        return v;
    }

}
