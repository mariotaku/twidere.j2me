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
 * Creation date : 21 nov. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util.frame;

import org.kalmeo.util.LinkedList;
import org.kalmeo.util.LinkedListItem;

/**
 * The FrameHandler id the base of the 'Frame' mecanism. It permits to process
 * messages through separate object instance called <b>Frame</b> and organized
 * into a stack.<br>
 * The last pushed frame of this FrameHandler will be invoke to process a new
 * message first. If it doesn't use it, the message is pass to the next frame
 * untile a frame returns false on its onMessage(...) call.
 * 
 * @author bbeaulant
 */
public class FrameHandler {

	/**
	 * This class add to a {@link Frame} the linked list concept
	 */
	private class LinkedFrame implements LinkedListItem {

		// Th associated Frame
		private Frame frame;
		
		// Define if the LinkedFrame need to be removed on next remove process
		private boolean removable = false;

		// Linked list variables
		private LinkedFrame previous;
		private LinkedFrame next;

		/**
		 * Construct a {@link LinkedFrame}
		 * 
		 * @param frame the associated {@link Frame}
		 */
		public LinkedFrame(Frame frame) {
			this.frame = frame;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.util.LinkedListItem#getNext()
		 */
		public LinkedListItem getNext() {
			return next;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.util.LinkedListItem#getPrevious()
		 */
		public LinkedListItem getPrevious() {
			return previous;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.util.LinkedListItem#setNext(org.kalmeo.util.LinkedListItem)
		 */
		public void setNext(LinkedListItem next) {
			this.next = (LinkedFrame) next;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.util.LinkedListItem#setPrevious(org.kalmeo.util.LinkedListItem)
		 */
		public void setPrevious(LinkedListItem previous) {
			this.previous = (LinkedFrame) previous;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.util.LinkedListItem#compareTo(org.kalmeo.util.LinkedListItem, int)
		 */
		public int compareTo(LinkedListItem item, int flag) {
			return 0;
		}

	}

	// The frame stack
	private final LinkedList frames = new LinkedList();
	
	// Number of current running processMessage
	private int runningProcessMessageCount = 0;

	// Number of removable linkedFrame
	private int removableLinkedFrameCount = 0;

	// Synchronization mutex
	private final Object mutex = new Object(); 
	
	/**
	 * Returns {@link LinkedFrame} associated withe the given <code>frame</code>,
	 * or null if the <code>frame</code> is not in the stack.
	 * 
	 * @param frame
	 * @return The {@link LinkedFrame} associated withe the given
	 *         <code>frame</code>, or null if the <code>frame</code> is not
	 *         in the stack
	 */
	private LinkedFrame getLinkedFrame(Frame frame) {
		synchronized (mutex) {
			for (LinkedFrame linkedFrame = (LinkedFrame) frames.getFirst(); linkedFrame != null; linkedFrame = linkedFrame.next) {
				if (linkedFrame.frame == frame && linkedFrame.removable == false) {
					return linkedFrame;
				}
			}
		}
		return null;
	}

	/**
	 * Returns th {@link Frame} on top of the frames stack, or null if the
	 * {@link FrameHandler} is empty.
	 * 
	 * @return the {@link Frame} on top of the stack
	 */
	public Frame getTopFrame() {
		synchronized (mutex) {
			if (!frames.isEmpty()) {
				for (LinkedFrame linkedFrame = (LinkedFrame) frames.getLast(); linkedFrame != null; linkedFrame = linkedFrame.previous) {
					// Ignore removable linkedFrames
					if (!linkedFrame.removable) {
						return linkedFrame.frame;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Push a frame on top of the frames stack
	 * 
	 * @param frame the {@link Frame} to push
	 * @return <code>true</code> if frame is pushed or <code>false</code> if
	 *         the frame was already present in to the stack
	 */
	public boolean pushFrame(Frame frame) {
		synchronized (mutex) {
			if (getLinkedFrame(frame) == null) {
				LinkedFrame linkedFrame = new LinkedFrame(frame);
				frames.add(linkedFrame);
				frame.onAdded();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove a frame ont top of the frames stack
	 * 
	 * @param frame the {@link Frame} to remove
	 * @return <code>true</code> if frame is removed or <code>false</code> if
	 *         the frame wasn't present in to the stack
	 */
	public boolean removeFrame(Frame frame) {
		synchronized (mutex) {
			LinkedFrame linkedFrame = getLinkedFrame(frame);
			if (linkedFrame != null) {
				internalLinkedFrameRemoveProcess(linkedFrame);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all frames.
	 */
	public void removeAllFrames() {
		synchronized (mutex) {
			LinkedFrame tmpLinkedFrame;
			// Remove all frames from the top to bottom
			for (LinkedFrame linkedFrame = (LinkedFrame) frames.getLast(); linkedFrame != null; ) {
				tmpLinkedFrame = linkedFrame.previous;
				internalLinkedFrameRemoveProcess(linkedFrame);
				linkedFrame = tmpLinkedFrame;
			}
		}
	}

	/**
	 * Remove all frames on top of the given <code>frame</code>. The
	 * <code>frame</code> is not removed and become the new top frame.
	 * 
	 * @param frame the reference {@link Frame}
	 */
	public void removeAllFrameOnTopOf(Frame frame) {
		synchronized (mutex) {
			LinkedFrame newTopLinkedFrame = getLinkedFrame(frame);
			if (newTopLinkedFrame != null) {
				LinkedFrame tmpLinkedFrame;
				for (LinkedFrame linkedFrame = newTopLinkedFrame.next; linkedFrame != null;) {
					tmpLinkedFrame = linkedFrame.next;
					internalLinkedFrameRemoveProcess(linkedFrame);
					linkedFrame = tmpLinkedFrame;
				}
			}
		}
	}
	
	/**
	 * Internal linkedFrame remove process invoked by all removeFrameXX methods.
	 * 
	 * @param linkedFrame
	 */
	private void internalLinkedFrameRemoveProcess(LinkedFrame linkedFrame) {
		if (runningProcessMessageCount == 0) {
			frames.remove(linkedFrame);
		} else {
			linkedFrame.removable = true;
			removableLinkedFrameCount++;
		}
		linkedFrame.frame.onRemoved();
	}

	/**
	 * Process a message into the frame stack.
	 * 
	 * @param identifier the message identifier
	 * @param arguments the message arguments
	 * @return <code>true</code> if the message has been processed by a frame.
	 */
	public boolean processMessage(Object identifier, Object[] arguments) {
		boolean messageProcessed = false;
		synchronized (mutex) {
			
			// Message process
			runningProcessMessageCount++;
			LinkedFrame linkedFrame = (LinkedFrame) frames.getLast();
			while (linkedFrame != null) {
				if (!linkedFrame.removable && !linkedFrame.frame.onMessage(identifier, arguments)) {
					messageProcessed = true;
					break;
				}
				linkedFrame = linkedFrame.previous;
			}
			runningProcessMessageCount--;
			
			// Remove process
			if (runningProcessMessageCount == 0 && removableLinkedFrameCount != 0) {
				
				LinkedFrame nextLinkedFrame = null;
				linkedFrame = (LinkedFrame) frames.getFirst();
				while (linkedFrame != null) {
					if (linkedFrame.removable) {
						nextLinkedFrame = linkedFrame.next;
						frames.remove(linkedFrame);
						linkedFrame = nextLinkedFrame;
					} else {
						linkedFrame = linkedFrame.next;
					}
				}
				removableLinkedFrameCount = 0;
				
			}
			
		}
		return messageProcessed;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("framesHandler :");
		for (LinkedFrame linkedFrame = (LinkedFrame) frames.getLast(); linkedFrame != null; linkedFrame = linkedFrame.previous) {
			String className = linkedFrame.frame.getClass().getName();
			buffer.append("\n > ").append(className.substring(className.lastIndexOf('.') + 1));
		}
		return buffer.toString();
	}

}
