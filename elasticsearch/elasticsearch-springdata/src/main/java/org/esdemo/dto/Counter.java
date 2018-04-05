package org.esdemo.dto;

public class Counter {

    private int[] count;

    public Counter() {
        count = new int[1];
    }

    public Counter(int size) {
        count = new int[size];
    }

    public void increase() {
        increase(0);
    }

    public void increaseAmount(int amount) {
        increase(0, amount);
    }

    public void increase(int idx) {
        if (isRnage(idx)) {
            count[idx]++;
        }
    }

    public void increase(int idx, int amount) {
        if (isRnage(idx)) {
            count[idx] += amount;
        }
    }

    public void decrease(int idx) {
        if (isRnage(idx)) {
            count[idx]--;
        }
    }

    public int getValue(int idx) {
        if (isRnage(idx)) {
            return count[idx];
        }

        return 0;
    }

    public int getValue() {
        return getValue(0);
    }

    private boolean isRnage(int idx) {
        return (count != null && idx >= 0 && idx < count.length);
    }
}
