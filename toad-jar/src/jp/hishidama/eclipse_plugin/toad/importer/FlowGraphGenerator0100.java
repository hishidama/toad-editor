package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.asakusafw.lang.compiler.analyzer.FlowDescriptionAnalyzer;
import com.asakusafw.vocabulary.external.ExporterDescription;
import com.asakusafw.vocabulary.external.ImporterDescription;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;

// compile group: 'com.asakusafw.lang.compiler', name: 'asakusa-compiler-tester', version: "0.4.0"
// compile group: 'com.asakusafw', name: 'asakusa-dsl-vocabulary', version: "0.4.0"
public class FlowGraphGenerator0100 extends FlowGraphGenerator {

	@Override
	public void checkVersion() throws NoClassDefFoundError {
		new FlowDescriptionAnalyzer();
	}

	@Override
	public FlowGraph doAanalyze(Class<FlowDescription> description, Constructor<? extends FlowDescription> ctor)
			throws Exception {
		List<Object> ports = new ArrayList<Object>();
		List<String> argNameList = new ArrayList<String>();
		FlowDescriptionAnalyzer analyzer = parseParameters(ctor, ports, argNameList);

		FlowDescription instance = newInstance(ctor, ports, argNameList);
		FlowGraph graph = analyzer.analyze(instance);
		return graph;
	}

	private FlowDescriptionAnalyzer parseParameters(Constructor<?> ctor, List<Object> ports, List<String> nameList)
			throws Exception {
		FlowDescriptionAnalyzer analyzer = new FlowDescriptionAnalyzer();
		List<Parameter> rawParams = parseRawParameters(ctor);
		for (Parameter raw : rawParams) {
			analyzeParameter(raw, analyzer, ports, nameList);
		}
		return analyzer;
	}

	private void analyzeParameter(Parameter parameter, FlowDescriptionAnalyzer analyzer, List<Object> ports,
			List<String> nameList) throws Exception {
		assert parameter != null;
		assert analyzer != null;
		if (parameter.raw == In.class) {
			analyzeInput(parameter, analyzer, ports);
		} else if (parameter.raw == Out.class) {
			analyzeOutput(parameter, analyzer, ports);
		} else {
			nameList.add(parameter.name);
		}
	}

	private void analyzeInput(Parameter parameter, FlowDescriptionAnalyzer analyzer, List<Object> ports)
			throws Exception {
		String name;
		ImporterDescription importer;
		if (parameter.importer == null) {
			name = parameter.name;
			Class<?> type = getParameterizedType(parameter.type);
			ports.add(analyzer.addInput(name, type));
		} else {
			name = parameter.importer.name();
			Class<? extends ImporterDescription> clazz = parameter.importer.description();
			importer = clazz.newInstance();
			ports.add(analyzer.addInput(name, importer));
		}
	}

	private void analyzeOutput(Parameter parameter, FlowDescriptionAnalyzer analyzer, List<Object> ports)
			throws Exception {
		String name;
		ExporterDescription exporter;
		if (parameter.exporter == null) {
			name = parameter.name;
			Class<?> type = getParameterizedType(parameter.type);
			ports.add(analyzer.addOutput(name, type));
		} else {
			name = parameter.exporter.name();
			Class<? extends ExporterDescription> clazz = parameter.exporter.description();
			exporter = clazz.newInstance();
			ports.add(analyzer.addOutput(name, exporter));
		}
	}
}
