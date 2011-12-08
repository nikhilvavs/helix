package com.linkedin.clustermanager.alerts;

import java.util.Iterator;
import java.util.List;

public class SumOperator extends Operator {

	public SumOperator() {
		minInputTupleLists = 2;
		maxInputTupleLists = 2;
		inputOutputTupleListsCountsEqual = false;
		numOutputTupleLists = 1;
	}

	@Override
	public List<Iterator<Tuple<String>>> execute(List<Iterator<Tuple<String>>> input) {
		// TODO Auto-generated method stub
		return null;
	}

}
