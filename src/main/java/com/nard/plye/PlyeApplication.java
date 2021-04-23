package com.nard.plye;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@RestController
public class PlyeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlyeApplication.class, args);
    }

    @GetMapping("/plye")
    public ModelAndView printResults(@RequestParam(value = "myTerm", defaultValue = "Jamaican") String term,
                               @RequestParam(value = "myLocation", defaultValue = "Monona") String location)
            throws IOException, JSONException {

        String getURL1 = String.format("https://api.yelp.com/v3/businesses/search?term=%s&location=%s",
                URLEncoder.encode( term,       StandardCharsets.UTF_8 ),
                URLEncoder.encode( location,   StandardCharsets.UTF_8 ));

        String response1 = getResponse(getURL1);
        JSONObject response1json = new JSONObject(response1);
        ModelAndView result = new ModelAndView();

        if ( response1json.has("businesses") ) {

            JSONObject topResult = response1json.getJSONArray("businesses").getJSONObject(0);
            String topResultID = topResult.get("id").toString();

            String getURL2 = String.format("https://api.yelp.com/v3/businesses/%s/reviews",
                    URLEncoder.encode( topResultID, StandardCharsets.UTF_8 ));

            String response2 = getResponse(getURL2);
            JSONObject response2json = new JSONObject(response2);

            JSONArray reviewsJson = response2json.getJSONArray("reviews");
            DetectFaces.detectFaces(reviewsJson);

            result.setViewName("results");
            result.addObject("name", topResult.get("name").toString());
            result.addObject("reviews", response2json.toString(4).replace("\\", ""));

        } else if ( response1json.has("error") ) {

            String error = String.format("No restaurants found for search term: %s, located at: %s",
                    term, location);

            result.setViewName("error");
            result.addObject("errorSummary", error);
            result.addObject("errorJSON", new JSONObject(response1).toString(4));

        }

        return result;
    }

    private String getResponse(String url) throws IOException {
        String result = "";

        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        GetMethod get = new GetMethod(url);
        get.setRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("YELP_FUSION_API_KEY").toString());

        client.executeMethod(get);
        result = get.getResponseBodyAsString();

        get.releaseConnection();

        return result;
    }

}
