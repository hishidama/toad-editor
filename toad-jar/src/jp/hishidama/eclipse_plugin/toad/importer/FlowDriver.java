package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.asakusafw.compiler.flow.FlowDescriptionDriver;
import com.asakusafw.vocabulary.external.ExporterDescription;
import com.asakusafw.vocabulary.external.ImporterDescription;
import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;

/**
 * @see com.asakusafw.compiler.flow.JobFlowDriver
 */
public class FlowDriver {

	private Class<FlowDescription> description;
	private List<String> parameterNames;
	private Map<String, Object> parameterValues;
	private List<Object> ports;

	@SuppressWarnings("unchecked")
	public FlowDriver(String className, List<String> parameterNames, Map<String, Object> parameterValues) {
		try {
			this.description = (Class<FlowDescription>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.parameterNames = parameterNames;
		this.parameterValues = parameterValues;
	}

	public FlowGraph analyze() throws Exception {
		Constructor<? extends FlowDescription> ctor = findConstructor();
		List<String> argNameList = new ArrayList<String>();
		FlowDescriptionDriver driver = parseParameters(ctor, argNameList);
		this.ports = driver.getPorts();
		List<Object> args = new ArrayList<Object>(ports.size() + argNameList.size());
		{
			args.addAll(ports);
			for (String argName : argNameList) {
				String key = String.format("%s#%s", description.getName(), argName);
				Object value = parameterValues.get(key);
				args.add(value);
			}
		}
		FlowDescription instance = newInstance(ctor, args);
		FlowGraph graph = driver.createFlowGraph(instance);
		return graph;
	}

	public List<Object> getPorts() {
		return ports;
	}

	protected Constructor<? extends FlowDescription> findConstructor() {
		@SuppressWarnings("unchecked")
		Constructor<? extends FlowDescription>[] ctors = (Constructor<? extends FlowDescription>[]) description
				.getConstructors();
		if (ctors.length == 0) {
			throw new RuntimeException("constructor not found");
		}
		return ctors[0];
	}

	private FlowDescriptionDriver parseParameters(Constructor<?> ctor, List<String> nameList) throws Exception {
		List<Parameter> rawParams = parseRawParameters(ctor);
		FlowDescriptionDriver driver = new FlowDescriptionDriver();
		for (Parameter raw : rawParams) {
			analyzeParameter(raw, driver, nameList);
		}
		return driver;
	}

	private void analyzeParameter(Parameter parameter, FlowDescriptionDriver driver, List<String> nameList)
			throws Exception {
		assert parameter != null;
		assert driver != null;
		if (parameter.raw == In.class) {
			analyzeInput(parameter, driver);
		} else if (parameter.raw == Out.class) {
			analyzeOutput(parameter, driver);
		} else {
			nameList.add(parameter.name);
		}
	}

	private void analyzeInput(Parameter parameter, FlowDescriptionDriver driver) throws Exception {
		String name;
		ImporterDescription importer;
		if (parameter.importer == null) {
			name = parameter.name;
			final Class<?> type = getParameterizedType(parameter.type);
			importer = new ImporterDescription() {
				@Override
				public Class<?> getModelType() {
					return type;
				}

				@Override
				public DataSize getDataSize() {
					return DataSize.UNKNOWN;
				}
			};
		} else {
			name = parameter.importer.name();
			Class<? extends ImporterDescription> clazz = parameter.importer.description();
			importer = clazz.newInstance();
		}
		driver.createIn(name, importer);
	}

	private void analyzeOutput(Parameter parameter, FlowDescriptionDriver driver) throws Exception {
		String name;
		ExporterDescription exporter;
		if (parameter.exporter == null) {
			name = parameter.name;
			final Class<?> type = getParameterizedType(parameter.type);
			exporter = new ExporterDescription() {
				@Override
				public Class<?> getModelType() {
					return type;
				}
			};
		} else {
			name = parameter.exporter.name();
			Class<? extends ExporterDescription> clazz = parameter.exporter.description();
			exporter = clazz.newInstance();

		}
		driver.createOut(name, exporter);
	}

	private Class<?> getParameterizedType(Type type) {
		ParameterizedType ptype = (ParameterizedType) type;
		Type[] args = ptype.getActualTypeArguments();
		return (Class<?>) args[0];
	}

	private FlowDescription newInstance(Constructor<? extends FlowDescription> ctor, List<?> args) throws Exception {
		try {
			return ctor.newInstance(args.toArray());
		} catch (Exception e) {
			System.err.println("FlowDriver#newIsntance" + args);
			throw e;
		}
	}

	private List<Parameter> parseRawParameters(Constructor<?> ctor) {
		assert ctor != null;
		Class<?>[] rawTypes = ctor.getParameterTypes();
		Type[] types = ctor.getGenericParameterTypes();
		Annotation[][] annotations = ctor.getParameterAnnotations();
		List<Parameter> results = new ArrayList<Parameter>();
		for (int i = 0; i < types.length; i++) {
			Import importer = null;
			Export expoter = null;
			for (Annotation a : annotations[i]) {
				if (a.annotationType() == Import.class) {
					importer = (Import) a;
				} else if (a.annotationType() == Export.class) {
					expoter = (Export) a;
				}
			}
			String name = parameterNames.get(i);
			results.add(new Parameter(name, rawTypes[i], types[i], importer, expoter));
		}
		return results;
	}

	private static class Parameter {
		public final String name;
		public final Class<?> raw;
		public final Type type;
		public final Import importer;
		public final Export exporter;

		public Parameter(String name, Class<?> raw, Type type, Import importer, Export exporter) {
			this.name = name;
			this.raw = raw;
			this.type = type;
			this.importer = importer;
			this.exporter = exporter;
		}
	}
}
