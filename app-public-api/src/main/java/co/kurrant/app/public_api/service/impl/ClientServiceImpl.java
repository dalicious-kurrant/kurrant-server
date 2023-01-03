package co.kurrant.app.public_api.service.impl;

import co.kurrant.app.public_api.dto.client.SpotListResponseDto;
import co.kurrant.app.public_api.service.ClientService;

import javax.servlet.http.HttpServletRequest;

public class ClientServiceImpl implements ClientService {
    @Override
    public SpotListResponseDto getClients(HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public void getSpotDetail(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId) {

    }

    @Override
    public void saveUserSpot(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId) {

    }

    @Override
    public void withdrawClient(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId) {

    }
}
