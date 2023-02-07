package jwhile.antlr4.analysis.algorithms;

import org.javatuples.Pair;

import jwhile.antlr4.cfg.entities.Label;

public class AnalysisInformation<T extends Comparable<?>> {

	private T value;

	public AnalysisInformation(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		if(value==null) {
			return super.hashCode();
		}
		return value.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.value == null) {
			return false;
		}
		if (!(obj instanceof AnalysisInformation)) {
			return false;
		}
		AnalysisInformation other = (AnalysisInformation) obj;
		return this.value.equals(other.value);
	}

	@Override
	public String toString() {
		if (value == null) {
			return "null";
		}
		if(value instanceof Pair){
			@SuppressWarnings("unchecked")
			Pair<Label, String> pair = (Pair<Label, String>) value;
			return "<"+pair.getValue0().getIntLabel()+","+pair.getValue1()+">";
		}
		return this.value.toString();
	}

}
