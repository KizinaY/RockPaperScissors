package com.kizina.rscgame.entity;

import java.util.UUID;

public class Match {

    private UUID id;

    private Player firstPlayer;

    private Player secondPlayer;

    private Player winner;

    private Status status;

    public boolean isFinished() {
        return winner != null;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public boolean isReady() {
        return firstPlayer != null && secondPlayer != null;
    }

    public boolean isWaitingOpponent() {
        return secondPlayer == null;
    }

    public void clearHandSigns() {
        if (firstPlayer != null) {
            firstPlayer.setHandSign(null);
        }

        if (secondPlayer != null) {
            secondPlayer.setHandSign(null);
        }
    }


    public Match(UUID id, Player firstPlayer, Status status) {
        this.id = id;
        this.firstPlayer = firstPlayer;
        this.status = status;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isHandSignsReady() {
        return isReady() && firstPlayer.getHandSign() != null && secondPlayer.getHandSign() != null;
    }
}
