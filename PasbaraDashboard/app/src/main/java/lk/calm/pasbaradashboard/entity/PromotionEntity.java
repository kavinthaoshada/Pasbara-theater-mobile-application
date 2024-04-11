package lk.calm.pasbaradashboard.entity;

public class PromotionEntity {
    private String promotion_name;
    private int discount_amount;
    private int minimum_booking;
    private int duration_period;
    private String timeStamp;

    public PromotionEntity(String promotion_name, int discount_amount, int minimum_booking, int duration_period, String timeStamp) {
        this.promotion_name = promotion_name;
        this.discount_amount = discount_amount;
        this.minimum_booking = minimum_booking;
        this.duration_period = duration_period;
        this.timeStamp = timeStamp;
    }

    public String getPromotion_name() {
        return promotion_name;
    }

    public void setPromotion_name(String promotion_name) {
        this.promotion_name = promotion_name;
    }

    public int getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(int discount_amount) {
        this.discount_amount = discount_amount;
    }

    public int getMinimum_booking() {
        return minimum_booking;
    }

    public void setMinimum_booking(int minimum_booking) {
        this.minimum_booking = minimum_booking;
    }

    public int getDuration_period() {
        return duration_period;
    }

    public void setDuration_period(int duration_period) {
        this.duration_period = duration_period;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
