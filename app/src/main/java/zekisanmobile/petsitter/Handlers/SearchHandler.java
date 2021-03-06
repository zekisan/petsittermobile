package zekisanmobile.petsitter.Handlers;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zekisanmobile.petsitter.Model.Animal;
import zekisanmobile.petsitter.Model.Sitter;
import zekisanmobile.petsitter.Owner.OwnerHomeActivity;

public class SearchHandler extends AsyncTask<String, Void, ArrayList<Sitter>> {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_SEARCH_URL = "https://petsitterapi.herokuapp.com/api/v1/pet_owners/";
    private final static String FINAL_SEARCH_URL = "/search_sitters";
    private OkHttpClient client = new OkHttpClient();
    private Context context;
    ArrayList<Sitter> sitters = new ArrayList<Sitter>();

    public SearchHandler(Context context){
        this.context = context;
    }

    @Override
    protected ArrayList<Sitter> doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[0]);
        Request request = new Request.Builder()
                .url(BASE_SEARCH_URL + params[1] + FINAL_SEARCH_URL)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray animalsArray = jsonObject.getJSONArray("animals");
                List<Animal> animals = new ArrayList<Animal>();

                for(int j = 0; j < animalsArray.length(); j++){
                    JSONObject animalObject = animalsArray.getJSONObject(j);
                    Animal animal = new Animal();
                    animal.setId(animalObject.getLong("id"));
                    animal.setName(animalObject.getString("name"));
                    animals.add(animal);
                }
                Sitter sitter = new Sitter(jsonObject.getLong("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("address"),
                        jsonObject.getString("photo"),
                        jsonObject.getString("header_background"),
                        Float.parseFloat(jsonObject.getString("latitude")),
                        Float.parseFloat(jsonObject.getString("longitude")),
                        jsonObject.getString("district"),
                        Double.valueOf(jsonObject.getString("value_hour")),
                        Double.valueOf(jsonObject.getString("value_shift")),
                        Double.valueOf(jsonObject.getString("value_day")),
                        jsonObject.getString("about_me"),
                        animals);
                sitters.add(sitter);

            }
            return sitters;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Sitter> sitters) {
        ((OwnerHomeActivity) context).updateSitterList(sitters);
    }
}
