package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can only be used in start-method.
 * @author mz
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ExecutableEclipseRCPExtension {
	/**
	 * {@link Class#getName()} will be used as Id-String.
	 */
	public Class<?> extensionId();
	
	/**
	 * Default is "implementation".
	 */
	public String executeableName() default "implementation";
}
