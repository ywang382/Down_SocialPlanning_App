package com.teaminus4.down;

public class FriendEntry implements Comparable<FriendEntry>{

    private String name;
    private String uid;
    private int avatar;
    private boolean selected;

    public FriendEntry(String name, String uid, int avatar){
        this.name = name;
        this.uid = uid;
        this.avatar = avatar;
        this.selected = false;
    }

    public String getName() {return this.name;}
    public String getUid() {return this.uid;}
    public int getAvatar() {return this.avatar;}
    public void setSelected(boolean sel) {this.selected = sel;}
    public boolean getSelect() {return this.selected;}

    public int compareTo(FriendEntry f) {
        return this.name.toUpperCase().compareTo(f.getName().toUpperCase());
    }


    public String toString() {
        return this.getName() + " " + this.getUid() + " " + this.getAvatar();
    }
}
