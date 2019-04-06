package com.example.down;

public class FriendEntry implements Comparable<FriendEntry>{

    private String name;
    private String uid;
    private int avatar;

    public FriendEntry(String name, String uid, int avatar){
        this.name = name;
        this.uid = uid;
        this.avatar = avatar;
    }

    public String getName() {return this.name;}
    public String getUid() {return this.uid;}
    public int getAvatar() {return this.avatar;}

    public int compareTo(FriendEntry f) {
        return this.name.toUpperCase().compareTo(f.getName().toUpperCase());
    }

    public String toString() {
        return this.getName() + " " + this.getUid() + " " + this.getAvatar();
    }
}
