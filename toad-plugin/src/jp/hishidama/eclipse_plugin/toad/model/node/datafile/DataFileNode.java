package jp.hishidama.eclipse_plugin.toad.model.node.datafile;

import static jp.hishidama.eclipse_plugin.util.StringUtil.isEmpty;
import static jp.hishidama.eclipse_plugin.util.StringUtil.nonEmpty;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionImplementsFilter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.internal.extension.ImporterExporterExtensionUtil;
import jp.hishidama.eclipse_plugin.toad.internal.util.JarUtil;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.gson.annotations.Expose;

public class DataFileNode extends RectangleNode implements ClassNameNode, HasDataModelNode {
	private static final long serialVersionUID = 1265408983766650946L;

	public static final String PROP_FILE_TYPE = "fileType";

	/** Importer/Exporter class name */
	@Expose
	private String className;
	@Expose
	private String modelName;
	@Expose
	private String modelDescription;
	@Expose
	private String fileType;
	@Expose
	private Map<String, String> propertyMap;

	public DataFileNode() {
		setType("DataFile");
		setWidth(80);
		setHeight(48);
	}

	@Override
	public DataFileNode cloneEdit() {
		DataFileNode to = new DataFileNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		DataFileNode from = (DataFileNode) fromModel;
		this.className = from.className;
		this.modelName = from.modelName;
		this.modelDescription = from.modelDescription;
		this.fileType = from.fileType;
		if (from.propertyMap != null) {
			this.propertyMap = new LinkedHashMap<String, String>(from.propertyMap);
		}
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		DataFileNode from = (DataFileNode) fromModel;
		ToadCommandUtil.add(compound, getClassNameCommand(from.getClassName()));
		ToadCommandUtil.add(compound, getModelNameCommand(from.getModelName()));
		ToadCommandUtil.add(compound, getModelDescriptionCommand(from.getModelDescription()));
		ToadCommandUtil.add(compound, getFileTypeCommand(from.getFileType()));
		ToadCommandUtil.add(compound, getPropertyCommand(new LinkedHashMap<String, String>(from.getPropertyMap())));

		return compound;
	}

