package com.teaminus4.down;

import java.util.ArrayList;

public class GroupEntry implements Comparable<GroupEntry> {

    private String name;
    private boolean selected;
    private ArrayList<String> friendUids;

    public GroupEntry(String name, ArrayList<String> friendUids){
        this.name = name;
        this.friendUids = friendUids;
        this.selected = false;
    }

    public String getName() {return this.name;}
    public void setSelected(boolean sel) {this.selected = sel;}
    public boolean getSelect() {return this.selected;}
    public ArrayList<String> getFriendUids() {return this.friendUids;}

    public int compareTo(GroupEntry g) {
        return this.name.toUpperCase().compareTo(g.getName().toUpperCase());
    }

    public int getSizeOfGroup() {
        return this.friendUids.size();
    }


    public String toString() {
        return this.getName() + " " + this.getFriendUids();
    }
}
