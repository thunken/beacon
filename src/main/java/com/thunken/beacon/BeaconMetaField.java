package com.thunken.beacon;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Enumerated type for BEACON meta fields.
 * <p>
 * Note: the current Javadoc for this project is incomplete. We rely on
 * <a href="https://projectlombok.org/" target="_top">Lombok</a> to generate boilerplate code, and Lombok does not plug
 * into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and constructors
 * may be incomplete. See <a href="https://projectlombok.org/features/delombok" target="_top">delombok</a> and
 * <a href="https://github.com/thunken/beacon/issues/1" target="_top">beacon#1</a> for more information.
 *
 * @see BeaconMetaField.Type
 * @see BeaconMetaFields
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#meta-fields"
 *      target="_top">https://gbv.github.io/beaconspec/beacon.html#meta-fields</a>
 */
@Getter
@RequiredArgsConstructor
public enum BeaconMetaField {

	ANNOTATION(Type.LINK_CONSTRUCTION, false),
	CONTACT(Type.LINK_DUMP, true),
	CREATOR(Type.LINK_DUMP, true),
	DESCRIPTION(Type.LINK_DUMP, true),
	FEED(Type.LINK_DUMP, true),
	FORMAT(Type.LINK_DUMP, true, "BEACON"),
	HOMEPAGE(Type.LINK_DUMP, true),
	INSTITUTION(Type.TARGET_DATASET, true),
	MESSAGE(Type.LINK_CONSTRUCTION, false),
	NAME(Type.TARGET_DATASET, true),
	PREFIX(Type.LINK_CONSTRUCTION, false, BeaconParser.RESERVED_EXPANSION),
	RELATION(Type.LINK_CONSTRUCTION, false, "rdfs:seeAlso"),
	SOURCESET(Type.SOURCE_DATASET, false),
	TARGET(Type.LINK_CONSTRUCTION, false, BeaconParser.RESERVED_EXPANSION),
	TARGETSET(Type.TARGET_DATASET, false),
	TIMESTAMP(Type.LINK_DUMP, true),
	UPDATE(Type.LINK_DUMP, false);

	public static final String DEFAULT_META_VALUE = "";

	@NonNull
	private final Type type;

	private final boolean repeatable;

	@NonNull
	private final String defaultValue;

	private BeaconMetaField(@NonNull final Type type, final boolean repeatable) {
		this(type, repeatable, DEFAULT_META_VALUE);
	}

	/**
	 * Returns an immutable singleton set containing the default value for this meta field.
	 *
	 * @return an immutable singleton set containing the default value for this meta field.
	 */
	public Set<String> getDefaultValues() {
		return Collections.singleton(defaultValue);
	}

	/**
	 * Enumerated type for BEACON meta field types.
	 * <p>
	 * Note: the current Javadoc for this project is incomplete. We rely on
	 * <a href="https://projectlombok.org/" target="_top">Lombok</a> to generate boilerplate code, and Lombok does not
	 * plug into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and
	 * constructors may be incomplete. See
	 * <a href="https://projectlombok.org/features/delombok" target="_top">delombok</a> and
	 * <a href="https://github.com/thunken/beacon/issues/1" target="_top">beacon#1</a> for more information.
	 *
	 * @see BeaconMetaField
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#meta-fields"
	 *      target="_top">https://gbv.github.io/beaconspec/beacon.html#meta-fields</a>
	 */
	public enum Type {

		LINK_CONSTRUCTION,
		LINK_DUMP,
		SOURCE_DATASET,
		TARGET_DATASET;

	}

}
