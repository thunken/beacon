package com.thunken.beacon;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.damnhandy.uri.template.Expression;
import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;

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
public enum BeaconMetaField implements Predicate<String> {

	ANNOTATION(Type.LINK_CONSTRUCTION, ValueType.URI),
	CONTACT(Type.LINK_DUMP, ValueType.STRING),
	CREATOR(Type.LINK_DUMP, ValueType.STRING),
	DESCRIPTION(Type.LINK_DUMP, ValueType.STRING),
	FEED(Type.LINK_DUMP, ValueType.URL),
	FORMAT(Type.LINK_DUMP, "BEACON", ValueType.BEACON),
	HOMEPAGE(Type.LINK_DUMP, ValueType.URL),
	INSTITUTION(Type.TARGET_DATASET, ValueType.URI),
	MESSAGE(Type.LINK_CONSTRUCTION, ValueType.STRING),
	NAME(Type.TARGET_DATASET, ValueType.STRING),
	PREFIX(Type.LINK_CONSTRUCTION, BeaconParser.RESERVED_EXPANSION, ValueType.URI_PATTERN),
	RELATION(Type.LINK_CONSTRUCTION, "http://www.w3.org/2000/01/rdf-schema#seeAlso", ValueType.URI_PATTERN),
	SOURCESET(Type.SOURCE_DATASET, ValueType.URI),
	TARGET(Type.LINK_CONSTRUCTION, BeaconParser.RESERVED_EXPANSION, ValueType.URI_PATTERN),
	TARGETSET(Type.TARGET_DATASET, ValueType.URI),
	TIMESTAMP(Type.LINK_DUMP, ValueType.TIMESTAMP),
	UPDATE(Type.LINK_DUMP, ValueType.UPDATE);

	public static final String DEFAULT_META_VALUE = "";

	@NonNull
	private final Type type;

	@NonNull
	private final String defaultValue;

	@NonNull
	private final ValueType valueType;

	private BeaconMetaField(@NonNull final Type type, @NonNull final ValueType valueType) {
		this(type, DEFAULT_META_VALUE, valueType);
	}

	/**
	 * Returns an immutable singleton set containing the default value for this meta field.
	 *
	 * @return an immutable singleton set containing the default value for this meta field.
	 * @deprecated
	 */
	@Deprecated
	public Set<String> getDefaultValues() {
		return Collections.singleton(defaultValue);
	}

	/**
	 * Returns {@code true} if this meta field is repeatable.
	 *
	 * @return {@code false}
	 * @deprecated Meta fields are not repeatable anymore, so this method always returns {@code false}.
	 */
	@Deprecated
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean test(final String value) {
		return valueType.test(value);
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

	public enum ValueType implements Predicate<String> {

		BEACON {
			@Override
			public boolean test(final String value) {
				return Objects.equals(value, "BEACON");
			}
		},
		STRING {
			@Override
			public boolean test(final String value) {
				return value != null;
			}
		},
		TIMESTAMP {
			@Override
			public boolean test(final String value) {
				return value != null && (BeaconMetaField.test(value, DateTimeFormatter.ISO_LOCAL_DATE)
						|| BeaconMetaField.test(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			}
		},
		UPDATE {
			@Override
			public boolean test(final String value) {
				return value != null
						&& Arrays.stream(BeaconUpdate.values()).map(BeaconUpdate::toString).anyMatch(value::equals);
			}
		},
		URI {
			@Override
			public boolean test(final String value) {
				if (value == null) {
					return false;
				}
				try {
					new URI(value);
				} catch (final URISyntaxException e) {
					return false;
				}
				return true;
			}
		},
		URI_PATTERN {
			@Override
			public boolean test(final String value) {
				if (value == null) {
					return false;
				}
				final UriTemplate template;
				try {
					template = UriTemplate.fromTemplate(value);
				} catch (final MalformedUriTemplateException e) {
					return false;
				}
				for (final Expression expression : template.getExpressions()) {
					if (!BeaconParser.VALID_EXPRESSIONS.contains(expression.getValue())) {
						return false;
					}
				}
				return true;
			}
		},
		URL {
			@Override
			public boolean test(final String value) {
				if (value == null) {
					return false;
				}
				try {
					new URL(value);
				} catch (final MalformedURLException e) {
					return false;
				}
				return true;
			}
		};

	}

	private static boolean test(@NonNull final String value, @NonNull final DateTimeFormatter formatter) {
		try {
			formatter.parse(value);
		} catch (final DateTimeParseException e) {
			return false;
		}
		return true;
	}

}
