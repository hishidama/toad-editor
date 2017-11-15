package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElementPortDescription;
import com.asakusafw.vocabulary.flow.graph.PortDirection;
import com.asakusafw.vocabulary.flow.graph.ShuffleKey;
import com.asakusafw.vocabulary.flow.graph.ShuffleKey.Order;

public class RevPort {

	private int id;
	private FlowElementPortDescription port;

	public RevPort(AtomicInteger id, FlowElementPortDescription port) {
		this.id = id.incrementAndGet();
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return port.getName();
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("id", id);
		map.put("name", port.getName());
		map.put("className", ((Class<?>) port.getDataType()).getName());
		map.put("out", port.getDirection() == PortDirection.OUTPUT);

		ShuffleKey key = port.getShuffleKey();
		if (key != null) {
			map.put("shuffleKey.group", key.getGroupProperties());

			List<Order> ordering = key.getOrderings();
			List<String> orderList = new ArrayList<String>(ordering.size());
			for (Order order : ordering) {
				orderList.add(order.getProperty() + " " + order.getDirection().name());
			}
			map.put("shuffleKey.order", orderList);
		}

		return map;
	}
}
