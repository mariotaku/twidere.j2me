/*
 * This file is part of org.kalmeo.util.
 * 
 * org.kalmeo.util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.kalmeo.util.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 21 nov. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util.worker;

import java.util.Vector;

/**
 * @author bbeaulant
 */
public class Worker implements Runnable {

	// The Worker static instance
	public static final Worker instance = new Worker();
	
	// The error listener of the worker thread
	private WorkerErrorListener workerErrorListener;

	// The associated thread
	private Thread thread;

	// The worker run state
	private boolean running = false;
	
	// Frame duration (in ms)
	private int frameDuration = 60;

	// Task list
	private final Vector tasks = new Vector();
	
	// Running task index
	private int runningTaskIndex = -1;
	
	// Synchronization mutex
	private final Object mutex = new Object(); 
	
	/**
	 * @param workerErrorListener
	 */
	public void setWorkerErrorListener(WorkerErrorListener workerErrorListener) {
		this.workerErrorListener = workerErrorListener;
	}
	
	/**
	 * @return <code>true</code> if the current Thread is the {@link Worker} thread
	 */
	public boolean isCurrentThread() {
		return (Thread.currentThread() == thread);
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return thread != null && running;
	}

	/**
	 * @return the frameDuration
	 */
	public int getFrameDuration() {
		return frameDuration;
	}

	/**
	 * @param frameDuration the frameDuration to set in milliseconds
	 */
	public void setFrameDuration(int frameDuration) {
		this.frameDuration = frameDuration;
	}

	/**
	 * Push a task to th worker task list.
	 * 
	 * @param task
	 */
	public void pushTask(WorkerTask task) {
		synchronized (mutex) {
			tasks.addElement(task);
		}
	}

	/**
	 * Remove the first instance of a {@link WorkerTask} instance from task list
	 * 
	 * @param task
	 * @return <code>true</code> if the task is correctly removed
	 */
	public boolean removeTask(WorkerTask task) {
		synchronized (mutex) {
			int taskIndex = tasks.indexOf(task);
			if (taskIndex != -1) {
				if (taskIndex == runningTaskIndex) {
					throw new IllegalArgumentException("A WorkerTask couldn't remove itself");
				}
				tasks.removeElementAt(taskIndex);
				if (taskIndex < runningTaskIndex) {
					runningTaskIndex--;
				}
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Remove all instances of {@link WorkerTask} from task list. The action is
	 * possible only if the worker is not running.
	 */
	public void removeAllTasks() {
		if (!isRunning()) {
			tasks.removeAllElements();
		}
	}

	/**
	 * Start the worker
	 */
	public void start() {
		if (!running) {
			running = true;
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Stop the worker
	 */
	public void stop() {
		running = false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (running) {
			WorkerTask task = null;
			long startTime = System.currentTimeMillis();
			try {
				synchronized (mutex) {
					if (!tasks.isEmpty()) {
						for (runningTaskIndex = 0; runningTaskIndex < tasks.size(); ++runningTaskIndex) {
							task = (WorkerTask) tasks.elementAt(runningTaskIndex);
							if (task.run()) {
								tasks.removeElementAt(runningTaskIndex);
								runningTaskIndex--;
							}
						}
						runningTaskIndex = -1;
					}
				}
			} catch (Exception e) {
				if (workerErrorListener != null) {
					workerErrorListener.onWorkerException(task, e);
				} else {
					e.printStackTrace();
				}
				// Remove the task responsible of the exception
				if (task != null) {
					runningTaskIndex = -1;
					removeTask(task);
				}
			} catch (Error e) {
				if (workerErrorListener != null) {
					workerErrorListener.onWorkerError(task, e);
				} else {
					e.printStackTrace();
				}
				// Remove the task responsible of the error
				if (task != null) {
					runningTaskIndex = -1;
					removeTask(task);
				}
			}
			if (running) {
				long executionTime = System.currentTimeMillis() - startTime;
				try {
					Thread.sleep(!running || executionTime > frameDuration ? 1 : frameDuration - executionTime);
				} catch (InterruptedException e) {
				}
			}
		}
		thread = null;
	}
	
}
