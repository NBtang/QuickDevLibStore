package com.laotang.quickdev.aretrofit;


public class NavigationData {
    public static final int NAVIGATION_ON_FOUND = 10;
    public static final int NAVIGATION_ON_LOST = 11;
    public static final int NAVIGATION_ON_ARRIVAL = 12;
    public static final int NAVIGATION_ON_INTERRUPT = 13;

    private int state;
    public NavigationData(int state){
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
