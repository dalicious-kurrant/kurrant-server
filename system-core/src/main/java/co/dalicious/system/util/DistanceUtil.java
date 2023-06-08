package co.dalicious.system.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

@Component
public class DistanceUtil {

    private static final double EARTH_RADIUS = 6371.0;

    public static List<Double> parsLocationToDouble(String location) {

        List<Double> locationArr = new ArrayList<>();
        locationArr.add(Double.parseDouble(location.split(" ")[0]));
        locationArr.add(Double.parseDouble(location.split(" ")[1]));
        return locationArr;
    }

    public static List<Map.Entry<BigInteger, Double>> sortByDistance(Map<BigInteger, List<Double>> locationMap, double myLatitude, double myLongitude) {
        List<Map.Entry<BigInteger, Double>> sortedDataList = new ArrayList<>(locationMap.size());

        for (Map.Entry<BigInteger, List<Double>> entry : locationMap.entrySet()) {
            BigInteger id = entry.getKey();
            List<Double> location = entry.getValue();
            double distance = calculateDistance(myLatitude, myLongitude, location.get(0), location.get(1));

            sortedDataList.add(new AbstractMap.SimpleEntry<>(id, distance));
        }

        sortedDataList.sort(Comparator.comparingDouble(Map.Entry::getValue)); // 거리순으로 정렬

        return sortedDataList;
    }

    // 거리 계산 (하버사인 공식 사용)
    private static double calculateDistance(double myLatitude, double myLongitude, double groupLat, double groupLon) {
        double myLatitudeRad = Math.toRadians(myLatitude);
        double myLongitudeRad = Math.toRadians(myLongitude);
        double groupLatRad = Math.toRadians(groupLat);
        double groupLonRad = Math.toRadians(groupLon);

        double dlon = groupLonRad - myLongitudeRad;
        double dlat = groupLatRad - myLatitudeRad;

        double x = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(myLatitudeRad) * Math.cos(groupLatRad) * Math.pow(Math.sin(dlon / 2), 2);
        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        return EARTH_RADIUS * y;
    }
}
