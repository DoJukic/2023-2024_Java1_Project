/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.utilities;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

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
    
    private final CompletableFuture worker = new CompletableFuture();
    private ArrayList<Runnable> todo;
    
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
            if (worker.isDone()){
                worker.thenRunAsync(todo.getLast()).thenRunAsync(() -> {taskFinished();});
                todo.removeLast();
                taskStarted();
            }
        }
    }
    
    public Boolean getAllTasksFinished(){
        synchronized(operationsLock){
            return (worker.isDone() && todo.isEmpty());
        }
    }
    
    private void taskStarted(){
        synchronized(operationsLock){
            for (var listener : taskStartedListeners){
                listener.notifiedTaskStarted();
            }
        }
    }
    
    private void taskFinished(){
        synchronized(operationsLock){
            for (var listener : oneTaskCompleteListeners){
                listener.notifiedTaskComplete();
            }
            
            if (todo.size() <= 0){
                for (var listener : allTasksCompleteListeners){
                    listener.notifiedTasksComplete();
                }
            }else{
                checkState();
            }
        }
    }
}
