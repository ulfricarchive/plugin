package com.ulfric.plugin;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;

public class FeatureFeature extends Feature {

	@Override
	public Application apply(Object feature) {
		return feature instanceof Feature ? new FeatureApplication((Feature) feature) : null;
	}

}