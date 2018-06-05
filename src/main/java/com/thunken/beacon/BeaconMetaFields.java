package com.thunken.beacon;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Custom collection to hold multiple BEACON meta fields and their respective values.
 * <p>
 * {@link BeaconMetaFields} instances are shared by a {@link BeaconParser} and all {@link BeaconLink} instances
 * generated by that parser. Therefore, for the sake of consistency, the public API for this class only includes
 * read-only methods.
 * <p>
 * Note: the current Javadoc for this project is incomplete. We rely on
 * <a href="https://projectlombok.org/" target="_top">Lombok</a> to generate boilerplate code, and Lombok does not plug
 * into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and constructors
 * may be incomplete. See <a href="https://projectlombok.org/features/delombok" target="_top">delombok</a> and
 * <a href="https://github.com/thunken/beacon/issues/1" target="_top">beacon#1</a> for more information.
 *
 * @see BeaconMetaField
 */
@EqualsAndHashCode
public final class BeaconMetaFields {

	private final Map<BeaconMetaField, String> fields = new EnumMap<>(BeaconMetaField.class);

	/**
	 * Returns the value to which the specified field is mapped.
	 *
	 * @return The value to which the specified field is mapped.
	 * @throws IllegalArgumentException
	 *             If {@code field} is mapped to multiple values.
	 * @throws NullPointerException
	 *             If {@code field} is null.
	 */
	public String getValue(@NonNull final BeaconMetaField field) {
		return fields.getOrDefault(field, field.getDefaultValue());
	}

	/**
	 * Returns all values to which the specified field is mapped.
	 *
	 * @return All values to which the specified field is mapped.
	 * @throws NullPointerException
	 *             If {@code field} is null.
	 * @deprecated Meta fields are not repeatable anymore, so this method returns an immutable set containing the only
	 *             value to which the specified field is mapped.
	 * @see BeaconMetaFields#getValue(BeaconMetaField)
	 */
	@Deprecated
	public Set<String> getValues(@NonNull final BeaconMetaField field) {
		return Collections.singleton(getValue(field));
	}

	/**
	 * Returns {@code true} if the specified field is mapped to its default value.
	 *
	 * @return {@code true} if the specified field is mapped to its default value.
	 * @throws NullPointerException
	 *             If {@code field} is null.
	 */
	public boolean isDefault(@NonNull final BeaconMetaField field) {
		return fields.containsKey(field) ? fields.get(field).equals(field.getDefaultValue()) : true;
	}

	@Override
	public String toString() {
		return Arrays.stream(BeaconMetaField.values()).map(field -> (field + "=" + getValue(field)))
				.collect(Collectors.joining(", ", "{", "}"));
	}

	void put(@NonNull final BeaconMetaField field, @NonNull final String value) {
		if (!field.test(value)) {
			throw new BeaconFormatException(field, value);
		}
		if (!value.equals(field.getDefaultValue())) {
			fields.put(field, value);
		}
	}

}
