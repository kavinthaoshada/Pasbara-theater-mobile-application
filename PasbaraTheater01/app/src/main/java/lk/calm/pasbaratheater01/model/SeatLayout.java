package lk.calm.pasbaratheater01.model;

public class SeatLayout {
    String layoutNumber;
    String layout_floor;

    public SeatLayout() {
    }

    public SeatLayout(String layoutNumber, String layout_floor) {
        this.layoutNumber = layoutNumber;
        this.layout_floor = layout_floor;
    }

    public String getLayoutNumber() {
        return layoutNumber;
    }

    public void setLayoutNumber(String layoutNumber) {
        this.layoutNumber = layoutNumber;
    }

    public String getLayout_floor() {
        return layout_floor;
    }

    public void setLayout_floor(String layout_floor) {
        this.layout_floor = layout_floor;
    }
}
