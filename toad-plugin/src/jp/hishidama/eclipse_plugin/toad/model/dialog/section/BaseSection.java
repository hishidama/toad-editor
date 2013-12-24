package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class BaseSection extends PropertySection {
	private AbstractNameModel model;

	private Text baseName;
	private Text baseDescription;

	public BaseSection(PropertyDialog dialog, AbstractNameModel model) {
		super(dialog);
		this.model = model;
	}

	public void createSection(Composite composite) {
		if (model instanceof NameNode) {
			baseName = createTextField(composite, "name");
			baseName.setText(nonNull(((NameNode) model).getName()));
			baseName.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					((NameNode) model).setName(baseName.getText().trim());
					dialog.doValidate();
				}
			});
		}
		baseDescription = createTextField(composite, "description");
		baseDescription.setText(nonNull(model.getDescription()));
		baseDescription.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setDescription(baseDescription.getText());
				dialog.doValidate();
			}
		});
	}

	public void setName(String name) {
		if (baseName != null) {
			baseName.setText(nonNull(name));
		}
	}

	public void setDescription(String desc) {
		baseDescription.setText(nonNull(desc));
	}
}
