package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.internal.extension.ImporterExporterExtensionUtil;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.util.DMDLStringUtil;

import org.eclipse.core.resources.IProject;

public class PorterClassGenerator extends DslClassGenerator implements ClassGenerator {

	private DataFileNode node;

	public PorterClassGenerator(IProject project, DataFileNode node) {
		super(project, node.getClassName());
		this.node = node;
	}

	@Override
	protected void defaultImport() {
	}

	@Override
	protected void appendClassJavadoc(StringBuilder sb) {
		sb.append("/**\n");
		sb.append(" * ");
		sb.append(node.getDescription());
		sb.append("\n");

		String memo = node.getMemo();
		if (StringUtil.nonEmpty(memo)) {
			String[] ss = memo.split("[\r\n]+");
			for (String s : ss) {
				sb.append(" * ");
				sb.append(s);
				sb.append("\n");
			}
		}

		sb.append(" */\n");
	}

	@Override
	protected void appendClassAnnotation(StringBuilder sb) {
	}

	@Override
	protected void appendClass(StringBuilder sb) {
		String fileType = node.getFileType();
		ToadImporterExporterProperty extension = ImporterExporterExtensionUtil.getExtension(fileType);
		if (extension == null) {
			throw new UnsupportedOperationException("fileType=" + fileType);
		}

		extension.generateClass(this, node, sb);
	}

	@Override
	public String getModelClassName(String modelName) {
		if (StringUtil.isEmpty(modelName)) {
			return "_UndefinedModel_";
		}
		String className = DMDLStringUtil.getModelClass(project, modelName);
		if (className == null) {
			return "_UndefinedPackage_" + modelName + "_";
		}
		return className;
	}

	@Override
	public String getCachedModelClassName(String modelName) {
		return getCachedClassName(getModelClassName(modelName));
	}
}
