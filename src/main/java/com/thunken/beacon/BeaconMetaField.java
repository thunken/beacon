package com.thunken.beacon;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// https://gbv.github.io/beaconspec/beacon.html#meta-fields
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

	public Set<String> getDefaultValues() {
		return Collections.singleton(defaultValue);
	}

	public enum Type {

		LINK_CONSTRUCTION,
		LINK_DUMP,
		SOURCE_DATASET,
		TARGET_DATASET;

	}

}
