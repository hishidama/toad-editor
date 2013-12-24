package jp.hishidama.eclipse_plugin.toad.model.node.port;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;

public class JobPort extends BasePort {
	private static final long serialVersionUID = -6596040697681266358L;

	public JobPort() {
		setType("JobPort");
	}

	@Override
	public JobPort cloneEdit() {
		JobPort to = new JobPort();
		to.copyFrom(this);
		return to;
	}

	@Override
	public boolean canStartConnect() {
		if (isOut()) {
			return getOutgoings().isEmpty();
		}
		return true;
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		if (isIn()) {
			if (!(source instanceof DataFileNode)) {
				return false;
			}
			for (Connection c : getIncomings()) {
				if (c == connection) {
					continue;
				}
				return false; // 1件でも有ったら不可
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		if (isOut()) {
			if (!(target instanceof DataFileNode)) {
				return false;
			}
			for (Connection c : getOutgoings()) {
				if (c == connection) {
					continue;
				}
				return false; // 1件でも有ったら不可
			}
			return true;
		}
		return true;
	}

	public DataFileNode getConnectedFile() {
		List<Connection> list;
		if (isIn()) {
			list = getIncomings();
		} else {
			list = getOutgoings();
		}
		for (Connection c : list) {
			NodeElement f = c.getOpposite(this);
			if (f instanceof DataFileNode) {
				return (DataFileNode) f;
			}
		}
		return null;
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		super.validate(vtype, edit, result);

		if (isIn() && !edit) {
			switch (getIncomings().size()) {
			case 0:
				result.add(new ErrorStatus("ジョブフローの入力ポート「{0}」への接続がありません。インポーターから接続して下さい。", getName()));
				break;
			case 1:
				break;
			default:
				result.add(new ErrorStatus("ジョブフローの入力ポート「{0}」への接続が多すぎます。", getName()));
				break;
			}
			if (getOutgoings().isEmpty()) {
				result.add(new ErrorStatus("ジョブフローの入力ポート「{0}」から どこにも接続されていません。使わない場合は停止演算子へ接続して下さい。", getName()));
			}
		}
		if (isOut() && !edit) {
			if (getIncomings().isEmpty()) {
				result.add(new ErrorStatus("ジョブフローの出力ポート「{0}」が どこからも接続されていません。無い場合は空演算子から接続して下さい。", getName()));
			}
			switch (getOutgoings().size()) {
			case 0:
				result.add(new ErrorStatus("ジョブフローの出力ポート「{0}」から どこにも接続されていません。エクスポーターに接続して下さい。", getName()));
				break;
			case 1:
				break;
			default:
				result.add(new ErrorStatus("ジョブフローの出力ポート「{0}」からの接続が多すぎます。", getName()));
				break;
			}
		}
	}

	@Override
	public String getDisplayLocation() {
		return String.format("JobPort(%s)", getSimpleDisplayName());
	}
}
