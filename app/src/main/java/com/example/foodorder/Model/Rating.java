package com.example.foodorder.Model;

public class Rating {
    private String userPhone;
    private String userName;
    private String foodId;
    private String rateValue;
    private String comment;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Rating(String userPhone, String userName, String foodId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public Rating() {
    }


}
