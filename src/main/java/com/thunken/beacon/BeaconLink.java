package com.thunken.beacon;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import com.damnhandy.uri.template.UriTemplate;

/**
 * Main class for BEACON links. Link elements in BEACON format are given in abbreviated form of link tokens. Each link
 * is constructed from a mandatory source token, an optional annotation token, and an optional target token.
 * <p>
 * Link construction rules are based on the value of link construction {@link BeaconMetaFields}.
 * <p>
 * Note: the current Javadoc for this project is incomplete. We rely on
 * <a href="https://projectlombok.org/" target="_top">Lombok</a> to generate boilerplate code, and Lombok does not plug
 * into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and constructors
 * may be incomplete. See <a href="https://projectlombok.org/features/delombok" target="_top">delombok</a> and
 * <a href="https://github.com/thunken/beacon/issues/1" target="_top">beacon#1</a> for more information.
 *
 * @see BeaconMetaField
 * @see BeaconParser
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
 */
public final class BeaconLink {

	private final String annotationToken;

	private final BeaconMetaFields metaFields;

	private final String sourceToken;

	private final String targetToken;

	public BeaconLink(final String sourceToken, final String annotationToken, final String targetToken,
			final BeaconMetaFields metaFields) {
		this.sourceToken = Objects.requireNonNull(sourceToken, "sourceToken is null");
		this.annotationToken = annotationToken;
		this.targetToken = Objects.requireNonNull(targetToken, "targetToken is null");
		this.metaFields = Objects.requireNonNull(metaFields, "metaFields is null");
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof BeaconLink) {
			final BeaconLink other = (BeaconLink) object;
			return Objects.equals(sourceToken, other.sourceToken) //
					&& Objects.equals(annotationToken, other.annotationToken) //
					&& Objects.equals(targetToken, other.targetToken) //
					&& Objects.equals(metaFields, other.metaFields);
		}
		return false;
	}

	/**
	 * Returns this link's annotation. The annotation is constructed from the annotation token, if given, or from the
	 * {@link BeaconMetaField#MESSAGE} meta field otherwise.
	 *
	 * @return This link's annotation.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
	 */
	public String getAnnotation() {
		return Optional.ofNullable(annotationToken).orElse(metaFields.getValue(BeaconMetaField.ANNOTATION));
	}

	public String getAnnotationToken() {
		return annotationToken;
	}

	public BeaconMetaFields getMetaFields() {
		return metaFields;
	}

	/**
	 * Returns this link's relation type. The relation type is set to the {@link BeaconMetaField#RELATION} meta field if
	 * this field contains an URI. If this field contains an URI pattern, the relation type is constructed from this
	 * pattern by inserting the annotation token or the empty string if no annotation token is given.
	 *
	 * @return This link's relation type.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
	 */
	public URI getRelationType() {
		return get(BeaconMetaField.RELATION, Optional.ofNullable(annotationToken).orElse(""));
	}

	/**
	 * Returns this link's source identifier. The source identifier is constructed from the
	 * {@link BeaconMetaField#PREFIX} meta field URI pattern by inserting the source token.
	 *
	 * @return This link's source identifier.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
	 */
	public URI getSourceIdentifier() {
		return get(BeaconMetaField.PREFIX, sourceToken);
	}

	public String getSourceToken() {
		return sourceToken;
	}

	/**
	 * Returns this link's target identifier. The target identifier is constructed from the
	 * {@link BeaconMetaField#TARGET} meta field URI pattern by inserting the target token.
	 *
	 * @return This link's target identifier.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
	 */
	public URI getTargetIdentifier() {
		return get(BeaconMetaField.TARGET, targetToken);
	}

	public String getTargetToken() {
		return targetToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = result * prime + sourceToken.hashCode();
		result = result * prime + (annotationToken == null ? prime : annotationToken.hashCode());
		result = result * prime + targetToken.hashCode();
		result = result * prime + metaFields.hashCode();
		return result;
	}

	private URI get(final BeaconMetaField metaField, final String token) {
		Objects.requireNonNull(metaField, "metaField is null");
		Objects.requireNonNull(token, "token is null");
		return URI.create(UriTemplate.fromTemplate(metaFields.getValue(metaField)).set("ID", token).expand());
	}

}
