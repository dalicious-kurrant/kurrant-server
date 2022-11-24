package co.dalicious.client.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import co.dalicious.client.core.annotation.validation.ValidEnum;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {
  private ValidEnum annotation;

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    this.annotation = constraintAnnotation;
  }

  @Override
  public boolean isValid(Enum value, ConstraintValidatorContext context) {
    boolean result = false;

    // 값이 안들어오면 그냥 true반환 (업데이트 할때 이런 경우가 대다수)
    if (value == null) {
      return true;
    }

    Object[] enumValues = this.annotation.enumClass().getEnumConstants();
    if (enumValues != null) {
      for (Object enumValue : enumValues) {
        if (value == enumValue) {
          result = true;
          break;
        }
      }
    }
    return result;
  }
}
