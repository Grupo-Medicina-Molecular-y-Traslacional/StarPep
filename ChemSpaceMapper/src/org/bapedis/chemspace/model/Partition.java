/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author loge
 */
public class Partition implements Iterable<Boolean> {

    public final static boolean LEFT_SIDE = false;
    public final static boolean RIGHT_SIDE = true;

    private final boolean[] array;
    private final int lowerIndex, higherIndex;
    private final int middle;

    public Partition(boolean[] array, int lowerIndex, int higherIndex) {
        this.array = array;
        this.lowerIndex = lowerIndex;
        this.higherIndex = higherIndex;
        this.middle = lowerIndex + (higherIndex - lowerIndex) / 2;
    }

    public int getLowerIndex() {
        return lowerIndex;
    }

    public int getMiddle() {
        return middle;
    }

    public int getHigherIndex() {
        return higherIndex;
    }

    public void initializePartition() {
            for (int i = lowerIndex; i <= middle; i++) {
                array[i] = LEFT_SIDE;
            }
            for (int i = middle + 1; i <= higherIndex; i++) {
                array[i] = RIGHT_SIDE;
            }
    }

    public void randomizePartition() {
        Random rnd = new Random();
        // Shuffle array
        for (int i = higherIndex; i > lowerIndex; i--) {
            swap(i - 1, rnd.nextInt(higherIndex - lowerIndex) + lowerIndex);
        }
    }

    public int getSize() {
        return higherIndex - lowerIndex + 1;
    }

    public void swap(int i, int j) {
            boolean tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
    }

    public boolean[] getArray() {
            int size = getSize();
            boolean[] partition = new boolean[size];
            System.arraycopy(array, lowerIndex, partition, 0, size);
            return partition;
    }

    public void setArray(boolean[] partition) {
            int size = getSize();
            System.arraycopy(partition, 0, array, lowerIndex, size);
    }

    public boolean getSideAt(int index) {
            return array[index];
    }

    public void doMoveAt(int index) {
            array[index] = !array[index];
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Boolean> {

        int cursor = lowerIndex;

        @Override
        public boolean hasNext() {
            return cursor <= higherIndex;
        }

        @Override
        public Boolean next() {
                return array[cursor++];
        }

    }

}
