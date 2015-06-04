package net.beaconpe.blockclient.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread class.
 */
public abstract class BlockThread extends Thread{
    private List<Runnable> startupTasks = new ArrayList<Runnable>();
    private List<Runnable> shutdownTasks = new ArrayList<Runnable>();
    private boolean running = false;

    public abstract void run();

    public void startInCurrentThread(){
        startupTasks.forEach(Runnable::run);
        running = true;
        run();
    }

    public void startup(){
        startupTasks.forEach(Runnable::run);
        running = true;
        start();
    }

    public void shutdown() throws InterruptedException {
        shutdownTasks.forEach(Runnable::run);
        running = false;
        join(5000);
    }

    public void addStartupTask(Runnable r){
        startupTasks.add(r);
    }

    public void addShutdownTask(Runnable r){
        shutdownTasks.add(r);
    }


    public boolean isRunning(){
        return running;
    }
}
