package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.task;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowpartDiagramGenerator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FlowpartParameterTask implements IRunnableWithProgress {
	private IDialogSettings settings;
	private List<IFile> list;
	private List<Item> result = new ArrayList<Item>();

	private Map<String, Object> map = new HashMap<String, Object>();

	public FlowpartParameterTask(IDialogSettings settings, List<IFile> list) {
		this.settings = settings;
		this.list = list;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("フローパートのパラメーターチェック", list.size());
		try {
			for (IFile file : list) {
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}
				monitor.subTask(file.getFullPath().toPortableString());

				FlowPartClass flowpart = FlowpartDiagramGenerator.findFlowpart(file);
				if (flowpart != null && flowpart.isDsl()) {
					for (Parameter param : flowpart.getParameters()) {
						String key = getKey(flowpart, param);
						if (!map.containsKey(key)) {
							String s = getDefaultValue(file.getProject(), flowpart.getClassName(), param);
							Object value = toValidateValue(param.className, s);
							map.put(key, value);

							Item item = new Item();
							item.file = file;
							item.flowpart = flowpart;
							item.param = param;
							item.value = value;
							result.add(item);
						}
					}
				}

				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	public static String getKey(FlowPartClass flowpart, Parameter param) {
		return String.format("%s#%s", flowpart.getClassName(), param.name);
	}

	private static enum NumberEnum {
		BYTE, SHORT, INT, LONG, FLOAT, DOUBLE
	}

	private static final Map<String, NumberEnum> NUMBER_TYPE;
	private static final Set<String> BOOLEAN_TYPE;
	private static final Set<String> CHAR_TYPE;
	static {
		Map<String, NumberEnum> map = new HashMap<String, NumberEnum>();
		map.put("byte", NumberEnum.BYTE);
		map.put("short", NumberEnum.SHORT);
		map.put("int", NumberEnum.INT);
		map.put("long", NumberEnum.LONG);
		map.put("float", NumberEnum.FLOAT);
		map.put("double", NumberEnum.DOUBLE);
		map.put("java.lang.Byte", NumberEnum.BYTE);
		map.put("java.lang.Short", NumberEnum.SHORT);
		map.put("java.lang.Integer", NumberEnum.INT);
		map.put("java.lang.Long", NumberEnum.LONG);
		map.put("java.lang.Float", NumberEnum.FLOAT);
		map.put("java.lang.Double", NumberEnum.DOUBLE);
		NUMBER_TYPE = map;
	}
	static {
		Set<String> set = new HashSet<String>(2);
		set.add("boolean");
		set.add("java.lang.Boolean");
		BOOLEAN_TYPE = set;
	}
	static {
		Set<String> set = new HashSet<String>(2);
		set.add("char");
		set.add("java.lang.Character");
		CHAR_TYPE = set;
	}

	private String getDefaultValue(IProject project, String className, Parameter param) {
		String settingKey = String.format("%s#%s#%s#%s", project.getName(), getClass().getSimpleName(), className,
				param.name);
		String s = settings.get(settingKey);
		if (s != null) {
			return s;
		}
		if (NUMBER_TYPE.containsKey(param.className)) {
			return "0";
		}
		if ("java.lang.String".equals(param.className)) {
			return "";
		}
		if (BOOLEAN_TYPE.contains(param.className)) {
			return "false";
		}
		if (CHAR_TYPE.contains(param.className)) {
			return "\\0";
		}
		return null;
	}

	public static Object toValidateValue(String className, String s) {
		try {
			NumberEnum type = NUMBER_TYPE.get(className);
			if (type != null) {
				switch (type) {
				case BYTE:
					return Byte.parseByte(s);
				case SHORT:
					return Short.parseShort(s);
				case INT:
					return Integer.parseInt(s);
				case LONG:
					return Long.parseLong(s);
				case FLOAT:
					return Float.parseFloat(s);
				case DOUBLE:
					return Double.parseDouble(s);
				default:
					throw new UnsupportedOperationException(MessageFormat.format("type={0}, value={1}", type, s));
				}
			}
			if ("java.lang.String".equals(className)) {
				return s;
			}
			if (BOOLEAN_TYPE.contains(className)) {
				return Boolean.parseBoolean(s);
			}
			if (CHAR_TYPE.contains(className)) {
				int code;
				if (s.startsWith("\\")) {
					code = Integer.parseInt(s.substring(1));
				} else if (s.length() >= 1) {
					code = s.charAt(0);
				} else {
					code = 0;
				}
				return (char) code;
			}
			return null;
		} catch (UnsupportedOperationException e) {
			throw e;
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> getParameters() {
		return map;
	}

	public List<Item> getTableItems() {
		return result;
	}

	public static class Item {
		public IFile file;
		public FlowPartClass flowpart;
		public Parameter param;
		public Object value;
	}
}
