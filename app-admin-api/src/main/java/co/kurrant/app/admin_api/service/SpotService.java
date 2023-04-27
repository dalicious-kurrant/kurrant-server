package co.kurrant.app.admin_api.service;

import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.dto.client.SpotDetailResDto;
import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.List;

public interface SpotService {
    List<SpotResponseDto> getAllSpotList(Integer status);

    void saveSpotList(SaveSpotList saveSpotList) throws ParseException;

    void deleteSpot(List<BigInteger> spotIdList);
    
    List<GroupDto.Group> getGroupList();

    SpotDetailResDto getSpotDetail(Integer spotId);

    void updateSpotDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException;
}
