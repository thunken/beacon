package com.thunken.beacon;

/**
 * Thrown to indicate that a method has been passed data that violates the BEACON specification.
 *
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#beacon-format" target=
 *      "_top">https://gbv.github.io/beaconspec/beacon.html#beacon-format</a>
 */
public class BeaconFormatException extends RuntimeException {

	private static final long serialVersionUID = 567805021545435595L;

	/**
	 * Constructs a {@link BeaconFormatException} with {@code null} as its error detail message.
	 */
	public BeaconFormatException() {
		super();
	}

	/**
	 * Constructs a {@link BeaconFormatException}, using the parameters to build the detail message.
	 * <p>
	 * This constructor is typically used when a method has been passed a {@link String} value that violates the BEACON
	 * specification for the specified {@link BeaconMetaField}.
	 *
	 * @param metaField
	 *            A meta field.
	 * @param metaValue
	 *            A value that was incorrectly tied to the specified meta field.
	 */
	public BeaconFormatException(final BeaconMetaField metaField, final String metaValue) {
		this(String.format("Expected value of type [%s] for meta field [%s], got [%s]", metaField.getValueType(),
				metaField, metaValue));
	}

	/**
	 * Constructs a {@link BeaconFormatException} with the specified detail message.
	 *
	 * @param message
	 *            The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
	 */
	public BeaconFormatException(final String message) {
		super(message);
	}

	/**
	 * Constructs a {@link BeaconFormatException} with the specified cause and a detail message of
	 * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
	 * {@code cause}).
	 *
	 * @param cause
	 *            The cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
	 *            value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public BeaconFormatException(final Throwable cause) {
		super(cause);
	}

}
