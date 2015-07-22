package org.zywx.wbpalmstar.plugin.uexmultidownloader.vo;

import java.io.Serializable;

/**
 * Created by ylt on 15/7/17.
 */
public class OpenInputVO implements Serializable {
    private static final long serialVersionUID = 8283960006532272804L;
    private int x;
    private int y;
    private int w;
    private int h;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
