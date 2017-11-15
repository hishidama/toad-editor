package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementInput;
import com.asakusafw.vocabulary.flow.graph.FlowElementOutput;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;
import com.asakusafw.vocabulary.flow.graph.FlowIn;
import com.asakusafw.vocabulary.flow.graph.FlowOut;

// com.asakusafw.compiler.flow.plan.FlowGraphUtil
public class FlowGraphUtil {

	/**
	 * 対象のグラフに含まれ、かついずれかの入力または出力に結線された全ての要素を返す。
	 * <p>
	 * 返される結果には、フローの入出力も含まれる。
	 * </p>
	 * 
	 * @param graph
	 *            対象のグラフ
	 * @return グラフに含まれるすべての要素
	 * @throws IllegalArgumentException
	 *             引数に{@code null}が指定された場合
	 */
	public static Set<FlowElement> collectElements(FlowGraph graph) {
		Set<FlowElement> elements = new HashSet<FlowElement>();
		for (FlowIn<?> in : graph.getFlowInputs()) {
			elements.add(in.getFlowElement());
		}
		for (FlowOut<?> out : graph.getFlowOutputs()) {
			elements.add(out.getFlowElement());
		}
		collect(elements);
		return elements;
	}

	private static void collect(Set<FlowElement> collected) {
		assert collected != null;
		LinkedList<FlowElement> work = new LinkedList<FlowElement>(collected);
		while (work.isEmpty() == false) {
			FlowElement first = work.removeFirst();
			if (collected.contains(first) == false) {
				collected.add(first);
			}
			for (FlowElement pred : getPredecessors(first)) {
				if (collected.contains(pred) == false) {
					work.add(pred);
				}
			}
			for (FlowElement succ : getSuccessors(first)) {
				if (collected.contains(succ) == false) {
					work.add(succ);
				}
			}
		}
	}

	/**
	 * 指定の要素に直接後続する全ての要素を返す。
	 * 
	 * @param element
	 *            対象の要素
	 * @return 直接後続する全ての要素
	 * @throws IllegalArgumentException
	 *             引数に{@code null}が指定された場合
	 */
	public static Set<FlowElement> getSuccessors(FlowElement element) {
		Set<FlowElement> results = new HashSet<FlowElement>();
		addSuccessors(results, element);
		return results;
	}

	private static void addSuccessors(Collection<FlowElement> target, FlowElement element) {
		assert target != null;
		assert element != null;
		for (FlowElementOutput output : element.getOutputPorts()) {
			for (FlowElementInput opposite : output.getOpposites()) {
				target.add(opposite.getOwner());
			}
		}
	}

	/**
	 * 指定の要素に直接先行する全ての要素を返す。
	 * 
	 * @param element
	 *            対象の要素
	 * @return 直接先行する全ての要素
	 * @throws IllegalArgumentException
	 *             引数に{@code null}が指定された場合
	 */
	public static Set<FlowElement> getPredecessors(FlowElement element) {
		Set<FlowElement> results = new HashSet<FlowElement>();
		addPredecessors(results, element);
		return results;
	}

	private static void addPredecessors(Collection<FlowElement> target, FlowElement element) {
		assert target != null;
		assert element != null;
		for (FlowElementInput input : element.getInputPorts()) {
			for (FlowElementOutput opposite : input.getOpposites()) {
				target.add(opposite.getOwner());
			}
		}
	}
}
