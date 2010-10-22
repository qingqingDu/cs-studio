package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.support.MultiLineTextPropertyDescriptor;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

/**The widget property for string. It also accept macro string $(macro).
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class StringProperty extends AbstractWidgetProperty {
	
	private boolean multiLine;
	/**String Property Constructor. The property value type is {@link String}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 */
	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		this(prop_id, description, category, defaultValue, false);
	}
	
	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue, boolean multiLine) {
		super(prop_id, description, category, defaultValue);
		this.multiLine = multiLine;
	}
	

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		String acceptedValue = null;

		if (value instanceof String) 
			acceptedValue = (String) value;
		else
			acceptedValue = value.toString();
		
		
		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(multiLine)
			return new MultiLineTextPropertyDescriptor(prop_id, description);
		else
			return new TextPropertyDescriptor(prop_id, description);
	}

	@Override
	public void writeToXML(Element propElement) {		
		String reShapedString = 
			getPropertyValue().toString().replaceAll("\\x0D\\x0A?", new String(new byte[]{13,10}));
		propElement.setText(reShapedString);
	}
	


	@Override
	public Object readValueFromXML(Element propElement) {
		return propElement.getValue();
	}
	
	@Override
	public Object getPropertyValue() {
		if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE)
			return OPIBuilderMacroUtil.replaceMacros(
					widgetModel, (String) super.getPropertyValue());
		else
			return super.getPropertyValue();
	}
	
	
	
	
	
	

}
