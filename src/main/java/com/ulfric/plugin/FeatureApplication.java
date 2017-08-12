package com.ulfric.plugin;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;

import java.util.Objects;

public class FeatureApplication extends Application {

	private final Feature feature;

	public FeatureApplication(Feature feature) {
		Objects.requireNonNull(feature, "feature");

		this.feature = feature;

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		Feature.register(feature);
	}

	private void unregister() {
		Feature.unregister(feature);
	}

}