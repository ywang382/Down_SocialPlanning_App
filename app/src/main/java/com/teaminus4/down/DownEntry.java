package com.teaminus4.down;

public class DownEntry implements Comparable<DownEntry>{
    public String id;
    public String title;
    public String time;
    public boolean isDown;
    public String creator;
    public String creatorID;
    public int nInvited;
    public int nDown;
    public long timestamp;

    public DownEntry(){ }

    public DownEntry(String title, String time, long timestamp) {
        this.title = title;
        this.time = time;
        this.timestamp = timestamp;
    }

    public int compareTo(DownEntry d) {
        if(this.timestamp > d.timestamp){
            return 1;
        }
        if(this.timestamp < d.timestamp){
            return -1;
        }
        return 0;
    }


}
