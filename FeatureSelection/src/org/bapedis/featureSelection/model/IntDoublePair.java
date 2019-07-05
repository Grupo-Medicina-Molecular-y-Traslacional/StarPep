/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.model;

/**
 *
 * @author Loge
 */
/**
 * Represents the position of a double value in an ordering. Comparable
 * interface is implemented so Arrays.sort can be used to sort an array of
 * IntDoublePairs by value. Note that the implicitly defined natural ordering is
 * NOT consistent with equals.
 */
public class IntDoublePair implements Comparable<IntDoublePair>{
    /**
     * Value of the pair
     */
    private final double value;

    /**
     * Original position of the pair
     */
    private final int position;

    /**
     * Construct an IntDoublePair with the given value and position.
     *
     * @param value the value of the pair
     * @param position the original position
     */
    public IntDoublePair(double value, int position) {
        this.value = value;
        this.position = position;
    }

    /**
     * Compare this IntDoublePair to another pair. Only the
     * <strong>values</strong> are compared.
     *
     * @param other the other pair to compare this to
     * @return result of <code>Double.compare(value, other.value)</code>
     */
    public int compareTo(IntDoublePair other) {
        return Double.compare(value, other.value);
    }

    // N.B. equals() and hashCode() are not implemented; see MATH-610 for discussion.
    /**
     * Returns the value of the pair.
     *
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the original position of the pair.
     *
     * @return position
     */
    public int getPosition() {
        return position;
    }
    
}
