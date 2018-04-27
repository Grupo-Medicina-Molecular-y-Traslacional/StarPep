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
    private int middle;

    public Partition(int lowerIndex, int higherIndex) {
        this(new boolean[higherIndex - lowerIndex + 1],
                lowerIndex, higherIndex);
    }

    private Partition(boolean[] array, int lowerIndex, int higherIndex) {
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

    public Partition getLeftPartition() {
        if (array[middle] == RIGHT_SIDE){
            middle--;
        }
        Partition left = new Partition(array, lowerIndex, middle);
        left.checkPartition(LEFT_SIDE);
        return left;
    }

    public Partition getRightPartition() {
        if (array[middle] == RIGHT_SIDE){
            middle--;
        }        
        Partition right = new Partition(array, middle + 1, higherIndex);
        right.checkPartition(RIGHT_SIDE);
        return right;
    }

    private void checkPartition(boolean side) {
        for (int i = lowerIndex; i <= higherIndex; i++) {
            if (array[i] != side) {
                throw new IllegalStateException("Invalid partition at position " + i);
            }
        }
    }
    
    public void initializePartition(){
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

    public Boolean[] getArray() {
        int size = getSize();
        Boolean[] partition = new Boolean[size];
        for (int i = 0; i < partition.length; i++) {
            partition[i] = array[lowerIndex + i];
        }
        return partition;
    }

    public void setArray(Boolean[] partition) {
        for (int i = 0; i < partition.length; i++) {
            array[lowerIndex + i] = partition[i];
        }
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

        private int cursor = lowerIndex;

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
