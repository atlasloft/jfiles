/*
 * Copyright (C) 2016 - WSU CEG3120 Students
 * 
 * Roberto C. Sánchez <roberto.sanchez@wright.edu>
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package edu.wright.cs.jfiles.exception;

/**
 * Class used to process general ShellExceptions that can occur.
 *
 */
public class ShellException extends RuntimeException {

	/**
	 * Default auto-generated UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Outputs the message for the shell exception.
	 * 
	 * @param message
	 *            the message to output
	 */
	public ShellException(String message) {
		super(message);
	}

	/**
	 * Outputs the message for the shell exception and throws an error.
	 * 
	 * @param message
	 *            the message to output
	 * @param th
	 *            the error to throw
	 */
	public ShellException(String message, Throwable th) {
		super(message, th);
	}
}
