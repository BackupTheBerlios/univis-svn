//$Id: Past.java 8587 2005-11-16 18:05:00Z epbernard $
package org.hibernate.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Check that a Date, a Calendar, or a string representation apply in the past
 *
 * @author Gavin King
 */
@Documented
@ValidatorClass(PastValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Past {
	String message() default "{validator.past}";
}
