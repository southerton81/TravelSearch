package dmitriy.com.travelsearch;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ModelsFetcher {
    private final static String mUrl = "http://api.goeuro.com/api/v2/position/suggest/de/";
    private final static int SEC = 1000;

    class FetchResult {
        volatile String mConstraint = "";
        volatile ArrayList<GoEuroPlaceModel> mModels = new ArrayList<GoEuroPlaceModel>();
    }

    FetchResult Fetch(String constraint) {
        ModelsFetcher.FetchResult result = new ModelsFetcher.FetchResult();
        BufferedReader reader = null;
        try {
            StringBuilder request = new StringBuilder(mUrl);
            request.append(constraint);

            URL url = new URL(request.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5 * SEC);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);

            result.mConstraint = constraint;
            result.mModels = new Gson().fromJson(response.toString(),
                    new TypeToken<ArrayList<GoEuroPlaceModel>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }
    }
}
