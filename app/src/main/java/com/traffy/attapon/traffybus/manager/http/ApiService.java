package com.traffy.attapon.traffybus.manager.http;

import com.traffy.attapon.traffybus.DAO.BusStopItemCollectionDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {
    @GET("arrival_time_next_by_vehicle.php")
    Call<List<BusStopItemCollectionDao>> loadBusStopList(@Query("bmta_id") String bmta_id);
}
