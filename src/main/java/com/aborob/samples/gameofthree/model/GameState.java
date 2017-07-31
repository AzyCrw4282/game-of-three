package com.aborob.samples.gameofthree.model;

public class GameState {

    private boolean gameOn;
    private boolean waitRivalMove;
    private String rivalSession;
    private Integer currentNumber;

    public boolean isGameOn() {
        return this.gameOn;
    }

    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    public boolean isWaitRivalMove() {
        return this.waitRivalMove;
    }

    public void setWaitRivalMove(boolean waitRivalMove) {
        this.waitRivalMove = waitRivalMove;
    }

    public String getRivalSession() {
        return this.rivalSession;
    }

    public void setRivalSession(String rivalSession) {
        this.rivalSession = rivalSession;
    }

    public Integer getCurrentNumber() {
        return this.currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }
}
