package com.ninetyminutes.africa.network;

import com.ninetyminutes.africa.NewsItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NewsService {

    public interface Callback {
        void onSuccess(List<NewsItem> news);
        void onError(String error);
    }

    public static void getNews(final Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/news" +
                        "?select=*" +
                        "&published=eq.true" +
                        "&order=created_at.desc";

                String response = SupabaseClient.get(endpoint);

                JSONArray array = new JSONArray(response);

                List<NewsItem> newsList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject item = array.getJSONObject(i);

                    String id =
                            item.optString("id", "");

                    String title =
                            item.optString("title", "");

                    String excerpt =
                            item.optString("excerpt", "");

                    String content =
                            item.optString("content", "");

                    String category =
                            item.optString("category", "");

                    String author =
                            item.optString("author", "");

                    String imageUrl =
                            item.optString("image_url", "");

                    String createdAt =
                            item.optString("created_at", "");

                    int views =
                            item.optInt("views", 0);

                    NewsItem news = new NewsItem(
                            id,
                            title,
                            excerpt,
                            content,
                            category,
                            author,
                            imageUrl,
                            createdAt,
                            views
                    );

                    newsList.add(news);
                }

                callback.onSuccess(newsList);

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Unknown error"
                );
            }

        }).start();
    }
}
