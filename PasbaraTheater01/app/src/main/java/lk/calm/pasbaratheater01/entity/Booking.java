package lk.calm.pasbaratheater01.entity;

import java.util.List;

public class Booking {
    private String userEmail;
    private String showTimeDocId;
    private String layoutNumber;
    private List<Integer> seatIdList;
    double total_cost;
    double paymnt;
    int status;
    String timeStamp;

    public Booking(String userEmail, String showTimeDocId, String layoutNumber,
                   List<Integer> seatIdList, double total_cost, double paymnt, int status, String timeStamp) {
        this.userEmail = userEmail;
        this.showTimeDocId = showTimeDocId;
        this.layoutNumber = layoutNumber;
        this.seatIdList = seatIdList;
        this.total_cost = total_cost;
        this.paymnt = paymnt;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public Booking() {
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getShowTimeDocId() {
        return showTimeDocId;
    }

    public void setShowTimeDocId(String showTimeDocId) {
        this.showTimeDocId = showTimeDocId;
    }

    public String getLayoutNumber() {
        return layoutNumber;
    }

    public void setLayoutNumber(String layoutNumber) {
        this.layoutNumber = layoutNumber;
    }

    public List<Integer> getSeatIdList() {
        return seatIdList;
    }

    public void setSeatIdList(List<Integer> seatIdList) {
        this.seatIdList = seatIdList;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public double getPaymnt() {
        return paymnt;
    }

    public void setPaymnt(double paymnt) {
        this.paymnt = paymnt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
