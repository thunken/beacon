package com.thunken.beacon;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;

/**
 * Main class to parse BEACON dumps.
 * <p>
 * Note: the current Javadoc for this project is incomplete. We rely on
 * <a href="https://projectlombok.org/" target="_top">Lombok</a> to generate boilerplate code, and Lombok does not plug
 * into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and constructors
 * may be incomplete. See <a href="https://projectlombok.org/features/delombok" target="_top">delombok</a> and
 * <a href="https://github.com/thunken/beacon/issues/1" target="_top">beacon#1</a> for more information.
 *
 * @see BeaconLink
 * @see BeaconMetaFields
 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#beacon-format" target=
 *      "_top">https://gbv.github.io/beaconspec/beacon.html#beacon-format</a>
 */
public class BeaconParser implements Closeable, Iterator<Optional<BeaconLink>> {

	static final String RESERVED_EXPANSION = "{+ID}", SIMPLE_EXPANSION = "{ID}";

	// https://gbv.github.io/beaconspec/beacon.html#uri-patterns
	static final List<String> VALID_EXPRESSIONS = Collections
			.unmodifiableList(Arrays.asList(RESERVED_EXPANSION, SIMPLE_EXPANSION));

	private static final String DEFAULT_ANNOTATION = "";

	private static final Pattern HTTPX = Pattern.compile("^https?:", Pattern.CASE_INSENSITIVE);

	private static final Pattern METALINE = Pattern.compile("#([A-Z]+)[:\\h]\\h*(.*)$");

	// Broader than https://gbv.github.io/beaconspec/beacon.html#whitespace-normalization
	private static final Pattern WHITESPACE = Pattern.compile("\\h+");

	private final BufferedReader bufferedReader;

	private String line;

	private int lineNo;

	private final BeaconMetaFields metaFields = new BeaconMetaFields();

	private final int offset;

	/**
	 * Creates a BEACON parser that uses the specified {@link Reader}, and initializes the parser's
	 * {@link BeaconMetaFields} from the meta lines.
	 *
	 * @param reader
	 *            A character stream reader.
	 * @throws BeaconFormatException
	 *             If the parser encounters data that violates the BEACON specification.
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws NullPointerException
	 *             If {@code reader} is null.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#beacon-format" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#beacon-format</a>
	 */
	public BeaconParser(final Reader reader) throws IOException {
		this(reader, new BeaconMetaFields());
	}

	/**
	 * Creates a BEACON parser that uses the specified {@link Reader} and default {@link BeaconMetaFields}, and further
	 * initializes the parser's {@link BeaconMetaFields} from the meta lines.
	 *
	 * @param reader
	 *            A character stream reader.
	 * @param defaults
	 *            Default values for BEACON meta fields. Overwritten by meta fields parsed from the reader, if any.
	 * @throws BeaconFormatException
	 *             If the parser encounters data that violates the BEACON specification.
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws NullPointerException
	 *             If {@code reader} is null.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#beacon-format" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#beacon-format</a>
	 */
	public BeaconParser(final Reader reader, final BeaconMetaFields defaults) throws IOException {
		Objects.requireNonNull(reader, "reader is null");
		Objects.requireNonNull(defaults, "defaults is null");
		bufferedReader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
		metaFields.putAll(defaults);
		// https://gbv.github.io/beaconspec/beacon.html#beacon-format
		// Parse meta lines
		final Set<BeaconMetaField> seen = EnumSet.noneOf(BeaconMetaField.class);
		while (readLine() != null && line.startsWith("#")) {
			final Matcher matcher = METALINE.matcher(line);
			if (!matcher.matches()) {
				throw new IOException("Invalid meta line on line " + getLineNo());
			}
			final BeaconMetaField metaField;
			try {
				metaField = BeaconMetaField.valueOf(matcher.group(1));
			} catch (final IllegalArgumentException e) {
				// TODO warn
				continue;
			}
			if (seen.contains(metaField)) {
				// TODO warn
			}
			String metaValue = normalize(matcher.group(2), BeaconMetaField.DEFAULT_META_VALUE);
			switch (metaField) {
			case FORMAT:
				if (getLineNo() != 1) {
					// TODO warn
				}
				break;
			case PREFIX:
			case TARGET:
				metaValue = normalizeTemplateString(metaValue);
				break;
			default:
				break;
			}
			metaFields.put(metaField, metaValue);
			seen.add(metaField);
		}
		// Discard empty lines
		while (line != null && normalize(line, null) == null) {
			readLine();
		}
		offset = getLineNo() - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		bufferedReader.close();
		line = null;
	}

	public int getLineNo() {
		return lineNo;
	}

	/**
	 * Returns the current link number.
	 *
	 * @return The current link number.
	 */
	public int getLinkNo() {
		return getLineNo() - offset;
	}

	public BeaconMetaFields getMetaFields() {
		return metaFields;
	}

