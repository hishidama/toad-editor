package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.asakusafw.compiler.flow.FlowDescriptionDriver;
import com.asakusafw.vocabulary.external.ExporterDescription;
import com.asakusafw.vocabulary.external.ImporterDescription;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;

/**
 * @see com.asakusafw.compiler.flow.JobFlowDriver
 */
public class FlowGraphGenerator040 extends FlowGraphGenerator {

	@Override
	public void checkVersion() throws NoClassDefFoundError {
		new FlowDescriptionDriver();
	}

	@Override
	public FlowGraph doAanalyze(Class<FlowDescription> description, Constructor<? extends FlowDescription> ctor)
			throws Exception {
		List<String> argNameList = new ArrayList<String>();
		FlowDescriptionDriver driver = parseParameters(ctor, argNameList);
		List<Object> ports = driver.getPorts();
		FlowDescription instance = newInstance(ctor, ports, argNameList);
		FlowGraph graph = driver.createFlowGraph(instance);
		return graph;
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
}
