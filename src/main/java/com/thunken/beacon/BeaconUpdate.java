package com.thunken.beacon;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Enumerated type for BEACON dump update frequencies. The {@link BeaconMetaField#UPDATE} meta field specifies how
 * frequently a link dump is likely to change. The field corresponds to the {@code <changefreq>} element in
 * <a href="https://www.sitemaps.org/protocol.html#xmlTagDefinitions" target="_top">Sitemaps XML format</a>.
 *
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#update"
 *      target="_top">https://gbv.github.io/beaconspec/beacon.html#update</a>
 */
@Getter
@RequiredArgsConstructor
public enum BeaconUpdate {

	ALWAYS(Duration.ZERO),
	HOURLY(Duration.ofHours(1L)),
	DAILY(Duration.ofDays(1L)),
	WEEKLY(Period.ofWeeks(1)),
	MONTHLY(Period.ofMonths(1)),
	YEARLY(Period.ofYears(1)),
	NEVER(Duration.ofSeconds(Long.MAX_VALUE, 999_999_999L));

	@NonNull
	private final TemporalAmount temporalAmount;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static BeaconUpdate of(@NonNull final String string) {
		return valueOf(string.toUpperCase(Locale.ROOT));
	}

}
