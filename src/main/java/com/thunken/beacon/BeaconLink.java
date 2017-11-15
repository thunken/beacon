package com.thunken.beacon;

import java.net.URI;
import java.util.Optional;

import com.damnhandy.uri.template.UriTemplate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class BeaconLink {

	@NonNull
	private final String sourceToken;

	private final String annotationToken;

	@NonNull
	private final String targetToken;

	@NonNull
	private final BeaconMetaFields metaFields;

	public String getAnnotation() {
		// https://gbv.github.io/beaconspec/beacon.html#link-construction
		return Optional.ofNullable(annotationToken).orElse(metaFields.getValue(BeaconMetaField.ANNOTATION));
	}

	public URI getSourceIdentifier() {
		return get(BeaconMetaField.PREFIX, sourceToken);
	}

	public URI getTargetIdentifier() {
		return get(BeaconMetaField.TARGET, targetToken);
	}

	private URI get(@NonNull final BeaconMetaField metaField, @NonNull final String token) {
		// https://gbv.github.io/beaconspec/beacon.html#link-construction
		return URI.create(UriTemplate.fromTemplate(metaFields.getValue(metaField)).set("ID", token).expand());
	}

}
