package co.kurrant.app.admin_api.util;

import co.dalicious.domain.address.dto.UpdateLocationDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.QGroupRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
// @PropertySource("classpath:application-map.properties")
public class AddressUtil {

    public final QGroupRepository qGroupRepository;
//
//    @Value("${naver.client.id}")
//    public String YOUR_CLIENT_ID;
//
//    @Value("${naver.secret.id}")
//    public String YOUR_CLIENT_SECRET;

    @Transactional
    public void updateLocation(List<UpdateLocationDto> dtoList) throws ParseException {
        List<BigInteger> doList = dtoList.stream().map(UpdateLocationDto::getId).toList();
        List<Group> groupList = qGroupRepository.findAllByIds(doList);

        for(Group group : groupList) {
            String updateLocation = dtoList.stream()
                    .filter(dto -> dto.getId().equals(group.getId())).findAny()
                    .map(UpdateLocationDto::getLocation)
                    .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
            group.getAddress().updateLocation(updateLocation);
        }
    }

//    @Transactional
//    public void updateLocation() throws ParseException {
//        List<Group> groupList = qGroupRepository.findGroupAndAddressIsNull();
//
//        for(Group group : groupList) {
//            String updateLocation = getLocation(group.getAddress().getAddress1());
//            group.getAddress().updateLocation(updateLocation);
//        }
//    }

//    public String getLocation (String address) {
//
//        String locationResult = null;
//        try {
//            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + address;
//            URL url = new URL(apiURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", YOUR_CLIENT_ID);
//            conn.setRequestProperty("X-NCP-APIGW-API-KEY", YOUR_CLIENT_SECRET);
//
//            int responseCode = conn.getResponseCode();
//            BufferedReader br;
//
//            if (responseCode == 200) { // 정상 호출
//                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else { // 에러 발생
//                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//            }
//
//            String line;
//            StringBuilder response = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                response.append(line);
//            }
//            br.close();
//
//            JSONTokener tokener = new JSONTokener(response.toString());
//            JSONObject object = new JSONObject(tokener);
//            JSONArray arr = object.getJSONArray("addresses");
//
//            for (int i = 0; i < arr.length(); i++) {
//                JSONObject temp = (JSONObject) arr.get(i);
//                System.out.println("address : " + temp.get("roadAddress"));
//                System.out.println("jibunAddress : " + temp.get("jibunAddress"));
//                System.out.println("위도 : " + temp.get("y"));
//                System.out.println("경도 : " + temp.get("x"));
//            }
//
////            locationResult = latitude + " " + longitude;
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//        }
//        return locationResult;
//    }

}
