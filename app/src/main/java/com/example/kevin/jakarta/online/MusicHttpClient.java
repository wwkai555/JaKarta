package com.example.kevin.jakarta.online;

/**
 * Created by kevin on 16-12-14.
 */

public class MusicHttpClient {

    private static MusicHttpClient httpClient;

    private MusicHttpClient() {
    }

    public static synchronized MusicHttpClient getInstance() {
        if (httpClient == null) {
            httpClient = new MusicHttpClient();
        }
        return httpClient;
    }


    public String requestCategory() {
        return null;
    }

    public String requestList(String categoryID) {
        return null;
    }

    public byte[] download(String url) {
        return null;
    }
}