	@Override
	public String getFigureLabel() {
		String ftype = getFileType();
		if (nonEmpty(ftype)) {
			return ftype;
		}
		return getType();
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public void setClassName(String className) {
		String old = this.className;
		this.className = className;
		firePropertyChange(PROP_CLASS_NAME, old, className);
	}

	@Override
	public ChangeTextCommand getClassNameCommand(String className) {
		return new ChangeTextCommand(className) {
			@Override
			protected void setValue(String value) {
				setClassName(value);
			}

			@Override
			protected String getValue() {
				return getClassName();
			}
		};
	}

	@Override
	public String getClassNamePattern() {
		String t = getFileTypeName();
		if (t.equals("Importer")) {
			return "*From*";
		} else if (t.equals("Exporter")) {
			return "*To*";
		} else {
			return "*Description";
		}
	}

	@Override
	public Filter getClassNameFilter() {
		String t = getFileTypeName();
		if (t.equals("Importer")) {
			return new ClassSelectionImplementsFilter("com.asakusafw.vocabulary.external.ImporterDescription");
		} else if (t.equals("Exporter")) {
			return new ClassSelectionImplementsFilter("com.asakusafw.vocabulary.external.ExporterDescription");
		} else {
			return new ClassSelectionImplementsFilter("com.asakusafw.vocabulary.external.ImporterDescription",
					"com.asakusafw.vocabulary.external.ExporterDescription");
		}
	}

	@Override
	public boolean hasToadFile() {
		return false;
	}

	@Override
	public String getToadFileExtension() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public void load(IJavaProject javaProject) {
		if (isEmpty(className)) {
			return;
		}

		if (isEmpty(fileType)) {
			try {
				IType t = javaProject.findType(className);
				List<ToadImporterExporterProperty> list = ImporterExporterExtensionUtil.getExtensionList();
				for (ToadImporterExporterProperty p : list) {
					if (p.acceptable(t)) {
						setFileType(p.getName());
						break;
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

		Map<String, String> map;
		try {
			Class<?> clazz = JarUtil.loadClass(javaProject, "jp.hishidama.eclipse_plugin.toad.loader.DataFileLoader");
			Method method = clazz.getMethod("load", String.class);
			Object r = method.invoke(null, className);
			map = (Map<String, String>) r;
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if ("getClass".equals(key)) {
					continue;
				} else if ("getModelType".equals(key)) {
					ModelDefinition model = ModelUiUtil.findModelByClass(javaProject.getProject(), value);
					if (model != null) {
						modelName = model.getName();
						modelDescription = ModelUtil.getDecodedDescription(model);
					}
				} else {
					if (propertyMap == null) {
						propertyMap = new LinkedHashMap<String, String>(map.size());
					}
					propertyMap.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isEmpty(getDescription()) && nonEmpty(modelDescription)) {
			setDescription(modelDescription);
		}
	}

	@Override
	public void setModelName(String name) {
		String old = this.modelName;
		this.modelName = name;
		firePropertyChange(PROP_MODEL_NAME, old, name);
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public ChangeTextCommand getModelNameCommand(String name) {
		return new ChangeTextCommand(name) {
			@Override
			protected void setValue(String value) {
				setModelName(value);
			}

			@Override
			protected String getValue() {
				return getModelName();
			}
		};
	}

	@Override
	public void setModelDescription(String description) {
		String old = this.modelDescription;
		this.modelDescription = description;
		firePropertyChange(PROP_MODEL_DESCRIPTION, old, description);
	}

	@Override
	public String getModelDescription() {
		return modelDescription;
	}

	@Override
	public ChangeTextCommand getModelDescriptionCommand(String description) {
		return new ChangeTextCommand(description) {
			@Override
			protected void setValue(String value) {
				setModelDescription(value);
			}

			@Override
			protected String getValue() {
				return getModelDescription();
			}
		};
	}

	public void setFileType(String type) {
		String old = this.fileType;
		this.fileType = type;
		firePropertyChange(PROP_FILE_TYPE, old, type);
	}

	public String getFileType() {
		return fileType;
	}

	private String getFileTypeName() {
		if (fileType.endsWith("Importer")) {
			return "Importer";
		} else if (fileType.endsWith("Exporter")) {
			return "Exporter";
		} else {
			return "DataFile";
		}
	}

	public ChangeTextCommand getFileTypeCommand(String type) {
		return new ChangeTextCommand(type) {
			@Override
			protected void setValue(String value) {
				setFileType(value);
			}

			@Override
			protected String getValue() {
				return getFileType();
			}
		};
	}

	private void setPropertyMap(Map<String, String> map) {
		propertyMap = map;
	}

	public void setProperty(String key, String value) {
		if (propertyMap == null) {
			propertyMap = new LinkedHashMap<String, String>();
		}
		propertyMap.put(key, value);
	}

	public String getProperty(String key) {
		if (propertyMap == null) {
			return null;
		}
		return propertyMap.get(key);
	}

	public Map<String, String> getPropertyMap() {
		if (propertyMap == null) {
			return Collections.emptyMap();
		}
		return propertyMap;
	}

	public ChangeMapCommand getPropertyCommand(Map<String, String> map) {
		return new ChangeMapCommand(map) {
			@Override
			protected void setValue(Map<String, String> value) {
				setPropertyMap(value);
			}

			@Override
			protected Map<String, String> getValue() {
				return getPropertyMap();
			}
		};
	}

	@Override
	public boolean canStartConnect() {
		return !getFileTypeName().equals("Exporter");
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		if (getFileTypeName().equals("Exporter")) {
			return false;
		}
		return (target instanceof JobPort) && ((JobPort) target).isIn();
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		if (getFileTypeName().equals("Importer")) {
			return false;
		}
		return (source instanceof JobPort) && ((JobPort) source).isOut();
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet) {
		DataModelNodeUtil.collectSiblingDataModelNode(list, set, idSet, this);
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validateDescription(vtype, result, getFileTypeName(), getDescription());
		ToadValidator.validateClassName(vtype, result, getClassName());
		ToadValidator.validateModelName(vtype, edit, result, null, getModelName());
		ToadValidator.validateDataFileType(vtype, result, getFileType());
		if (vtype != ValidateType.GENERATE && !edit) {
			validateConnection(vtype, result);
		}
		// TODO fileTypeに応じたpropetyMapの精査
	}

	private void validateConnection(ValidateType vtype, List<IStatus> result) {
		String t = getFileTypeName();
		if (t.endsWith("Importer")) {
			if (!getIncomings().isEmpty()) {
				result.add(new ErrorStatus("インポーターへは入力できません。"));
				return;
			}
			switch (getOutgoings().size()) {
			case 0:
				result.add(new ErrorStatus("インポーター「{0}」は使われていません。ジョブフローの入力ポートに接続して下さい。", getDescription()));
				break;
			case 1:
				NodeElement node = getOutgoings().get(0).getOpposite(this);
				if (node instanceof JobPort && ((JobPort) node).isIn()) {
					// OK
				} else {
					result.add(new ErrorStatus("インポーター「{0}」の接続先がジョブフローの入力ポートではありません。", getDescription()));
				}
				break;
			default:
				result.add(new ErrorStatus("インポーター「{0}」からの出力が多すぎます。", getDescription()));
				break;
			}
		} else if (t.endsWith("Exporter")) {
			if (!getOutgoings().isEmpty()) {
				result.add(new ErrorStatus("エクスポーターからは出力できません。"));
				return;
			}
			switch (getIncomings().size()) {
			case 0:
				result.add(new ErrorStatus("エクスポーター「{0}」は使われていません。ジョブフローの出力ポートから接続して下さい。", getDescription()));
				break;
			case 1:
				NodeElement node = getIncomings().get(0).getOpposite(this);
				if (node instanceof JobPort && ((JobPort) node).isOut()) {
					// OK
				} else {
					result.add(new ErrorStatus("エクスポーター「{0}」の接続元がジョブフローの出力ポートではありません。", getDescription()));
				}
				break;
			default:
				result.add(new ErrorStatus("エクスポーター「{0}」への入力が多すぎます。", getDescription()));
				break;
			}
		}
	}

	@Override
	public String getNodeDescription() {
		return getFileTypeName();
	}

	@Override
	public String getDisplayLocation() {
		return String.format("%s(%s)", getFileTypeName(), getDescription());
	}
}
