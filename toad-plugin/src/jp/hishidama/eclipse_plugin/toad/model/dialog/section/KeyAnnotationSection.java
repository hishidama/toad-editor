package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;

import org.eclipse.jface.window.Window;

public class KeyAnnotationSection extends KeySection {
	private List<List<String>> dropList = new ArrayList<List<String>>();

	public KeyAnnotationSection(PropertyDialog dialog, OperatorNode model) {
		super(dialog, model);
	}

	@Override
	protected List<String> getTitle() {
		List<OpePort> ports = model.getInputPorts();
		List<String> list = new ArrayList<String>(ports.size());
		for (OpePort port : ports) {
			list.add(port.getModelName());
		}
		return list;
	}

	@Override
	protected List<Row> buildKey(String modelName) {
		List<Row> rowList = new ArrayList<Row>();

		List<OpePort> ports = model.getInputPorts();
		int i = 0;
		for (OpePort port : ports) {
			Attribute attr = findKeyGroupAttribute(port);
			if (attr != null) {
				List<String> value = attr.getValue();
				int j = 0;
				for (String s : value) {
					while (j >= rowList.size()) {
						rowList.add(new Row());
					}
					Row row = rowList.get(j);
					row.set(i, s);
					j++;
				}
			}
			i++;
		}

		return rowList;
	}

	@Override
	protected boolean editElement(Row element) {
		List<String> values = new ArrayList<String>(element.name);
		if (dropList.isEmpty()) {
			for (String modelName : titleList) {
				List<Property> plist = ModelUiUtil.getProperties(getProject(), modelName, getContainer());
				List<String> list = convert(plist);
				dropList.add(list);
			}
		}
		EditKeyDialog dialog = new EditKeyDialog(getShell(), titleList, values, dropList);
		if (dialog.open() != Window.OK) {
			return false;
		}

		element.name = dialog.getValue();
		return true;
	}

	private List<String> convert(List<Property> plist) {
		List<String> list = new ArrayList<String>(plist.size());
		for (Property p : plist) {
			list.add(p.getName());
		}
		return list;
	}

	@Override
	protected void refreshTable() {
		List<Row> rowList = table.getElementList();

		List<OpePort> ports = model.getInputPorts();
		int i = 0;
		for (OpePort port : ports) {
			Attribute attr = findKeyGroupAttribute(port);
			if (attr == null) {
				attr = new Attribute("com.asakusafw.vocabulary.model.Key", "group", "java.lang.String");
				port.addAttribute(attr);
			}

			List<String> values = new ArrayList<String>();
			for (Row row : rowList) {
				String value = row.get(i);
				values.add(value);
			}
			attr.setValue(values);

			i++;
		}
	}

	private static Attribute findKeyGroupAttribute(OpePort port) {
		for (Attribute attr : port.getAttributeList()) {
			if ("com.asakusafw.vocabulary.model.Key".equals(attr.getAnnotationName())
					&& "group".equals(attr.getParameterName())) {
				return attr;
			}
		}
		return null;
	}
}
