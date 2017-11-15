package jp.hishidama.eclipse_plugin.toad.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ToadColorManager {

	private static ToadColorManager instance = new ToadColorManager();

	public static ToadColorManager getInstance() {
		return instance;
	}

	public static Color getJobFlowColor() {
		return getInstance().getColor(new RGB(192, 224, 192));
	}

	public static Color getFlowPartColor() {
		return getInstance().getColor(new RGB(224, 192, 224));
	}

	public static Color getOperatorColor() {
		return getInstance().getColor(new RGB(192, 192, 224));
	}

	private Map<RGB, Color> map = null;

	public Color getColor(int red, int green, int blue) {
		return getColor(new RGB(red, green, blue));
	}

	public Color getColor(RGB rgb) {
		if (map == null) {
			map = new HashMap<RGB, Color>();
		}

		Color c = map.get(rgb);
		if (c == null) {
			c = new Color(null, rgb);
			map.put(rgb, c);
		}
		return c;
	}

	public void dispose() {
		if (map != null) {
			for (Color c : map.values()) {
				c.dispose();
			}
			map = null;
		}
	}
}
