package com.echodev.echoalpha.util;

/**
 * Created by Ho on 23/2/2017.
 */

public class BubbleCoordinates {

    private int x, y;

    public BubbleCoordinates(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public BubbleCoordinates setX(final int x) {
        this.x = x;
        return this;
    }

    public BubbleCoordinates setY(final int y) {
        this.y = y;
        return this;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
