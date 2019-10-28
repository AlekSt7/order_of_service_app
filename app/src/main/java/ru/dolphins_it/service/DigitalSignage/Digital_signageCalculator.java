package ru.dolphins_it.service.DigitalSignage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;


/**
 * A simple {@link Fragment} subclass.
 */
public class Digital_signageCalculator extends Fragment {

    private CheckBox consult_check, sensor_check;

    private CheckBox[] checkBoxes1, checkBoxes2;

    private NumberPicker numberPicker, numberPicker2;

    public Spinner spinner1, spinner2, spinner3, spinner4;

    private RadioGroup radioGroup;

    private Button order;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;

    private Session session = new Session();

    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;

    public static TextView header;

    int radio_buttons_group_id, radio_buttons_group = 1; //Значение RadioGroup, значение "4" = "Консультация"

    public Digital_signageCalculator() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session.inicialize(getContext());
        dataBaseHelper = new DataBaseHelper(getContext());
        db = dataBaseHelper.getWritableDatabase();
        session.inicialize(getContext());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_signage_calculator, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        header = getView().findViewById(R.id.name);
        header.startAnimation( AnimationUtils.loadAnimation(getContext(), R.anim.text_blink));

        inputLayout = getView().findViewById(R.id.comment_til);
        inputEditText = getView().findViewById(R.id.comment);

        spinner1 = getView().findViewById(R.id.spinner);
        spinner2 = getView().findViewById(R.id.spinner2);
        spinner3 = getView().findViewById(R.id.spinner3);
        spinner4 = getView().findViewById(R.id.spinner4);

        consult_check = getView().findViewById(R.id.checkBox);
        sensor_check = getView().findViewById(R.id.checkBox9);

        checkBoxes1 = new CheckBox[]{getView().findViewById(R.id.checkBox1), getView().findViewById(R.id.checkBox2),
               getView().findViewById(R.id.checkBox3), getView().findViewById(R.id.checkBox4)};
        checkBoxes2 = new CheckBox[]{getView().findViewById(R.id.checkBox5), getView().findViewById(R.id.checkBox6),
                getView().findViewById(R.id.checkBox7), getView().findViewById(R.id.checkBox8)};

        radioGroup = (RadioGroup) getView().findViewById(R.id.radioGroup);

        numberPicker = (NumberPicker) getView().findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(50);
        numberPicker.setMinValue(1);

        numberPicker2 = (NumberPicker) getView().findViewById(R.id.numberPicker2);
        numberPicker2.setMaxValue(50);
        numberPicker2.setMinValue(1);

        order = getView().findViewById(R.id.button);

