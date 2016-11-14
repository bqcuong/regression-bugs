package com.peterphi.rules.types;

import com.google.inject.Injector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by bmcleod on 14/09/2016.
 */
public class StringObjectVar extends Variable
{
	@XmlAttribute(required = true, name="value")
	String v;

	@Override
	public Object getValue(final Injector injector)
	{
		return v;
	}
}
