package com.aborob.samples.gameofthree.entity;

public class NumberMessage {

    final public static int NO_ERROR = 0;
    final public static int WIN = 1;
    final public static int ERROR = 2;
    final public static int WAIT = 3;
    final public static int WRONG = 4;

    private Integer number;
    private Integer addition;
    private Integer status;
    private String message;

    public NumberMessage() {
    }

    public NumberMessage(Integer number, Integer addition, Integer status, String message) {
        this.number = number;
        this.addition = addition;
        this.status = status;
        this.message = message;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getAddition() {
        return addition;
    }

    public void setAddition(Integer addition) {
        this.addition = addition;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
