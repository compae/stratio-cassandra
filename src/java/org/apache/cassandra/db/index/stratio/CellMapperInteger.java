package org.apache.cassandra.db.index.stratio;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A {@link CellMapper} to map an integer field.
 * 
 * @author adelapena
 */
public class CellMapperInteger extends CellMapper<Integer> {

	private Float DEFAULT_BOOST = 1.0f;

	@JsonProperty("boost")
	private final Float boost;

	@JsonCreator
	public CellMapperInteger(@JsonProperty("boost") Float boost) {
		super();
		this.boost = boost == null ? DEFAULT_BOOST : boost;
	}

	@Override
	public Analyzer analyzer() {
		return EMPTY_ANALYZER;
	}

	@Override
    public Field field(String name, Object value) {
		Integer number = parseColumnValue(value);
		Field field = new IntField(name, number, STORE);
		field.setBoost(boost);
		return field;
	}

	@Override
    public Query range(String name, String start, String end, boolean startInclusive, boolean endInclusive) {
		return NumericRangeQuery.newIntRange(name,
		                                     parseQueryValue(start),
		                                     parseQueryValue(end),
		                                     startInclusive,
		                                     endInclusive);
	}
	
	@Override
    public Query match(String name, String value) {
		return NumericRangeQuery.newIntRange(name, parseQueryValue(value), parseQueryValue(value), true, true);
	}

	@Override
	protected Integer parseColumnValue(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			return Integer.valueOf(value.toString());
		} else {
			throw new MappingException("Value '%s' cannot be cast to Integer", value);
		}
	}

	@Override
	protected Integer parseQueryValue(String value) {
		if (value == null) {
			return null;
		} else {
			return Integer.valueOf(value);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnMapperInteger []");
		return builder.toString();
	}

}