	/**
	 * Returns {@code true} if the underlying BEACON dump has more links. (In other words, returns {@code true} if
	 * {@link #next} would return an {@link Optional} rather than throwing an exception.)
	 *
	 * @return {@code true} if the underlying BEACON dump has more links.
	 */
	@Override
	public boolean hasNext() {
		return line != null;
	}

	/**
	 * Returns an {@link Optional} describing the next {@link BeaconLink} in the underlying BEACON dump, or
	 * {@link Optional#empty()} if the corresponding line cannot be parsed.
	 *
	 * @return An {@link Optional} describing the next {@link BeaconLink} in the underlying BEACON dump, or
	 *         {@link Optional#empty()} if the corresponding line cannot be parsed.
	 * @throws NoSuchElementException
	 *             If the underlying BEACON dump has no more links.
	 * @see BeaconParser#parseLine(String, BeaconMetaFields)
	 */
	@Override
	public Optional<BeaconLink> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final Optional<BeaconLink> link = parseLine(line, metaFields);
		readLine();
		return link;
	}

	/**
	 * {@link BeaconParser} provides read-only access to a BEACON dump, so {@link Iterator#remove()} is not supported.
	 *
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException("remove");
	}

	private String readLine() {
		try {
			if ((line = bufferedReader.readLine()) != null) {
				lineNo++;
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		return line;
	}

	/**
	 * Parses a BEACON link line and returns an {@link Optional} describing the corresponding {@link BeaconLink}, or
	 * {@link Optional#empty()} if the line cannot be parsed into a valid link.
	 *
	 * @param linkLine
	 *            The BEACON link line to parse.
	 * @param metaFields
	 *            The {@link BeaconMetaFields} specifying link construction rules.
	 * @return An {@link Optional} describing the corresponding {@link BeaconLink}, or {@link Optional#empty()} if the
	 *         line cannot be parsed into a valid link
	 * @throws NullPointerException
	 *             If {@code linkLine} or {@code metaFields} is null.
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#links" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#links</a>
	 * @see <a href="https://gbv.github.io/beaconspec/beacon.html#link-construction" target=
	 *      "_top">https://gbv.github.io/beaconspec/beacon.html#link-construction</a>
	 */
	public static Optional<BeaconLink> parseLine(final String linkLine, final BeaconMetaFields metaFields) {
		Objects.requireNonNull(linkLine, "linkLine is null");
		Objects.requireNonNull(metaFields, "metaFields is null");
		final String[] tokens = tokenize(linkLine);
		final String source, annotation, target;
		switch (tokens.length) {
		case 1:
			source = target = tokens[0];
			annotation = DEFAULT_ANNOTATION;
			break;
		case 2:
			if (metaFields.isDefault(BeaconMetaField.TARGET) && tokens[1] != null && HTTPX.matcher(tokens[1]).find()) {
				source = tokens[0];
				annotation = DEFAULT_ANNOTATION;
				target = tokens[1];
			} else {
				source = target = tokens[0];
				annotation = tokens[1];
			}
			break;
		case 3:
			source = tokens[0];
			annotation = tokens[1];
			target = tokens[2];
			break;
		default:
			// TODO warn
			return Optional.empty();
		}
		return source == null || target == null ? Optional.empty()
				: Optional.of(new BeaconLink(source, Optional.ofNullable(annotation).orElse(DEFAULT_ANNOTATION), target,
						metaFields));
	}

	private static String normalize(final String string, final String defaultValue) {
		// https://gbv.github.io/beaconspec/beacon.html#allowed-characters
		// https://gbv.github.io/beaconspec/beacon.html#whitespace-normalization
		return Optional.ofNullable(string).map(s -> WHITESPACE.matcher(s).replaceAll(" ")).map(String::trim)
				.filter(s -> !s.isEmpty()).map(s -> Normalizer.normalize(s, Normalizer.Form.NFKC)).orElse(defaultValue);
	}

	private static String normalizeTemplateString(final String templateString) {
		// https://gbv.github.io/beaconspec/beacon.html#uri-patterns
		if (templateString == null || templateString.isEmpty()) {
			return RESERVED_EXPANSION;
		}
		final UriTemplate template;
		try {
			template = UriTemplate.fromTemplate(templateString);
		} catch (final MalformedUriTemplateException e) {
			throw new BeaconFormatException(e);
		}
		return template.expressionCount() == 0 ? templateString + SIMPLE_EXPANSION : templateString;
	}

	private static String[] tokenize(final String string) {
		Objects.requireNonNull(string, "string is null");
		// https://gbv.github.io/beaconspec/beacon.html#beacon-format
		// https://gbv.github.io/beaconspec/beacon.html#links
		final String[] tokens = string.split("\\|", -1);
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = normalize(tokens[i], null);
		}
		return tokens;
	}

}
