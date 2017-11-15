package com.thunken.beacon;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;

import lombok.NonNull;

public final class BeaconMetaFields {

	private final SetMultimap<BeaconMetaField, String> fields = HashMultimap.create();

	public String getValue(@NonNull final BeaconMetaField field) {
		return Iterables.getOnlyElement(getValues(field));
	}

	public Set<String> getValues(@NonNull final BeaconMetaField field) {
		return fields.containsKey(field) ? fields.get(field) : field.getDefaultValues();
	}

	public boolean isDefault(@NonNull final BeaconMetaField field) {
		return fields.containsKey(field) ? fields.get(field).equals(field.getDefaultValues()) : true;
	}

	void put(@NonNull final BeaconMetaField field, @NonNull final String value) {
		if (field.isRepeatable()) {
			fields.put(field, value);
		} else {
			fields.replaceValues(field, Collections.singleton(value));
		}
	}

}