        if (session.session_exist()) {
            order.setText("Добавить в корзину");

            inputLayout.setVisibility(View.VISIBLE);
            if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.DIGITAL_SIGNAGE_ID + ") FROM " + dataBaseHelper.DIGITAL_SIGNAGE, null) > 0) { //Проверяем существоваание именно этой услуги в корзине
                order.setText("Добавлено в корзину");
                order.setEnabled(false);

                userCursor = db.query(dataBaseHelper.DIGITAL_SIGNAGE, new String[]{dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS, dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE, dataBaseHelper.SCREEN_SIZE,
                                dataBaseHelper.FASTENING_TYPE, dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON, dataBaseHelper.TOUCH_SCREEN, dataBaseHelper.POSTING, dataBaseHelper.k_CONTENT,
                        dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT, dataBaseHelper.UNIQUE_CONTENT, dataBaseHelper.POSTING_POS, dataBaseHelper.k_CONTENT_POS,
                                dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT_POS, dataBaseHelper.UNIQUE_CONTENT_POS, dataBaseHelper.COMMENT, dataBaseHelper.RADIO_ID},
                        null, null, null, null, null);

                userCursor.moveToFirst();

                if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS)) != 0){
                numberPicker.setValue(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS)));}
                else{
                consult_check.setChecked(true);
                numberPicker.setEnabled(false);
                }


                radioGroup.check(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.RADIO_ID)));

                if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE)) == 4){
                    for (int iter = 0; iter < checkBoxes1.length; iter++) {
                        checkBoxes1[iter].setEnabled(false);}
                    for (int iter = 0; iter < checkBoxes2.length; iter++) {
                        checkBoxes2[iter].setEnabled(false);}
                    numberPicker2.setEnabled(false);
                    sensor_check.setEnabled(false);
                }else{
                    numberPicker2.setValue(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON)));

                    String object_on = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.SCREEN_SIZE));
                    String fastening_type = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.FASTENING_TYPE));

                    for (int iter = 0; iter < checkBoxes1.length; iter++) {
                        if(new String((String) checkBoxes1[iter].getText()).equals(object_on)) {
                            checkBoxes1[iter].setChecked(true);}
                            if(!checkBoxes1[iter].isChecked()){checkBoxes1[iter].setEnabled(false);}
                    }

                    for (int iter = 0; iter < checkBoxes2.length; iter++) {
                        if(new String((String) checkBoxes2[iter].getText()).equals(fastening_type)){
                            checkBoxes2[iter].setChecked(true);}
                        if(!checkBoxes2[iter].isChecked()){checkBoxes2[iter].setEnabled(false);}
                    }

                    if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.TOUCH_SCREEN)) == 1){sensor_check.setChecked(true);}
                    else{sensor_check.setChecked(false);}
                }

                spinner1.setTextAlignment(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.UNIQUE_CONTENT_POS)));
                spinner2.setTextAlignment(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.POSTING_POS)));
                spinner3.setTextAlignment(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.k_CONTENT_POS)));
                spinner4.setTextAlignment(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT_POS)));

                inputEditText.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));
            }
        }

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.session_exist()) {
                    ContentValues contentValues = new ContentValues();

                    if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.ORDER_ID + ") FROM " + dataBaseHelper.TABLE_ORDER, null) > 0) { //Проверяем наличие заказов в базе данных

                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                null, null, null, null, null);

                        String id;

                        userCursor.moveToFirst();
                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));


                        contentValues.put(dataBaseHelper.DIGITAL_SIGNAGE_ID, add_digital_signage());
                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и добавляем услугу

                    }else{ //Если закозов нет
                        contentValues.put(dataBaseHelper.DIGITAL_SIGNAGE_ID, add_digital_signage());
                        db.insert(DataBaseHelper.TABLE_ORDER, null, contentValues); //Добавляем услугу к заказу
                    }

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    order.setText("Добавлено в корзину");
                    order.setEnabled(false);

                }else{
                    Toast toast = Toast.makeText(getContext(),
                            "Авторизируйтесь или зарегистрируйтесь, чтобы оставить заявку", Toast.LENGTH_SHORT);
                    toast.show();
                }}
        });

        //final int[] checked_count = {0};

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = getView().findViewById(checkedId);
                switch (String.valueOf(radioButton.getText())) {
                    case "1":
                        radio_buttons_group = 1;
                        radio_buttons_group_id = radioGroup.getCheckedRadioButtonId(); break;
                    case "2":
                        radio_buttons_group = 2;
                        radio_buttons_group_id = radioGroup.getCheckedRadioButtonId(); break;
                    case "3":
                        radio_buttons_group = 3;
                        radio_buttons_group_id = radioGroup.getCheckedRadioButtonId(); break;
                    case "Консультация":
                        radio_buttons_group = 4;
                        radio_buttons_group_id = radioGroup.getCheckedRadioButtonId();
                        for (int iter = 0; iter < checkBoxes1.length; iter++) {
                            checkBoxes1[iter].setEnabled(false);}
                        for (int iter = 0; iter < checkBoxes2.length; iter++) {
                            checkBoxes2[iter].setEnabled(false);}
                            numberPicker2.setEnabled(false);
                            sensor_check.setEnabled(false);
                    break;
                }

                /*if(checked_count[0] == radio_buttons_group) {
                    for (int iter = 0; iter < checkBoxes1.length; iter++) {
                        if (!checkBoxes1[iter].isChecked()) {
                            checkBoxes1[iter].setEnabled(false);
                        }
                    }
                }
                else*/ if (radio_buttons_group != 4){ int checkBoxes1_check = 0;
                for (int iter = 0; iter < checkBoxes1.length; iter++) {
                    if(checkBoxes1[iter].isChecked()){checkBoxes1_check++;}}
                if(checkBoxes1_check != 0){
                    for (int iter = 0; iter < checkBoxes1.length; iter++) {
                        if(checkBoxes1[iter].isChecked()){checkBoxes1[iter].setEnabled(true);}}
                }else{
                    for (int iter = 0; iter < checkBoxes1.length; iter++) {
                        checkBoxes1[iter].setEnabled(true);
                    }}

                    int checkBoxes2_check = 0;
                    for (int iter = 0; iter < checkBoxes2.length; iter++) {
                    if(checkBoxes2[iter].isChecked()){checkBoxes2_check++;}}
                    if(checkBoxes2_check != 0){
                    for (int iter = 0; iter < checkBoxes2.length; iter++) {
                        if(checkBoxes2[iter].isChecked()){checkBoxes2[iter].setEnabled(true);}}
                    }else{
                        for (int iter = 0; iter < checkBoxes2.length; iter++) {
                            checkBoxes2[iter].setEnabled(true);
                    }}
                    numberPicker2.setEnabled(true);
                    sensor_check.setEnabled(true);
                }

                /*for (int iter = 0; iter < checkBoxes2.length; iter++) {
                    checkBoxes2[iter].setEnabled(true);}
                    numberPicker2.setEnabled(true);
                    sensor_check.setEnabled(true);
            }}*/
                }
        });



        for(int i = 0; i < checkBoxes1.length; i++) { //Задаём слушатели в цикле для массива CheckBox
            final int finalI = i;
            checkBoxes1[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkBoxes1[finalI].isChecked()){
                        for (int iter = 0; iter < checkBoxes1.length; iter++) {
                            if(!checkBoxes1[iter].isChecked()){checkBoxes1[iter].setEnabled(false);}
                        }
                    }else{
                        for (int iter = 0; iter < checkBoxes1.length; iter++) {
                            checkBoxes1[iter].setEnabled(true);
                        }
                    }

                    /*if(checkBoxes1[finalI].isChecked()){
                        checked_count[0]++;
                    }
                    else{
                        checked_count[0]--;
                    }

                        if(checked_count[0] == radio_buttons_group && radio_buttons_group != 4){
                            for (int iter = 0; iter < checkBoxes1.length; iter++) {
                                if(!checkBoxes1[iter].isChecked()){checkBoxes1[iter].setEnabled(false);}
                            }
                        }else if(checked_count[0] < radio_buttons_group) {
                            for (int iter = 0; iter < checkBoxes1.length; iter++) {
                                checkBoxes1[iter].setEnabled(true); }
                        }*/
                    }
            });
        }

        for(int i = 0; i < checkBoxes2.length; i++) { //Задаём слушатели в цикле для массива CheckBox
            final int finalI = i;
            checkBoxes2[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBoxes2[finalI].isChecked()){
                        for (int iter = 0; iter < checkBoxes2.length; iter++) {
                            if(!checkBoxes2[iter].isChecked()){checkBoxes2[iter].setEnabled(false);}
                        }
                    }else{
                        for (int iter = 0; iter < checkBoxes2.length; iter++) {
                            checkBoxes2[iter].setEnabled(true);
                        }
                    }
                }
            });
        }

        consult_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(consult_check.isChecked()){
                    numberPicker.setEnabled(false);
                }else{
                    numberPicker.setEnabled(true);
                }
            }
        });

    }

    int add_digital_signage(){

        ContentValues contentValues = new ContentValues();

        if(consult_check.isChecked()){contentValues.put(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS, 0);}
        else{contentValues.put(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS, numberPicker.getValue());}




            contentValues.put(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE, radio_buttons_group);
            contentValues.put(dataBaseHelper.RADIO_ID, radio_buttons_group_id);
            contentValues.put(dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON, numberPicker2.getValue());
            for (int iter = 0; iter < checkBoxes1.length; iter++) {
                if(checkBoxes1[iter].isChecked()){contentValues.put(dataBaseHelper.SCREEN_SIZE, (String) checkBoxes1[iter].getText());}
            }

            for (int iter = 0; iter < checkBoxes1.length; iter++) {
                if(checkBoxes2[iter].isChecked()){contentValues.put(dataBaseHelper.FASTENING_TYPE, (String) checkBoxes2[iter].getText());}
            }

            if(sensor_check.isChecked()){contentValues.put(dataBaseHelper.TOUCH_SCREEN, 1);}
            else{contentValues.put(dataBaseHelper.TOUCH_SCREEN, 0);}




        contentValues.put(dataBaseHelper.UNIQUE_CONTENT, spinner1.getSelectedItem().toString());
        contentValues.put(dataBaseHelper.POSTING, spinner2.getSelectedItem().toString());
        contentValues.put(dataBaseHelper.k_CONTENT, spinner3.getSelectedItem().toString());
        contentValues.put(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT, spinner4.getSelectedItem().toString());
        contentValues.put(dataBaseHelper.UNIQUE_CONTENT_POS, spinner1.getTextDirection());
        contentValues.put(dataBaseHelper.POSTING_POS, spinner2.getTextAlignment());
        contentValues.put(dataBaseHelper.k_CONTENT_POS, spinner3.getTextAlignment());
        contentValues.put(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT_POS, spinner4.getTextAlignment());
        contentValues.put(dataBaseHelper.COMMENT, inputEditText.getText().toString());
        db.insert(DataBaseHelper.DIGITAL_SIGNAGE, null, contentValues); //Добавляем услугу в таблицу

        userCursor = db.query(dataBaseHelper.DIGITAL_SIGNAGE, new String[]{dataBaseHelper.DIGITAL_SIGNAGE_ID},
                null, null, null, null, null);

        userCursor.moveToFirst();

        return userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_SIGNAGE_ID));

    }

}