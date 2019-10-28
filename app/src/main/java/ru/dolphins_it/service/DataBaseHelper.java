package ru.dolphins_it.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "Database";


    public static final String TABLE_USER = "user";

    public static final String USER_TOKEN = "user_token";
    public static final String USER_PHONE_NUMBER = "phone_number";


    public static final String TABLE_ORDER = "orders";

    public static final String ORDER_ID = "order_id";
    public static final String DIGITAL_SIGNAGE_ID = "digital_signage_id";
    public static final String E_OCHER_ID = "e_ocher_id";
    public static final String DIGITAL_CONSULTANT_COMMENT = "digital_consultant_comment";
    public static final String TESOSQ_COMMENT = "tesosq_comment";
    public static final String DIGITAL_CONSULTANT = "digital_consultant";
    public static final String TESOSQ = "tesosq";
    public static final String COMMENT = "comment";


    public static final String E_OCHER = "e_ocher";

    public static final String NUMBER_OF_OPERATORS = "number_of_operators";
    public static final String NUMBER_OF_TERMINALS = "number_of_terminals";
    public static final String NUMBER_OF_BOARDS = "number_of_boards";
    public static final String APPROXIMATE_COAST = "approximate_coast";


    public static final String DIGITAL_SIGNAGE = "digital_signage";

    public static final String NUMBER_OF_INSTALLED_OBJECTS = "number_of_installed_objects";
    public static final String NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE = "number_of_screens_of_differend_size";
    public static final String SCREEN_SIZE = "screen_size";
    public static final String FASTENING_TYPE = "fastening_type";
    public static final String NUMBER_OF_SCREENS_OBJECT_ON = "number_of_screens_object_on";
    public static final String TOUCH_SCREEN = "touch_screen";
    public static final String POSTING = "posting";
    public static final String POSTING_POS = "posting_pos";
    public static final String k_CONTENT = "k_content";
    public static final String TYPE_OF_INSTALLATION_PROJECT = "type_of_installation_project";
    public static final String UNIQUE_CONTENT = "unique_content";
    public static final String k_CONTENT_POS = "k_content_pos";
    public static final String TYPE_OF_INSTALLATION_PROJECT_POS = "type_of_installation_project_pos";
    public static final String UNIQUE_CONTENT_POS = "unique_content_pos";
    public static final String RADIO_ID = "radio_id";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_USER + //Создаём таблицу с заказами
              "(" + USER_PHONE_NUMBER + " integer," + USER_TOKEN + " text" + ")");

        db.execSQL("create table " + TABLE_ORDER + //Создаём таблицу с пользователем
                "(" + ORDER_ID + " integer primary key autoincrement," + DIGITAL_SIGNAGE_ID + " integer," + E_OCHER_ID + " integer," + DIGITAL_CONSULTANT_COMMENT + " text," + TESOSQ_COMMENT + " text," + DIGITAL_CONSULTANT + " integer," + TESOSQ + " integer" + ")");

        db.execSQL("create table " + E_OCHER + //Создаём таблицу для параметров электронной очереди
                "(" + E_OCHER_ID + " integer primary key autoincrement," + NUMBER_OF_OPERATORS + " integer," + NUMBER_OF_TERMINALS + " integer," + NUMBER_OF_BOARDS + " integer," +
                APPROXIMATE_COAST + " integer," + COMMENT + " text" + ")");

        db.execSQL("create table " + DIGITAL_SIGNAGE + //Создаём таблицу для параметров цифрового консультанта
                "(" + DIGITAL_SIGNAGE_ID + " integer primary key autoincrement," + NUMBER_OF_INSTALLED_OBJECTS + " integer," + NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE + " integer," + SCREEN_SIZE + " text,"
                + FASTENING_TYPE + " text," + NUMBER_OF_SCREENS_OBJECT_ON + " integer," + TOUCH_SCREEN + " integer," + POSTING + " text,"
                + k_CONTENT + " text," + TYPE_OF_INSTALLATION_PROJECT + " text," + UNIQUE_CONTENT + " text," + POSTING_POS + " integer," + k_CONTENT_POS + " integer," + TYPE_OF_INSTALLATION_PROJECT_POS +
                " integer," + UNIQUE_CONTENT_POS + " integer," + COMMENT + " text," + RADIO_ID + " integer" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_USER + TABLE_ORDER + E_OCHER + DIGITAL_SIGNAGE);

        onCreate(db);

    }
}