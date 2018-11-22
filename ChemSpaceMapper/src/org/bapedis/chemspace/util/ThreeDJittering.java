package org.bapedis.chemspace.util;

import java.util.Random;

import javax.vecmath.Vector3f;

public class ThreeDJittering {

    Vector3f[] v;
    float minDist;
    float stepWidth;
    Random r;
    Vector3f dirs[];
    public static int STEPS = 10;
    int steps = STEPS;
    ThreeDNNComputer nn;

    public ThreeDJittering(Vector3f[] v, float minDist, Random r) {
        this.v = v;
        this.minDist = minDist;
        this.r = r;
        dirs = new Vector3f[v.length];
        computeDist();
    }

    public Vector3f getPosition(int i) {
        return v[i];
    }

    /**
     * returns true if neighbors found < min dist
     */
    private Boolean computeDist() {
        nn = new ThreeDNNComputer(v, minDist);
        nn.computeFast();
        return nn.isNeighborFound();
    }

    /**
     * returns true if finished (no neighbors found < min dist)
     */
    public boolean jitterStep() {
        if (stepWidth == 0) {
            this.stepWidth = minDist / (float) steps;
        }

        if (minDist <= 0) {
            throw new IllegalStateException("set min dist");
        }

        for (int i = 0; i < v.length; i++) {
            dirs[i] = null;
        }
        for (int i = 0; i < v.length; i++) {
            int neighbor = nn.getNeigbohrs()[i];
            Vector3f dir;
            if (neighbor == -1) {
                dir = null;
            } else if (dirs[neighbor] != null && nn.getNeigbohrs()[neighbor] == i) {
                dir = VectorUtil.negate(dirs[neighbor]);
            } else if (v[i].equals(v[neighbor])) {
                dir = VectorUtil.random3DVector(1.0F, r);
                if (v[i].y == 0 && v[neighbor].y == 0) {
                    dir.y = 0;
                }
                if (v[i].z == 0 && v[neighbor].z == 0) {
                    dir.z = 0;
                }
                normalize(dir, stepWidth);
            } else {
                dir = VectorUtil.direction(v[i], v[neighbor]);
                //					List<Vector3f> v1 = new ArrayList<Vector3f>();
                //					for (Vector3f v : objects[i].getOffsets())
                //						v1.add(Vector3fUtil.sum(v, objects[i].getPosition()));
                //					List<Vector3f> v2 = new ArrayList<Vector3f>();
                //					for (Vector3f v : objects[neighbor].getOffsets())
                //						v2.add(Vector3fUtil.sum(v, objects[neighbor].getPosition()));
                //
                //					dir = new Vector3f(0, 0, 0);
                //					for (int j1 = 0; j1 < v1.size(); j1++)
                //						for (int j2 = 0; j2 < v2.size(); j2++)
                //						{
                //							Vector3f v = Vector3fUtil.direction(v1.get(j1), v2.get(j2));
                //							Vector3fUtil.normalize(v, dist[i][neighbor] / v.length());
                //							dir.add(v);
                //						}
                normalize(dir, stepWidth);
            }
            dirs[i] = dir;
        }
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i] != null) {
                v[i].add(dirs[i]);
            }
        }
        return !computeDist();
    }

    public void jitter() {
        long start = System.currentTimeMillis();
        long reduceStepWidth = 2000; // reduce step-width if running longer than 2 seconds
        this.stepWidth = minDist / (float) steps;

        int i = 0;
        while (!jitterStep()) {
            i++;
            if (steps > 3 && System.currentTimeMillis() - start > reduceStepWidth) {
                reduceStepWidth += 1000;
                steps = Math.max(3, steps - 1);
                this.stepWidth = minDist / (float) steps;
//                System.out.println("jitter-iteration: " + i + " reducing step-width to " + steps);
            }
        }
    }

    private void normalize(Vector3f v, float length) {
        v.normalize();
        v.scale(length);
    }
}
