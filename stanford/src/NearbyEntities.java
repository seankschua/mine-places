import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class NearbyEntities {
	
	public static String testEntity = "The Royal Horseguards";
	public static String testEntity2 = "The Nadler Kensington";
	public static int otherEntitiesBoundRangeSQM = 200;
	
	public static final String googleGeoPrefixURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	public static final String googleGeoSuffixURL = "&key=" + Key.googleGeocode;
	
	private static String jsonCoord(String address) throws IOException {
		URL url = new URL(googleGeoPrefixURL + address + googleGeoSuffixURL);
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		String jsonResult = "";
		while ((inputLine = in.readLine()) != null) {
		    jsonResult += inputLine;
		}
		in.close();
		return jsonResult; 
	}
	
	//Haversine formula
	//http://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371.0; //3958.75; // miles (or 6371.0 kilometers)
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
    }
	
	public static void main(String[] args) throws JsonSyntaxException, UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		
		Gson gson = new Gson();
		
		System.out.println("testEntity: " + testEntity);
		
		GoogleGeoCodeResponse result = gson.fromJson(jsonCoord(URLEncoder.encode(testEntity, "UTF-8")),GoogleGeoCodeResponse.class);
		double lat = Double.parseDouble(result.results[0].geometry.location.lat);
		double lng = Double.parseDouble(result.results[0].geometry.location.lng);
		
		System.out.println("lat: " + lat + ", lng: " + lng);
		
		System.out.println("testEntity2: " + testEntity2);
		
		GoogleGeoCodeResponse result2 = gson.fromJson(jsonCoord(URLEncoder.encode(testEntity2, "UTF-8")),GoogleGeoCodeResponse.class);
		double lat2 = Double.parseDouble(result2.results[0].geometry.location.lat);
		double lng2 = Double.parseDouble(result2.results[0].geometry.location.lng);
		
		System.out.println("lat2: " + lat2 + ", lng2: " + lng2);
		
		System.out.println("Distance: " + VincentyDistanceCalculator.getDistance(lat,lng,lat2,lng2) + "km");
		
	}

}
