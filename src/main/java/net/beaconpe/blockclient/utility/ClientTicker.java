package net.beaconpe.blockclient.utility;


import org.blockserver.ticker.RegisteredTask;
import org.blockserver.ticker.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A Client Ticker.
 */
public class ClientTicker extends BlockThread{
    private long currentTick = -1L;
    private int TPS = 20;
    private List<RegisteredTask> tasks = new ArrayList<>();

    public ClientTicker(){
        addShutdownTask(() -> {
            synchronized (tasks) {
                for (RegisteredTask task : tasks) {
                    task.getTask().onFinalize();
                }
            }
        });
    }

    @Override
    public void run() {
        setName("ClientTicker");
        while(isRunning()){
            currentTick++;
            synchronized (tasks) {
                for (RegisteredTask task : tasks) {
                    task.check(currentTick);
                }
            }

            int time = 1000 / TPS;
            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerRepeatingTask(Task task, int delay){
        synchronized (tasks){
            tasks.add(RegisteredTask.repeat(task, delay));
        }
    }
}
