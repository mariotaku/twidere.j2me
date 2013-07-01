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
 * Creation date : 27 août 08
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util.worker;

/**
 * @author omarino
 */
public interface WorkerErrorListener {
	
	/**
	 * The catched error in Worker loop
	 * 
	 * @param task
	 * @param error
	 */
	public void onWorkerError(WorkerTask task, Error error);

	/**
	 * The catched exception in Worker loop
	 * 
	 * @param exception
	 * @param task
	 */
	public void onWorkerException(WorkerTask task, Exception exception);
	
}
