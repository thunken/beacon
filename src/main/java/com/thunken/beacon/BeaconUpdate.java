package com.thunken.beacon;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Locale;
import java.util.Objects;

/**
 * Enumerated type for BEACON dump update frequencies. The {@link BeaconMetaField#UPDATE} meta field specifies how
 * frequently a link dump is likely to change. The field corresponds to the {@code <changefreq>} element in
 * <a href="https://www.sitemaps.org/protocol.html#xmlTagDefinitions" target="_top">Sitemaps XML format</a>.
 *
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#update" target=
 *      "_top">https://gbv.github.io/beaconspec/beacon.html#update</a>
 */
public enum BeaconUpdate {

	ALWAYS(Duration.ZERO),
	DAILY(Duration.ofDays(1L)),
	HOURLY(Duration.ofHours(1L)),
	MONTHLY(Period.ofMonths(1)),
	NEVER(Duration.ofSeconds(Long.MAX_VALUE, 999_999_999L)),
	WEEKLY(Period.ofWeeks(1)),
	YEARLY(Period.ofYears(1));

	private final TemporalAmount temporalAmount;

	BeaconUpdate(final TemporalAmount temporalAmount) {
		this.temporalAmount = Objects.requireNonNull(temporalAmount, "temporalAmount is null");
	}

	public TemporalAmount getTemporalAmount() {
		return temporalAmount;
	}

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static BeaconUpdate of(final String string) {
		Objects.requireNonNull(string, "string is null");
		return valueOf(string.toUpperCase(Locale.ROOT));
	}

}
