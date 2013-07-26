/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package twitter2me.internal.logging;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.4
 */
final class NullLogger extends Logger {

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String message, final String message2) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String message, final Throwable th) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String message, final String message2) {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebugEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isErrorEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfoEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarnEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String message, final String message2) {
	}
}
