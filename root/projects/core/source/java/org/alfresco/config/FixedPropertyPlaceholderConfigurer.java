package org.alfresco.config;

import java.lang.reflect.Field;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * The fix for unconfigurable valueSeparator property.
 * https://jira.springframework.org/browse/SPR-7429
 *
 * @author arsenyko
 */
public class FixedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{

    public void setValueSeparator(String value)
    {
        try
        {
            Field valueSeparator = PropertyPlaceholderConfigurer.class.getDeclaredField("valueSeparator");
            valueSeparator.setAccessible(true);
            valueSeparator.set(this, value);
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Cannot set the valueSeparator propery", e);
        }
    }
}
