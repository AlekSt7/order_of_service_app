package ru.dolphins_it.service.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiService { //Тут описываем то, что будем парсить

    //Авториазция
    @Expose
    @SerializedName("privileges")
    private String privileges;
    public String getPrivileges() {
        return privileges;
    }

    @SerializedName("token")
    private String token;
    public String getToken() {
        return token;
    }

    @Expose
    @SerializedName("result")
    private String result;
    public String getSmsResult() {
        return result;
    }


    //Сессия
    @Expose
    @SerializedName("session_exist")
    private String status;
    public String getTokenStatus() { return status; }

    //Данные пользователя
    @Expose
    @SerializedName("name")
    private String user_name;
    public String getUserName() { return user_name; }

    @Expose
    @SerializedName("phone_number")
    private String phone_number;
    public String getPhoneNumber() { return phone_number; }

    @Expose
    @SerializedName("organization_name")
    private String organization_name;
    public String getOrganizationName() { return organization_name; }

    @Expose
    @SerializedName("job")
    private String job;
    public String getJob() { return job; }

    @Expose
    @SerializedName("e_mail")
    private String e_mail;
    public String getE_mail() { return e_mail; }

    @Expose
    @SerializedName("sms_mailing")
    private String sms_mailing;
    public String getSmsMailing() { return sms_mailing; }

    //Заявки
    @Expose
    @SerializedName("order_id")
    private String order_id;
    public String getOrderId() { return order_id; }

    @Expose
    @SerializedName("date")
    private String order_date;
    public String getOrderdate() { return order_date; }

    @Expose
    @SerializedName("order_status")
    private String order_status;
    public String getOrderStatus() { return order_status; }

    @Expose
    @SerializedName("access_right")
    private String access_right;
    public String getAccess_right() { return access_right; }

    @Expose
    @SerializedName("date_of_adoption")
    private String date_of_adoption;
    public String getDate_of_adoption() { return date_of_adoption; }

    @Expose
    @SerializedName("reason_for_rejection")
    private String reason_for_rejection;
    public String getReason_for_rejection() { return reason_for_rejection; }
}
