package com.traffy.attapon.traffybus.DAO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BusStopItemCollectionDao {
    @SerializedName("number_of_nexts")
    @Expose
    private String numberOfNexts;
    @SerializedName("bus_line")
    @Expose
    private String busLine;
    @SerializedName("predict_time")
    @Expose
    private Integer predictTime;
    @SerializedName("stop_name")
    @Expose
    private String stopName;
    @SerializedName("stop_id")
    @Expose
    private String stopId;

    /**
     *
     * @return
     * The numberOfNexts
     */
    public String getNumberOfNexts() {
        return numberOfNexts;
    }

    /**
     *
     * @param numberOfNexts
     * The number_of_nexts
     */
    public void setNumberOfNexts(String numberOfNexts) {
        this.numberOfNexts = numberOfNexts;
    }

    /**
     *
     * @return
     * The busLine
     */
    public String getBusLine() {
        return busLine;
    }

    /**
     *
     * @param busLine
     * The bus_line
     */
    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    /**
     *
     * @return
     * The predictTime
     */
    public Integer getPredictTime() {
        return predictTime;
    }

    /**
     *
     * @param predictTime
     * The predict_time
     */
    public void setPredictTime(Integer predictTime) {
        this.predictTime = predictTime;
    }

    /**
     *
     * @return
     * The stopName
     */
    public String getStopName() {
        return stopName;
    }

    /**
     *
     * @param stopName
     * The stop_name
     */
    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    /**
     *
     * @return
     * The stopId
     */
    public String getStopId() {
        return stopId;
    }

    /**
     *
     * @param stopId
     * The stop_id
     */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }
}
