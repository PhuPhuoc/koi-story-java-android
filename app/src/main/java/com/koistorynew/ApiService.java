package com.koistorynew;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.koistorynew.ui.market.model.PostMarket;
import com.koistorynew.ui.market.model.PostMarketDetail;
import com.koistorynew.ui.myconsult.model.AddConsult;
import com.koistorynew.ui.myconsult.model.MyConsult;
import com.koistorynew.ui.mymarket.model.MyMarket;
import com.koistorynew.ui.mymarket.model.PostMarketRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private final RequestQueue requestQueue;

    public ApiService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getMarketPosts(final DataCallback<List<PostMarket>> callback) {
        String url = "http://api.koistory.site/api/v1/markets";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<PostMarket> posts = new ArrayList<>();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        Log.d("ApiService", "Fetched data: " + response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject postObject = jsonArray.getJSONObject(i);
                            String name = postObject.getString("product_name");
                            String image = postObject.getString("file_path");
                            int price = postObject.getInt("price");
                            String id = postObject.getString("post_id");

                            posts.add(new PostMarket(id, name, image, price, null));
                        }
                        callback.onSuccess(posts);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                }, error -> callback.onError());

        requestQueue.add(stringRequest);
    }

    public void getMyMarketPosts(String userId, final DataMyMarketCallback<List<MyMarket>> callback) {
        String url = "http://api.koistory.site/api/v1/markets/my/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<MyMarket> posts = new ArrayList<>();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        Log.d("ApiService", "Fetched data: " + response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject postObject = jsonArray.getJSONObject(i);
                            String name = postObject.getString("product_name");
                            String image = postObject.getString("file_path");
                            int price = postObject.getInt("price");
                            String id = postObject.getString("post_id");

                            posts.add(new MyMarket(id, name, image, price, null));
                        }
                        callback.onSuccess(posts);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                }, error -> callback.onError());

        requestQueue.add(stringRequest);
    }

    public void getMyConsultPosts(String userId, final DataMyMarketCallback<List<MyConsult>> callback) {
        String url = "http://api.koistory.site/api/v1/consults/my/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<MyConsult> posts = new ArrayList<>();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        Log.d("ApiService", "Fetched data: " + response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject postObject = jsonArray.getJSONObject(i);
                            String id = postObject.getString("id");
                            String user_id = postObject.getString("user_id");
                            String user_name = postObject.getString("user_name");
                            String user_avatar = postObject.getString("user_avatar");
                            String post_type = postObject.getString("post_type");
                            String title = postObject.getString("title");
                            String content = postObject.getString("content");
                            String file_path = postObject.getString("file_path");
                            String created_at = postObject.getString("created_at");

                            posts.add(new MyConsult(id, user_id, user_name, user_avatar, post_type, title, content, file_path, created_at));
                        }
                        callback.onSuccess(posts);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                }, error -> callback.onError());

        requestQueue.add(stringRequest);
    }


    public void getMarketPostDetail(String productId, final DetailCallback callback) {
        String url = "http://api.koistory.site/api/v1/markets/" + productId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject postObject = jsonResponse.getJSONObject("data");

                        String id = postObject.getString("id");
                        String userId = postObject.getString("user_id");
                        String postType = postObject.getString("post_type");
                        String createAt = postObject.getString("created_at");
                        String title = postObject.getString("title");
                        String productName = postObject.getString("product_name");
                        String productType = postObject.getString("product_type");
                        double price = postObject.getDouble("price");
                        String sellerAddress = postObject.getString("seller_address");
                        String phoneNumber = postObject.getString("phone_number");
                        String description = postObject.getString("description");
                        String color = postObject.getString("color");
                        String size = postObject.getString("size");
                        String old = postObject.getString("old");
                        String type = postObject.getString("type");

                        // Parse images
                        List<PostMarketDetail.ImageData> imageDataList = new ArrayList<>();
                        JSONArray imageArray = postObject.getJSONArray("ListImage");
                        for (int i = 0; i < imageArray.length(); i++) {
                            JSONObject imageObject = imageArray.getJSONObject(i);
                            String filePath = imageObject.getString("file_path");
                            int imageOrder = imageObject.getInt("image_order");
                            PostMarketDetail.ImageData imageData = new PostMarketDetail.ImageData();
                            imageData.setFilePath(filePath);
                            imageData.setImageOrder(imageOrder);
                            imageDataList.add(imageData);
                        }

                        PostMarketDetail postMarketDetail = new PostMarketDetail();
                        postMarketDetail.setId(id);
                        postMarketDetail.setUserId(userId);
                        postMarketDetail.setPostType(postType);
                        postMarketDetail.setCreatedAt(createAt);
                        postMarketDetail.setTitle(title);
                        postMarketDetail.setProductName(productName);
                        postMarketDetail.setProductType(productType);
                        postMarketDetail.setPrice(price);
                        postMarketDetail.setSellerAddress(sellerAddress);
                        postMarketDetail.setPhoneNumber(phoneNumber);
                        postMarketDetail.setDescription(description);
                        postMarketDetail.setColor(color);
                        postMarketDetail.setSize(size);
                        postMarketDetail.setOld(old);
                        postMarketDetail.setType(type);
                        postMarketDetail.setListImage(imageDataList);

                        callback.onSuccess(postMarketDetail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                }, error -> callback.onError());

        requestQueue.add(stringRequest);
    }

    public void createMarketPost(PostMarketRequest request, final DataCallback<String> callback) {
        String url = "http://api.koistory.site/api/v1/markets";
        String user_id = UserSessionManager.getInstance().getFbUid();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("color", request.getColor());
            jsonBody.put("description", request.getDescription());
            JSONArray imageArray = new JSONArray();
            for (String image : request.getListImage()) {
                imageArray.put(image);
            }
            jsonBody.put("list_image", imageArray);
            jsonBody.put("old", request.getOld());
            jsonBody.put("phone_number", request.getPhoneNumber());
            jsonBody.put("post_type", request.getPostType());
            jsonBody.put("price", request.getPrice());
            jsonBody.put("product_name", request.getProductName());
            jsonBody.put("product_type", request.getProductType());
            jsonBody.put("seller_address", request.getSellerAddress());
            jsonBody.put("size", request.getSize());
            jsonBody.put("title", request.getTitle());
            jsonBody.put("type", request.getType());
            jsonBody.put("user_id", user_id);

            // Convert list_image to JSONArray
            // Use JSONArray for the list_image

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        // Assuming the response contains a success message or ID
                        String message = response.getString("message");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                },
                error -> {
                    // Log the error details
                    Log.e("ApiService", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ApiService", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("ApiService", "Error Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError();
                }) {
        };
        requestQueue.add(jsonRequest);
    }

    public void updateMarketPost(String id, PostMarketRequest request, final DataCallback<String> callback) {
        String url = "http://api.koistory.site/api/v1/markets/" + id;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("color", request.getColor());
            jsonBody.put("description", request.getDescription());
            jsonBody.put("old", request.getOld());
            jsonBody.put("phone_number", request.getPhoneNumber());
            jsonBody.put("post_type", request.getPostType());
            jsonBody.put("price", request.getPrice());
            jsonBody.put("product_name", request.getProductName());
            jsonBody.put("product_type", request.getProductType());
            jsonBody.put("seller_address", request.getSellerAddress());
            jsonBody.put("size", request.getSize());
            jsonBody.put("title", request.getTitle());
            jsonBody.put("type", request.getType());

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                },
                error -> {
                    Log.e("ApiService", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ApiService", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("ApiService", "Error Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError();
                });

        requestQueue.add(jsonRequest);
    }


    public void getConsult(final DataConsultCallback<List<PostMarket>> callback) {
        String url = "http://api.koistory.site/api/v1/markets";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    List<PostMarket> posts = new ArrayList<>();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        Log.d("ApiService", "Fetched data: " + response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject postObject = jsonArray.getJSONObject(i);
                            String name = postObject.getString("product_name");
                            String image = postObject.getString("file_path");
                            int price = postObject.getInt("price");
                            String id = postObject.getString("post_id");

                            posts.add(new PostMarket(id, name, image, price, null));
                        }
                        callback.onSuccess(posts);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                }, error -> callback.onError());

        requestQueue.add(stringRequest);
    }


    public void deleteMarketPost(String postId, final DataCallback<String> callback) {
        String url = "http://api.koistory.site/api/v1/markets/" + postId;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Xử lý phản hồi khi xóa thành công
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message"); // Nếu API trả về thông điệp
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                },
                error -> {
                    // Xử lý lỗi
                    Log.e("ApiService", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ApiService", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("ApiService", "Error Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError();
                });

        requestQueue.add(stringRequest);
    }

    public void deleteConsultPost(String postId, final DataCallback<String> callback) {
        String url = "http://api.koistory.site/api/v1/consult/" + postId;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Xử lý phản hồi khi xóa thành công
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message"); // Nếu API trả về thông điệp
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                },
                error -> {
                    // Xử lý lỗi
                    Log.e("ApiService", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ApiService", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("ApiService", "Error Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError();
                });

        requestQueue.add(stringRequest);
    }

    public void createConsult(AddConsult request, final DataCallback<String> callback) {
        String url = "http://api.koistory.site/api/v1/consults";
        String user_id = UserSessionManager.getInstance().getFbUid();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("content", request.getContent());
            jsonBody.put("file_path", request.getFile_path());
            jsonBody.put("post_type", request.getPost_type());
            jsonBody.put("title", request.getTitle());
            jsonBody.put("user_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                },
                error -> {
                    // Log the error details
                    Log.e("ApiService", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ApiService", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("ApiService", "Error Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError();
                }) {
        };
        requestQueue.add(jsonRequest);
    }




    public interface DataCallback<T> {
        void onSuccess(T data);

        void onError();
    }

    public interface DataMyMarketCallback<T> {
        void onSuccess(T data);

        void onError();
    }

    public interface DataConsultCallback<T> {
        void onSuccess(T data);

        void onError();
    }


    public interface DetailCallback {
        void onSuccess(PostMarketDetail postMarketDetail);

        void onError();
    }


}
