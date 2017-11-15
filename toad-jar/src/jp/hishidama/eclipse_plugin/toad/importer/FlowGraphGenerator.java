package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;

public abstract class FlowGraphGenerator {

	private Class<FlowDescription> description;
	private List<String> parameterNames;
	private Map<String, Object> parameterValues;

	public abstract void checkVersion() throws NoClassDefFoundError;

	@SuppressWarnings("unchecked")
	public FlowGraph analyze(String className, List<String> parameterNames, Map<String, Object> parameterValues)
			throws Exception {
		try {
			this.description = (Class<FlowDescription>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.parameterNames = parameterNames;
		this.parameterValues = parameterValues;

		Constructor<? extends FlowDescription> ctor = findConstructor(description);

		return doAanalyze(description, ctor);
	}

	public abstract FlowGraph doAanalyze(Class<FlowDescription> description, Constructor<? extends FlowDescription> ctor)
			throws Exception;

	protected Constructor<? extends FlowDescription> findConstructor(Class<FlowDescription> description) {
		@SuppressWarnings("unchecked")
		Constructor<? extends FlowDescription>[] ctors = (Constructor<? extends FlowDescription>[]) description
				.getConstructors();
		if (ctors.length == 0) {
			throw new RuntimeException("constructor not found");
		}
		return ctors[0];
	}

	protected List<Parameter> parseRawParameters(Constructor<?> ctor) {
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

	protected static class Parameter {
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

	protected Class<?> getParameterizedType(Type type) {
		ParameterizedType ptype = (ParameterizedType) type;
		Type[] args = ptype.getActualTypeArguments();
		return (Class<?>) args[0];
	}

	protected FlowDescription newInstance(Constructor<? extends FlowDescription> ctor, List<Object> ports,
			List<String> argNameList) throws Exception {
		List<Object> args = new ArrayList<Object>(ports.size() + argNameList.size());
		{
			args.addAll(ports);
			for (String argName : argNameList) {
				String key = String.format("%s#%s", description.getName(), argName);
				Object value = parameterValues.get(key);
				args.add(value);
			}
		}

		try {
			return ctor.newInstance(args.toArray());
		} catch (Exception e) {
			throw new Exception("FlowGraphGenerator#newIsntance" + args, e);
		}
	}
}
