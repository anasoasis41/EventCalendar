package riahi.com.eventcalendar.apiclient;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface JsonService {

    @GET("/getEvents.php")
    void listEvents(Callback<List<Event>> eventsCallback);

    @FormUrlEncoded
    @POST("/calendar/insertEvent.php")
    public void insertEvent(
            @Field("name") String nameET,
            @Field("dayOfMonth") String dateET,
            @Field("startTime") String startET,
            @Field("endTime") String endET,
            @Field("color") String colorEvent,
            Callback<Response> callback
    );
}
