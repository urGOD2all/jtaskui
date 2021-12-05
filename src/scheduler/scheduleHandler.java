package jtaskui.scheduler;

import jtaskui.TaskObj;
import jtaskui.ui.swing.jReminder.jReminder;
import jtaskui.util.DateUtil;

import java.util.HashMap;

import java.time.LocalDateTime;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for scheduling. At the moment it is designed to work only for reminders of TaskObj.
 */
public class scheduleHandler {
    // Futures for all the scheduled tasks
    private HashMap<TaskObj, ScheduledFuture<?>> scheduledMap;
    // Services for all the scheduled tasks
    private HashMap<TaskObj, ScheduledExecutorService> scheduledServices;

    /**
     * Default contstuctor, initialises the Maps.
     */
    public scheduleHandler() {
        scheduledMap = new HashMap<TaskObj, ScheduledFuture<?>>();
        scheduledServices = new HashMap<TaskObj, ScheduledExecutorService>();
    }

    /**
     * Schedules the given task reminder UI. If canCancel is true this will cancel and schedule pending and reschedule. Useful if the reminder was changed
     *
     * @param task - TaskObj to have its reminder Date/Time read and scheduled for future reminder UI execution
     * @param boolean - if canCancel is true then a pending schedule can be cancelled and the schedule will be configured again
     */
    public void schedule(TaskObj task, boolean canCancel) {
        // If permitted to cancel a duplicate request then do so
        if (canCancel == true) {
            cancel(task);
        }
        // Schedule this task
        schedule(task);
    }

    /**
     * Schedules a given task reminder UI,
     *
     * @param task - TaskObj to have its reminder Date/Time read and scheduled for future reminder UI execution
     */
    private void schedule(TaskObj task) {
        // Do nothing if the task is already scheduled
        if (isScheduled(task)) return;

        // Work out time from now until the reminder is due in seconds
        long delaySeconds = DateUtil.secondsBetween(LocalDateTime.now(), task.getReminderLocalDateTime());
        //System.out.println(delaySeconds + " " + task.getSubject());

        // Create a new ScheduleExecutorService for this schedule. Only needs one thread because each Task could have a different time.
        ScheduledExecutorService scheduledTask = Executors.newScheduledThreadPool(1);
        // Schedule the task with the delay (if any) and store the result so it can be cancelled if required
        ScheduledFuture<?> sf = scheduledTask.schedule(new Runnable() {
            public void run() {
                jReminder jrt = new jReminder(task);
                jrt.initGUI();

                // Cleanup after this has finished executing
                completed(task);
            }
        }, delaySeconds, TimeUnit.SECONDS);

        // Store the Future to allow for cancelling
        scheduledMap.put(task, sf);
        // Store the ScheduleExecutorService so any resources can be cleaned up if the Task is cancelled
        scheduledServices.put(task, scheduledTask);
    }

    /**
     * Cancels a reminder for a Task. This can be used if the reminder is changed.
     *
     * @return boolean - true if the task was cancelled, false otherwise
     */
    public boolean cancel(TaskObj task) {
        if (isScheduled(task)) {
            // If the task is not already complete, cancel it
            if (! isDone(task)) scheduledMap.get(task).cancel(true);
            // Cleanup
            completed(task);
        }
        return true;
    }

    /**
     * Cancels all pending schedules. This is useful if the current file is closed or if the UI is closing.
     */
    public void cancelAll() {
        // Shutdown all the services
        scheduledServices.forEach((task, service) -> {
            scheduledMap.get(task).cancel(true);
            service.shutdownNow();
        });

        // Empty the HashMaps incase this gets reused
        scheduledMap.clear();
        scheduledServices.clear();
    }

    /**
     * Cleans up the HashMap and shutsdown the Executor service
     *
     * @param task - TaskObj to mark as completed and cleanup from the HashMaps
     */
    private void completed(TaskObj task) {
        scheduledServices.get(task).shutdownNow();
        scheduledMap.remove(task);
        scheduledServices.remove(task);
    }

    /**
     * Check to see if a TaskObj has its reminder already scheduled
     *
     * @param task - TaskObj to check if the reminder is scheduled for
     */
    public boolean isScheduled(TaskObj task) {
        return scheduledMap.containsKey(task);
    }

    /**
     * Check to see if a TaskObj has been cancelled. Only returns true if the TaskObj
     * has been scheduled.
     *
     * @param task - TaskObj to check if the reminder has been cancelled
     */
    public boolean isCancelled(TaskObj task) {
        if (isScheduled(task)) return scheduledMap.get(task).isCancelled();
        else return false;
    }

    /**
     * Check to see if a TaskObj reminder has been executed already. Only returns true if
     * the TaskObj had been scheduled
     *
     * @param task - TaskObj to check if the reminder has been executed
     */
    public boolean isDone(TaskObj task) {
        if (isScheduled(task)) return scheduledMap.get(task).isDone();
        else return false;
    }
}
