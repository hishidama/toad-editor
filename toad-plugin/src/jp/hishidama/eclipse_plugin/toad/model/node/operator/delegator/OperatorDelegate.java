package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import static jp.hishidama.eclipse_plugin.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;

public abstract class OperatorDelegate {
	// OperatorNodeはシリアライズされるので、種類に応じたサブクラスは作らない。
	// 個別の処理はこちらのクラスで記述する。

	private static final Map<String, Class<? extends OperatorDelegate>> DELEGATE_MAP;
	static {
		Map<String, Class<? extends OperatorDelegate>> map = new HashMap<String, Class<? extends OperatorDelegate>>();
		map.put("checkpoint", CoreCheckPoint.class);
		map.put("confluent", CoreConfluent.class);
		map.put("empty", CoreEmpty.class);
		map.put("extend", CoreExtend.class);
		map.put("project", CoreProject.class);
		map.put("restructure", CoreRestructure.class);
		map.put("stop", CoreStop.class);
		map.put("@Branch", UserBranch.class);
		map.put("@CoGroup", UserCoGroup.class);
		map.put("@Convert", UserConvert.class);
		map.put("@Extract", UserExtract.class);
		map.put("@Fold", UserFold.class);
		map.put("@GroupSort", UserGroupSort.class);
		map.put("@Logging", UserLogging.class);
		map.put("@MasterBranch", UserMasterBranch.class);
		map.put("@MasterCheck", UserMasterCheck.class);
		map.put("@MasterJoin", UserMasterJoin.class);
		map.put("@MasterJoinUpdate", UserMasterJoinUpdate.class);
		map.put("@Split", UserSplit.class);
		map.put("@Summarize", UserSummarize.class);
		map.put("@Update", UserUpdate.class);
		map.put("FlowPart", UserFlowPart.class);
		DELEGATE_MAP = map;
	}

	public static OperatorDelegate getDelegate(OperatorNode node) {
		OperatorDelegate delegate;
		{
			String type = node.getType();
			Class<? extends OperatorDelegate> clazz = DELEGATE_MAP.get(type);
			if (clazz == null) {
				throw new UnsupportedOperationException("type=" + type);
			}
			try {
				delegate = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		delegate.node = node;
		return delegate;
	}

	protected static final String KEY = "com.asakusafw.vocabulary.model.Key";

	/*
	 * fields/methods
	 */
	protected OperatorNode node;
	private String methodName;
	private String description;
	private int inPortMin, inPortMax, outPortMin, outPortMax;

	protected OperatorDelegate(String methodName, String description, int inMin, int inMax, int outMin, int outMax) {
		this.methodName = methodName;
		this.description = description;
		this.inPortMin = inMin;
		this.inPortMax = inMax;
		this.outPortMin = outMin;
		this.outPortMax = outMax;
	}

	public final String getMethodName() {
		return methodName;
	}

	public final String getDescription() {
		return description;
	}

	public final int getInPortMin() {
		return inPortMin;
	}

	public final int getInPortMax() {
		return inPortMax;
	}

	public final int getOutPortMin() {
		return outPortMin;
	}

	public final int getOutPortMax() {
		return outPortMax;
	}

	public boolean canStartConnect() {
		return false;
	}

	public boolean canConnectTo() {
		return false;
	}

	public boolean canConnectFrom() {
		return false;
	}

	/**
	 * 円形Figure内に表示する文字.
	 * 
	 * @return 文字
	 */
	public String getEllipseFigureText() {
		return null;
	}

	/**
	 * Operatorのデフォルトの属性.
	 * 
	 * @return 属性
	 */
	public List<Attribute> getDefaultAttribute() {
		return null;
	}

	/**
	 * Portのデフォルトのアノテーション.
	 * 
	 * @return アノテーション
	 */
	public List<Attribute> getDefaultPortAnnotation() {
		return null;
	}

	/**
	 * キーの名前.
	 * 
	 * @return キー名
	 */
	public String getKeyTitle() {
		return null;
	}

	/**
	 * 値引数を持てるかどうか.
	 * 
	 * @return true: 値引数を持つ
	 */
	public boolean enableValueParameter() {
		return false;
	}

	/**
	 * 型引数を持てるかどうか.
	 * 
	 * @return true: 型引数を持つ
	 */
	public boolean enableTypeParameter() {
		return false;
	}

	public boolean isReturnEnum() {
		return false;
	}

	public List<String> getPortRoleList(boolean in) {
		return Collections.emptyList();
	}

	public abstract void setDescription(JavadocClass javadoc);

	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode src) {
		// do override
	}

	protected final void collectAllSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set,
			Set<Integer> idSet, HasDataModelNode src) {
		DataModelNodeUtil.collectConnectionSiblingDataModelNode(list, set, idSet, node);
		for (OpePort port : node.getInputPorts()) {
			port.collectSiblingDataModelNode(list, set, idSet);
		}
		for (OpePort port : node.getOutputPorts()) {
			port.collectSiblingDataModelNode(list, set, idSet);
		}
	}

