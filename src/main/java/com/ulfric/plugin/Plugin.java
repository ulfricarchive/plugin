package com.ulfric.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.dragoon.application.Hookable;
import com.ulfric.dragoon.application.ThreadClassLoaderState;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.value.Result;
import com.ulfric.tryto.TryTo;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class Plugin extends JavaPlugin implements Extensible<Class<?>>, Hookable {

	static final Class<?> LOADER_TYPE = Plugin.class.getClassLoader().getClass();
	static final Field PLUGIN_FIELD = FieldHelper.getDeclaredField(LOADER_TYPE, "plugin")
			.orElse(null);

	protected static final ObjectFactory FACTORY = new ObjectFactory();

	static {
		if (PLUGIN_FIELD != null) {
			PLUGIN_FIELD.setAccessible(true);
		}

		Feature.register(new FeatureFeature());
	}

	public static Plugin getProvidingPlugin(Object value) {
		Objects.requireNonNull(value, "value");

		if (value instanceof Plugin) {
			return (Plugin) value;
		}

		if (value instanceof ClassLoader) {
			return getProvidedPlugin((ClassLoader) value);
		}

		Class<?> type;
		if (value instanceof Class) {
			type = (Class<?>) value;
		} else {
			type = value.getClass();
		}

		return getProvidingPlugin(type);
	}

	public static Plugin getProvidingPlugin(Class<?> type) {
		return getProvidedPlugin(type.getClassLoader());
	}

	private static Plugin getProvidedPlugin(ClassLoader loader) {
		if (loader == null) {
			return null;
		}

		if (PLUGIN_FIELD == null) {
			return null;
		}

		if (LOADER_TYPE.isInstance(loader)) {
			Object plugin = TryTo.get(() -> PLUGIN_FIELD.get(loader));
			if (plugin instanceof Plugin) {
				return (Plugin) plugin;
			}
			return null;
		}

		return getProvidedPlugin(loader.getParent());
	}

	public static <T extends Plugin> T getPluginInstance(Class<T> type) {
		JavaPlugin plugin = JavaPlugin.getPlugin(type);
		return type.isInstance(plugin) ? type.cast(plugin) : null;
	}

	private final PluginContainer container;
	private final ThreadClassLoaderState state = new ThreadClassLoaderState(getClassLoader());

	public Plugin() {
		Class<? extends PluginContainer> containerType = Classes.translate(PluginContainer.class, getClassLoader());
		container = FACTORY.request(containerType, this);
	}

	@Override
	public final Result install(Class<?> application) {
		return container.install(application);
	}

	@Override
	public final void addBootHook(Runnable hook) {
		container.addBootHook(hook);
	}

	@Override
	public final void addShutdownHook(Runnable hook) {
		container.addShutdownHook(hook);
	}

	public final void log(String message) {
		getLogger().info(message);
	}

	@Override
	public final void onLoad() {
	}

	@Override
	public final void onEnable() {
		state.doContextual(container::boot);
	}

	@Override
	public final void onDisable() {
		state.doContextual(container::shutdown);
	}

}