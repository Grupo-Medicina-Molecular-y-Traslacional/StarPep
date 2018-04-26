/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author loge
 */
public class Partition implements Iterable<Boolean> {

    public final static boolean LEFT_SIDE = false;
    public final static boolean RIGHT_SIDE = true;

    private final List<Boolean> arrayList;
    private final int lowerIndex, higherIndex;
    private final int middle;

    public Partition(int lowerIndex, int higherIndex) {
        this(Collections.synchronizedList(new ArrayList<Boolean>(higherIndex - lowerIndex + 1)),
                lowerIndex, higherIndex);
    }

    private Partition(List<Boolean> array, int lowerIndex, int higherIndex) {
        this.arrayList = array;
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
        return new Partition(arrayList, lowerIndex, middle);
    }

    public Partition getRightPartition() {
        return new Partition(arrayList, middle + 1, higherIndex);
    }

    public void initializePartition() {
        for (int i = lowerIndex; i <= middle; i++) {
            arrayList.add(i, LEFT_SIDE);
        }
        for (int i = middle + 1; i <= higherIndex; i++) {
            arrayList.add(i, RIGHT_SIDE);
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
        boolean tmp = arrayList.get(i);
        arrayList.set(i, arrayList.get(j));
        arrayList.set(j, tmp);
    }

    public Boolean[] getArray() {
        int size = getSize();
        Boolean[] partition = new Boolean[size];
        for (int i = 0; i < partition.length; i++) {
            partition[i] = arrayList.get(lowerIndex + i);
        }
        return partition;
    }

    public void setArray(Boolean[] partition) {
        for(int i=0; i< partition.length; i++){
            arrayList.set(lowerIndex + i, partition[i]);
        }
    }

    public boolean getSideAt(int index) {
        return arrayList.get(index);
    }

    public void doMoveAt(int index) {
        arrayList.set(index, !arrayList.get(index));
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
            return arrayList.get(cursor++);
        }

    }

}
