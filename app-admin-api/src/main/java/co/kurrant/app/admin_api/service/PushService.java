package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.push.PushRequestDto;

public interface PushService {
    void sendToPush(PushRequestDto pushRequestDto);
}