	protected final void collectSameSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set,
			Set<Integer> idSet, HasDataModelNode src, String... same) {
		List<NodeElement> candidate = getDataModelNodes(same);
		if (candidate.contains(src)) {
			for (NodeElement c : candidate) {
				if (c instanceof HasDataModelNode) {
					((HasDataModelNode) c).collectSiblingDataModelNode(list, set, idSet);
				} else {
					c.collectSiblingDataModelNode(list, set, idSet, src);
				}
			}
		}
	}

	private List<NodeElement> getDataModelNodes(String[] same) {
		List<NodeElement> result = new ArrayList<NodeElement>(same.length);
		for (String s : same) {
			String[] ss = s.split("\\.");
			String io = ss[0];
			String pos = ss[1];

			List<OpePort> list;
			if (io.equals("in")) {
				list = node.getInputPorts();
			} else {
				list = node.getOutputPorts();
			}

			int min, max;
			if (pos.endsWith("-")) {
				min = pos(list, pos.substring(0, pos.length() - 1));
				max = Integer.MAX_VALUE;
			} else {
				min = max = pos(list, pos);
			}

			for (int i = min; i <= max; i++) {
				if (i >= list.size()) {
					break;
				}
				OpePort port = list.get(i);
				result.add(port);
			}
		}
		return result;
	}

	private int pos(List<OpePort> list, String pos) {
		try {
			return Integer.parseInt(pos);
		} catch (NumberFormatException e) {
			// fall through
		}

		int i = 0;
		for (OpePort port : list) {
			if (pos.equals(port.getRole())) {
				return i;
			}
			i++;
		}

		return Integer.MAX_VALUE;
	}

	public GuessDataModelType guessDataModelType(OpePort port) {
		return null;
	}

	public void validate(ValidateType vtype, List<IStatus> result) {
		validatePortCount(result);
	}

	private void validatePortCount(List<IStatus> result) {
		List<OpePort> in = node.getInputPorts();
		if (in.size() < inPortMin) {
			result.add(new ErrorStatus("入力ポート数が足りません。ポート数={0}, 最低={1}", in.size(), inPortMin));
		}
		if (in.size() > inPortMax) {
			result.add(new ErrorStatus("入力ポート数が多すぎます。ポート数={0}, 最大={1}", in.size(), inPortMax));
		}

		List<OpePort> out = node.getOutputPorts();
		if (out.size() < outPortMin) {
			result.add(new ErrorStatus("出力ポート数が足りません。ポート数={0}, 最低={1}", out.size(), outPortMin));
		}
		if (out.size() > outPortMax) {
			result.add(new ErrorStatus("出力ポート数が多すぎます。ポート数={0}, 最大={1}", out.size(), outPortMax));
		}
	}

	protected final void validateDataModel(List<IStatus> result, String... same) {
		List<NodeElement> list = getDataModelNodes(same);
		validateDataModel(result, list);
	}

	protected final void validateDataModelAllPorts(List<IStatus> result) {
		List<OpePort> list = node.getPorts();
		validateDataModel(result, list);
	}

	protected final void validateDataModel(List<IStatus> result, List<? extends NodeElement> list) {
		String modelName = null;
		for (NodeElement t : list) {
			if (t instanceof HasDataModelNode) {
				String name = ((HasDataModelNode) t).getModelName();
				if (isEmpty(name)) {
					continue;
				}
				if (modelName == null) {
					modelName = name;
				} else {
					if (!modelName.equals(name)) {
						result.add(new ErrorStatus("データモデルが異なっている接続があります。"));
						break;
					}
				}
			}
		}
	}

	public abstract OperatorMethodGenerator getSourceCode();
}
