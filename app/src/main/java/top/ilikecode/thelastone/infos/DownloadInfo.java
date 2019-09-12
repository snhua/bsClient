package top.ilikecode.thelastone.infos;

import java.io.Serializable;
import java.util.ArrayList;

public class DownloadInfo implements Serializable {
    private int[] dice;
    private ArrayList<String> playerNames;
    private boolean isStart;
    private String roomId;

    public void setDice(int[] dice) {
        this.dice = dice;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
        this.playerNames = playerNames;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int[] getDice() {
        return dice;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public boolean isStart() {
        return isStart;
    }

    public String getRoomId() {
        return roomId;
    }
}
