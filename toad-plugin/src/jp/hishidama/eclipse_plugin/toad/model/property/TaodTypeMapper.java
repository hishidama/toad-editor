package jp.hishidama.eclipse_plugin.toad.model.property;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

public class TaodTypeMapper extends AbstractTypeMapper {

	@Override
	public Class<?> mapType(Object object) {
		if (object instanceof AbstractEditPart) {
			AbstractEditPart part = (AbstractEditPart) object;
			return part.getModel().getClass();
		}

		return super.mapType(object);
	}
}
