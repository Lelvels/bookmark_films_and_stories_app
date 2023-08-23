package com.mrcong.bookmarkfilmsandcomicsapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*Working on the background, not showing any display to the user*/
public class AppExecutors {
    //Singleton pattern
    private static AppExecutors instance;
    public static AppExecutors getInstance(){
        if (instance == null){
            instance = new AppExecutors();
        }
        return instance;
    }
    //Create a thread pool
    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);
    public ScheduledExecutorService getmNetworkIO(){
        return mNetworkIO;
    }
}
