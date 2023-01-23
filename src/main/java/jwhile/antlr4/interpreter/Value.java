package jwhile.antlr4.interpreter;

public class Value {

	public static Value NULL = new Value(null);

	final Object value;

	public Value(Object value) {
		if (value instanceof Value) {
			throw new IllegalStateException("Undesired recurrsion.");
		}
		this.value = value;
	}

	public Boolean asBoolean() {
		return (Boolean) value;
	}

	public Double asDouble() {
		return (Double) value;
	}

	public String asString() {
		return String.valueOf(value);
	}
	
	public Integer asInteger() {
		return (Integer) value;
	}

	public boolean isDouble() {
		if(value == null) {
			return false;
		}
		return value instanceof Double;
	}

	public boolean isInteger() {
		if(value == null) {
			return false;
		}
		return value instanceof Integer;
	}
	
	public boolean isBoolean() {
		if(value == null) {
			return false;
		}
		return value instanceof Boolean;
	}

	public boolean isNull() {
		return value == null;
	}

	@Override
	public int hashCode() {
		if (value == null) {
			return 0;
		}
		return this.value.hashCode();
	}
	
	public boolean isTruthyValue() {
		if (isBoolean()) {
			return asBoolean();
		} else {
			if (isInteger()) {
				return asInteger() > 0;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (value == o) {
			return true;
		}
		if(o==null) {
			return false;
		}
		
		if (value == null || o == null || o.getClass() != this.getClass()) {
			return false;
		}

		Value that = (Value) o;

		return this.value.equals(that.value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static boolean isEqual(Value a, Value b) {
		if (a.value == b.value) {
			return true;
		} else if (a.value == null || b.value == null) {
			return false;
		} else {
			return a.value.equals(b.value);
		}
	}
}
