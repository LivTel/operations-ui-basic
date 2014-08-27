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
public class TimeAxisPanelBeanInfo extends SimpleBeanInfo {
	
	private static final int defaultPropertyIndex = -1;
	private static final int defaultEventIndex = -1;
	
	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(TimeAxisPanel.class, null);

		return beanDescriptor;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] properties = new PropertyDescriptor[0];

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
