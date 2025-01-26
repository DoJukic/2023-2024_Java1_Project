/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.utilities;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Domi
 */
public class SynchronousAsynchronousWorker {
    
    public interface TaskStartedListener{
        public void notifiedTaskStarted();
    }
    public interface OneTaskCompleteListener{
        public void notifiedTaskComplete();
    }
    public interface AllTasksCompleteListener{
        public void notifiedTasksComplete();
    }
    
    private final Object operationsLock = new Object();
    
    private ArrayList<Runnable> todo = new ArrayList<>();
    private Boolean operationsRunning = false;
    
    private final ArrayList<TaskStartedListener> taskStartedListeners = new ArrayList<>();
    
    private final ArrayList<OneTaskCompleteListener> oneTaskCompleteListeners = new ArrayList<>();
    private final ArrayList<AllTasksCompleteListener> allTasksCompleteListeners = new ArrayList<>();
    
    public void addTask(Runnable runnable){
        synchronized(operationsLock){
            todo.add(runnable);
            checkState();
        }
    }
    
    public void cancelNonRunningTasks(){
        synchronized(operationsLock){
            todo.clear();
        }
    }
    
    public void subscribeToTaskStarted_ThreadWarn(TaskStartedListener listener){
        synchronized(operationsLock){
            taskStartedListeners.add(listener);
        }
    }
    
    public void subscribeToTaskEnded_ThreadWarn(OneTaskCompleteListener listener){
        synchronized(operationsLock){
            oneTaskCompleteListeners.add(listener);
        }
    }
    
    public void subscribeToAllTasksEnded_ThreadWarn(AllTasksCompleteListener listener){
        synchronized(operationsLock){
            allTasksCompleteListeners.add(listener);
        }
    }
    
    private void checkState(){
        synchronized(operationsLock){
            if (!operationsRunning){
                tryRunNextTask();
            }
        }
    }
    
    private void tryRunNextTask(){
        synchronized(operationsLock){
            if (!todo.isEmpty()){
                runNextTask();
            }
        }
    }
    
    private void runNextTask(){
        var task = todo.getFirst();
        ForkJoinPool.commonPool().execute(() ->{
            task.run();
            taskFinished();
        });
        todo.removeFirst();
        taskStarted();
    }
    
    public Boolean getAllTasksFinished(){
        synchronized(operationsLock){
            return (!operationsRunning && todo.isEmpty());
        }
    }
    
    private void taskStarted(){
        synchronized(operationsLock){
            operationsRunning = true;
            
            for (var listener : taskStartedListeners){
                listener.notifiedTaskStarted();
            }
        }
    }
    
    private void taskFinished(){
        synchronized(operationsLock){
            operationsRunning = false;
            
            for (var listener : oneTaskCompleteListeners){
                listener.notifiedTaskComplete();
            }
            
            if (todo.isEmpty()){
                for (var listener : allTasksCompleteListeners){
                    listener.notifiedTasksComplete();
                }
            }else{
                runNextTask();
            }
        }
    }
}
