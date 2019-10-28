package ru.dolphins_it.service.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderApi {

    @Expose
    @SerializedName("date")
        private String date;
        public String getDate ()
        {
            return date;
        }

    @Expose
    @SerializedName("order_status")
        private String order_status;
        public String getOrder_status ()
        {
        return order_status;
        }

    @Expose
    @SerializedName("user_id")
        private String user_id;
        public String getUser_id ()
        {
            return user_id;
        }

    @Expose
    @SerializedName("e_ocher_id")
        private String e_ocher_id;
        public String getE_ocher_id ()
        {
            return e_ocher_id;
        }

    @Expose
    @SerializedName("date_of_adoption")
        private String date_of_adoption;
        public String getDate_of_adoption ()
        {
        return date_of_adoption;
        }

    @Expose
    @SerializedName("reason_for_rejection")
        private String reason_for_rejection;
        public String getReason_for_rejection () { return reason_for_rejection; }

    @Expose
    @SerializedName("tesosq")
        private String tesosq;
        public String getTesosq ()
        {
        return tesosq;
        }

    @Expose
    @SerializedName("order_id")
        private String order_id;
        public String getOrder_id () {
            return order_id;
        }

    @Expose
    @SerializedName("digital_signage_id")
        private String digital_signage_id;
        public String getDigital_signage_id ()
        {
             return digital_signage_id;
        }

    @Expose
    @SerializedName("digital_consultant")
        private String digital_consultant;
        public String getDigital_consultant () { return digital_consultant; }

    @Expose
    @SerializedName("number_of_operators")
    private String number_of_operators;
    public String getNumber_of_operators() { return number_of_operators; }

    @Expose
    @SerializedName("number_of_terminals")
    private String number_of_terminals;
    public String getNumber_of_terminals() { return number_of_terminals; }

    @Expose
    @SerializedName("number_of_boards")
    private String number_of_boards;
    public String getNumber_of_boards() { return number_of_boards; }

    @Expose
    @SerializedName("approximate_coast")
    private String approximate_coast;
    public String getApproximate_coast() { return approximate_coast; }

    @Expose
    @SerializedName("comment")
    private String comment;
    public String getComment() { return comment; }

    @Expose
    @SerializedName("number_of_installed_objects")
    private String number_of_installed_objects;
    public String getNumber_of_installed_objects() { return number_of_installed_objects; }

    @Expose
    @SerializedName("number_of_screens_of_differend_size")
    private String number_of_screens_of_differend_size;
    public String getNumber_of_screens_of_differend_size() { return number_of_screens_of_differend_size; }

    @Expose
    @SerializedName("screen_size")
    private String screen_size;
    public String getScreen_size() { return screen_size; }

    @Expose
    @SerializedName("fastening_type")
    private String fastening_type;
    public String getFastening_type() { return fastening_type; }

    @Expose
    @SerializedName("number_of_screens_object_on")
    private String number_of_screens_object_on;
    public String getNumber_of_screens_object_on() { return number_of_screens_object_on; }

    @Expose
    @SerializedName("touch_screen")
    private String touch_screen;
    public String getTouch_screen() { return touch_screen; }

    @Expose
    @SerializedName("posting")
    private String posting;
    public String getPosting() { return posting; }

    @Expose
    @SerializedName("k_content")
    private String k_content;
    public String getK_content() { return k_content; }

    @Expose
    @SerializedName("type_of_installation_project")
    private String type_of_installation_project;
    public String getType_of_installation_project() { return type_of_installation_project; }

    @Expose
    @SerializedName("unique_content")
    private String unique_content;
    public String getUnique_content() { return unique_content; }

}
