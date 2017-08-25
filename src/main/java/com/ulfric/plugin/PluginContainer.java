package com.ulfric.plugin;

import com.google.common.base.CaseFormat;

import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.loader.Loader;

import java.util.Objects;

@Loader
public class PluginContainer extends Container {

	private final String name;

	public PluginContainer(Plugin plugin) {
		Objects.requireNonNull(plugin, "plugin");

		String name = plugin.getName();
		Objects.requireNonNull(name, plugin + ".getName()");

		this.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	protected void log(String message) {
	}

}