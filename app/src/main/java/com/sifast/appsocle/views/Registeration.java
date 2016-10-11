package com.sifast.appsocle.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.tasks.RegistrationTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_LONG;

public class Registeration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    boolean checkInputs = true;
    private User registeredUser;
    private Button butSignUp,butCalendar;
    private EditText txtUsername, txtEmail, txtPassword, txtRepeatedPassword, txtDateOfBirth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        butSignUp = (Button) findViewById(R.id.butSignUp);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtRepeatedPassword = (EditText) findViewById(R.id.txtRepeatPassword);
        txtDateOfBirth = (EditText) findViewById(R.id.txtDateOfBirth);
        butCalendar= (Button) findViewById(R.id.butCalender);
        signUp();
        checkUsername();
        checkEmail();
        checkRepeatPassword();
        checkPasswordLength();
        pickDate();
    }

public void pickDate(){

    butCalendar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    (DatePickerDialog.OnDateSetListener) Registeration.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setAccentColor(Color.rgb(104,47,132));
            dpd.show(getFragmentManager(), "Datepickerdialog");



        }
    });




}


    public void signUp() {
        //a function called once you click on signUp button
        final Date signUpDate = new Date();
        butSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, repeatedPass, email, dateOfBirh;
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                email = txtEmail.getText().toString();
                dateOfBirh = txtDateOfBirth.getText().toString();
                //setting the registered user
                registeredUser = new User(username, password, email, dateOfBirh, signUpDate.toString());
                if (checkInputs) {
                    RegistrationTask registerTask = new RegistrationTask(getRegisteredUser(), Registeration.this);
                    registerTask.execute();
                    txtUsername.setText("");
                    txtEmail.setText("");
                    txtDateOfBirth.setText("");
                    txtPassword.setText("");
                    txtRepeatedPassword.setText("");
                    Toast.makeText(getApplicationContext(),"User added successfully",Toast.LENGTH_LONG).show();
                } else {
                    String inputsErrorMsg = getBaseContext().getResources().getString(R.string.checkInputsMsg);
                    Toast.makeText(getApplicationContext(), inputsErrorMsg, LENGTH_LONG).show();
                }

            }
        });

    }



    public void checkUsername() {

        Firebase.setAndroidContext(getApplicationContext());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //setting connexion parameter
                String dbUsersUrl =getApplicationContext().getResources().getString(R.string.dbUsersUrl);
                final Firebase ref = new Firebase(dbUsersUrl);
                Query query = ref.orderByChild("username").equalTo(String.valueOf(txtUsername.getText()));


                //get the data from th DB
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //checking if the user exist
                        if (dataSnapshot.exists()) {
                            String usernameExistanceError=getBaseContext().getResources().getString(R.string.usernameExistanceError);
                            txtUsername.setError(usernameExistanceError);
                            checkInputs = false;
                        }

else checkInputs=true;
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }


        };
        txtUsername.addTextChangedListener(fieldValidatorTextWatcher);


    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void checkEmail() {

        Firebase.setAndroidContext(getApplicationContext());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (validEmail(String.valueOf(txtEmail.getText()))) {
                    //setting connexion parameter
                    String dbUsersUrl =getApplicationContext().getResources().getString(R.string.dbUsersUrl);
                    final Firebase ref = new Firebase(dbUsersUrl);
                    Query query = ref.orderByChild("email").equalTo(String.valueOf(txtEmail.getText()));


                    //get the data from th DB
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //checking if the user exist
                            if (dataSnapshot.exists()) {
                                String emailExistanceError=getBaseContext().getResources().getString(R.string.mailExistError);
                                txtEmail.setError(emailExistanceError);
                                checkInputs = false;
                            }

else checkInputs=true;
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } else {
                    String emailFormMsgError=getBaseContext().getResources().getString(R.string.notValidMailError);
                    txtEmail.setError(emailFormMsgError);
                }

            }
        };
        txtEmail.addTextChangedListener(fieldValidatorTextWatcher);


    }

    public void checkPasswordLength() {
final int minPasswordLength=5;
        Firebase.setAndroidContext(getApplicationContext());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txtPassword.getText().length() < minPasswordLength) {
                    String passwordLengthErrorMsg=getBaseContext().getResources().getString(R.string.pswdShortError);
                    txtPassword.setError(passwordLengthErrorMsg);
                    checkInputs = false;
                }
                else
                    checkInputs=true;
            }


        };
        txtPassword.addTextChangedListener(fieldValidatorTextWatcher);


    }

    public void checkRepeatPassword() {

        Firebase.setAndroidContext(getApplicationContext());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!String.valueOf(txtPassword.getText()).equals(String.valueOf(txtRepeatedPassword.getText()))) {
                    String passwordsErrorMsg="passwords are not alike";
                    txtRepeatedPassword.setError(passwordsErrorMsg);
                    checkInputs = false;
                }
                else checkInputs=true;
            }


        };
        txtRepeatedPassword.addTextChangedListener(fieldValidatorTextWatcher);


    }
    public User getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(User registeredUser) {
        this.registeredUser = registeredUser;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        txtDateOfBirth.setText(date);
    }
}
