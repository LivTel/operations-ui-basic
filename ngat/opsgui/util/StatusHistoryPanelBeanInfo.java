/**
 * 
 */
package ngat.opsgui.util;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author eng
 *
 */
public class StatusHistoryPanelBeanInfo extends SimpleBeanInfo {
	
	private static final int defaultPropertyIndex = -1;
	private static final int defaultEventIndex = -1;
	
	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(StatusHistoryPanel.class, null);

		return beanDescriptor;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] properties = new PropertyDescriptor[2];

		try {
			properties[0] = new PropertyDescriptor("controller", StatusHistoryPanel.class, null, "setController");
			properties[1] = new PropertyDescriptor("map", StatusHistoryPanel.class, null, "setMap");
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return properties;
	}

	@Override
	public EventSetDescriptor[] getEventSetDescriptors() {
		EventSetDescriptor[] eventSets = new EventSetDescriptor[0];

		return eventSets;
	}

	@Override
	public MethodDescriptor[] getMethodDescriptors() {
		MethodDescriptor[] methods = new MethodDescriptor[0];

		return methods;
	}

	@Override
	public int getDefaultPropertyIndex() {
		return defaultPropertyIndex;
	}

	@Override
	public int getDefaultEventIndex() {
		return defaultEventIndex;
	}
}
