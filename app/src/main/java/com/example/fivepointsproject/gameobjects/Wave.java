package com.example.fivepointsproject.gameobjects;

public class Wave {
    public int numBalls;
    public double minHp;
    public double maxHp;

    public Wave(int numBalls, double minHp, double maxHp) {
        this.numBalls = numBalls;
        this.minHp = minHp;
        this.maxHp = maxHp;
    }
}
