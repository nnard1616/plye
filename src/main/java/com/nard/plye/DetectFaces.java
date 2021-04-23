package com.nard.plye;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// borrowed this from https://cloud.google.com/vision/docs/detecting-faces#vision_face_detection-java
public class DetectFaces {

    // Detects faces in the specified local image.
    public static void detectFaces(JSONArray reviewJson ) throws IOException, JSONException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        for (int i = 0; i < reviewJson.length(); i++) {
            String imgURL = reviewJson.getJSONObject(i).getJSONObject("user").getString("image_url");
            ByteString imgBytes = ByteString.readFrom(new URL(imgURL).openStream());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

        }

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (int i = 0; i < responses.size(); i++) {
                AnnotateImageResponse res = responses.get(i);

                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }

                reviewJson.getJSONObject(i).getJSONObject("user").put("joyLikelihood", "NIL");
                reviewJson.getJSONObject(i).getJSONObject("user").put("sorrowLikelihood", "NIL");

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    reviewJson.getJSONObject(i).getJSONObject("user").put("joyLikelihood", annotation.getJoyLikelihood());
                    reviewJson.getJSONObject(i).getJSONObject("user").put("sorrowLikelihood", annotation.getSorrowLikelihood());
                }
            }
        }
    }
}
