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

/**
 * @author bbeaulant
 */
public interface WorkerTask {

	/**
	 * Call by the {@link Worker} when the {@link WorkerTask} need to be
	 * execute.
	 * 
	 * @return <code>true</code> if the task need to be removed after execute
	 */
	public boolean run();

}
