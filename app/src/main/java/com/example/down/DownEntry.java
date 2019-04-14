package com.example.down;

public class DownEntry {
    public String id;
    public String title;
    public String time;
    public boolean isDown;
    public String creator;
    public int nInvited;
    public int nDown;
    public long timestamp;

    public DownEntry(){ }

    public DownEntry(String title, String time, String creator) {
        this.title = title;
        this.time = time;
        this.creator = creator;
    }


}
