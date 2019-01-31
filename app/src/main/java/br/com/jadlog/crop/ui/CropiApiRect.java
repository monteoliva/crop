package br.com.jadlog.crop.ui;

import java.io.Serializable;

public class CropiApiRect implements Serializable {
    private int widht;
    private int height;
    private int top;
    private int left;
    private int right;
    private int bottom;

    public CropiApiRect() {
        this.widht  = 0;
        this.height = 0;
        this.top    = 0;
        this.left   = 0;
        this.right  = 0;
        this.bottom = 0;
    }

    public int getWidht() {
        return widht;
    }

    public void setWidht(int widht) {
        this.widht = widht;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
