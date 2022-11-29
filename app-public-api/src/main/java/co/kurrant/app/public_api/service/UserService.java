package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.entity.User;


public interface UserService {
    User findByToken();
}
