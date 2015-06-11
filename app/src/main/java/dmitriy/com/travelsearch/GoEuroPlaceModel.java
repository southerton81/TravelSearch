package dmitriy.com.travelsearch;

import java.util.Comparator;

public class GoEuroPlaceModel {
    long _id;
    String key;
    String name;
    String fullname;
    String iata_airport_code;
    String type;
    String country;
    GeoPosition geo_position;
    long locationId;
    boolean inEurope;
    String countryCode;
    boolean coreCountry;
    double distance;

    class GeoPosition
    {
        double latitude;
        double longitude;
    }
}