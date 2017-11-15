package jp.hishidama.eclipse_plugin.toad.validation;

import static jp.hishidama.eclipse_plugin.util.StringUtil.*;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;
import jp.hishidama.xtext.dmdl_editor.validation.WarningStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

@SuppressWarnings("restriction")
public class ToadValidator {

	public static void validateJobName(ValidateType vtype, List<IStatus> result, String name) {
		if (isEmpty(name)) {
			switch (vtype) {
			case IMPLEMENTS:
			case GENERATE:
			case ALL:
				result.add(new ErrorStatus("ジョブフロー名を入力して下さい。"));
				break;
			default:
				break;
			}
			return;
		}
		// TODO ジョブフロー名の精査
	}

	public static void validateDescription(ValidateType vtype, List<IStatus> result, String title, String desc) {
		if (isEmpty(desc)) {
			String message = MessageFormat.format("{0}のdescriptionを入力して下さい。", title);
			switch (vtype) {
			case DESIGN:
			case ALL:
				result.add(new ErrorStatus(message));
				break;
			default:
				result.add(new WarningStatus(message));
				break;
			}
			return;
		}
	}

	public static void validatePorts(ValidateType vtype, List<IStatus> result, HasPortNode<? extends BasePort> node) {
		if (vtype == ValidateType.GENERATE) {
			for (BasePort port : node.getPorts(true)) {
				validateModelName(vtype, false, result, "入力ポート" + port.getName(), port.getModelName());
			}
			for (BasePort port : node.getPorts(false)) {
				validateModelName(vtype, false, result, "出力ポート" + port.getName(), port.getModelName());
			}
		}
		validatePorts(vtype, result, "入力ポート名", node.getPorts(true));
		validatePorts(vtype, result, "出力ポート名", node.getPorts(false));
	}

	private static void validatePorts(ValidateType vtype, List<IStatus> result, String title,
			List<? extends BasePort> list) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		for (BasePort port : list) {
			String name = port.getName();
			if (nonEmpty(name)) {
				Integer n = map.get(name);
				if (n == null) {
					n = 1;
				} else {
					n++;
				}
				map.put(name, n);
			}
		}
		for (Entry<String, Integer> entry : map.entrySet()) {
			int n = entry.getValue();
			if (n >= 2) {
				result.add(new ErrorStatus("{0}が重複しています。name={1}", title, entry.getKey()));
			}
		}
	}

	public static void validatePortName(ValidateType vtype, List<IStatus> result, String name) {
		if (isEmpty(name)) {
			result.add(new ErrorStatus("ポート名を入力して下さい。"));
			return;
		}
		result.add(JavaConventions.validateIdentifier(name, CompilerOptions.VERSION_1_6, CompilerOptions.VERSION_1_6));
	}

	public static void validateModelName(ValidateType vtype, boolean edit, List<IStatus> result, String target,
			String name) {
		if (isEmpty(name)) {
			if (!edit) {
				String message;
				if (target == null) {
					message = "データモデルを入力して下さい。";
				} else {
					message = MessageFormat.format("{0}のデータモデルを入力して下さい。", target);
				}
				result.add(new ErrorStatus(message));
			}
			return;
		}
		switch (vtype) {
		case IMPLEMENTS:
		case GENERATE:
		case ALL:
			// TODO データモデルの存在チェック（edit以外のとき？）
			break;
		default:
			break;
		}
	}

	public static void validateClassName(ValidateType vtype, List<IStatus> result, String name) {
		if (isEmpty(name)) {
			switch (vtype) {
			case IMPLEMENTS:
			case GENERATE:
			case ALL:
				result.add(new ErrorStatus("クラス名を入力して下さい。"));
				return;
			default:
				return;
			}
		}
		result.add(JavaConventions.validateJavaTypeName(name, CompilerOptions.VERSION_1_6, CompilerOptions.VERSION_1_6));
	}

	public static void validateMethodName(ValidateType vtype, List<IStatus> result, String name) {
		if (isEmpty(name)) {
			switch (vtype) {
			case IMPLEMENTS:
			case GENERATE:
			case ALL:
				result.add(new ErrorStatus("メソッド名を入力して下さい。"));
				return;
			default:
				return;
			}
		}
		result.add(JavaConventions.validateMethodName(name, CompilerOptions.VERSION_1_6, CompilerOptions.VERSION_1_6));
	}

	public static void validateDataFileType(ValidateType vtype, List<IStatus> result, String name) {
		if (isEmpty(name)) {
			switch (vtype) {
			case IMPLEMENTS:
			case GENERATE:
			case ALL:
				result.add(new ErrorStatus("fileTypeを入力して下さい。"));
				return;
			default:
				return;
			}
		}
		if (!(name.endsWith("Importer") || name.endsWith("Exporter"))) {
			result.add(new ErrorStatus("fileTypeが間違っています。"));
			return;
		}
	}

	public static String getErrorMessage(List<IStatus> result) {
		StringBuilder sb = new StringBuilder(512);
		for (IStatus s : result) {
			if (s != null && !s.isOK()) {
				sb.append(s.getMessage());
				sb.append("\n");
			}
		}
		if (sb.length() <= 0) {
			return null;
		}
		return sb.toString();
	}
}
