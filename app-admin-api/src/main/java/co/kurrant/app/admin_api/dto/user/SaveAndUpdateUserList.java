package co.kurrant.app.admin_api.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveAndUpdateUserList {
    private List<SaveUserListRequestDto> userList;
}
