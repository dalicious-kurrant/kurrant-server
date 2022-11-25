package co.kurrant.app.public_api.validation;

import co.dalicious.client.core.exception.ApiException;
import co.dalicious.client.core.exception.ExceptionEnum;

public class UserValidation {
    public static void CheckIsPasswordMatched(String password, String passwordCheck) {
        if(!password.equals(passwordCheck)) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
    }
}
