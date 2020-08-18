package com.example.fueltrackv1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.Object;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class signup extends AppCompatActivity {

    /*int REQUEST_CODE = 44;
    int active_code = 0;
    double latitude = 0.0;
    double longitude = 0.0;*/

    String sexType="Select", workType="Select", currentDate, currentTime, key, phone;
    RadioGroup regSex, regWork;
    RadioButton radioSexType, radioWorkType;
    Button signUp, login;
    LinearLayout inAppNotification;
    TextInputLayout regFirstName, regLastName, regPassword, regConfirmPassword, regMobile;
    AutoCompleteTextView regAddress;
    int f=0;

    FirebaseDatabase rootNode;
    DatabaseReference reference,ref,Node;

    private static final String[] States = new String[]{"Ariyalur", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kanchipuram", "Kanniyakumari", "Karur", "Krishnagiri", "Madurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambular", "Pudukkottai", "Ramnathapuram", "Salem", "Sivagangai", "Thanjavur", "Theni", "Thoothukodi", "Tiruvarur", "Tirunelveli", "Tiruchirappalli", "Tiruvallur", "Tiruppur", "Tiruvannamalai", "Vellore", "Villupuram", "Virudhnagar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        //Init the text variables
        regFirstName = findViewById(R.id.firstName);
        regLastName = findViewById(R.id.lastName);
        regPassword = findViewById(R.id.signUpPassword);
        regConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        regMobile = findViewById(R.id.mobileNumber);
        regAddress = findViewById(R.id.text_list);
        regSex = findViewById(R.id.signUpSex);
        regWork = findViewById(R.id.workType);
        inAppNotification = findViewById(R.id.signUpRootLayout);

        String[] disticts=getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(signup.this, android.R.layout.simple_list_item_1, States );
        regAddress.setAdapter(adapter);
        //Init the button
        signUp = findViewById(R.id.signUpGo);
        login = findViewById(R.id.returnLogin);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
                signup.this.finish();
            }
        });


    }

    //validation
    public void radioClick(View view) {
        int sexId = regSex.getCheckedRadioButtonId();
        radioSexType = findViewById(sexId);
        sexType = radioSexType.getText().toString();
    }

    public void radioClickWork(View view) {
        int workId = regWork.getCheckedRadioButtonId();
        radioWorkType = findViewById(workId);
        workType = radioWorkType.getText().toString();
    }

    private Boolean validateFName() {
        String val = regFirstName.getEditText().getText().toString();

        if (val.isEmpty()) {
            regFirstName.setError("First Name cannot be empty");
            return false;
        } else {
            regFirstName.setError(null);
            regFirstName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[@#$%]).{6,20})";

        if (val.isEmpty()) {
            regPassword.setError("Password cannot be empty");
            return false;
        } else if (!val.matches(passwordPattern)) {
            regPassword.setError("Password too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = regConfirmPassword.getEditText().getText().toString();
        String passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[@#$%]).{6,20})";

        if (val.isEmpty()) {
            regConfirmPassword.setError("Password cannot be empty");
            return false;
        } else if (!val.matches(passwordPattern)) {
            regConfirmPassword.setError("Password too weak" +
                    "- min one LowerCase" +
                    "- min one special character" +
                    "- min 6 characters");
            Toast.makeText(signup.this,"Password too weak - min one LowerCase - min one LowerCase - min one special character - min 6 characters",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            regConfirmPassword.setError(null);
            regConfirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePasswordCheck() {
        String val = regPassword.getEditText().getText().toString();
        String val1 = regConfirmPassword.getEditText().getText().toString();

        if (!val.equals(val1)) {
            regConfirmPassword.setError("Password does not match");
            return false;
        } else {
            regConfirmPassword.setError(null);
            regConfirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateMobile() {
        String val = regMobile.getEditText().getText().toString();
        if (val.isEmpty()) {
            regMobile.setError("Mobile cannot be empty");
            return false;
        } else if (val.length() != 10) {
            regMobile.setError("Invalid mobile number");
            return false;
        }else if(valonDatabae()==1) {
            regMobile.setError("Number already Exists");
            return false;
        }else {
                regMobile.setError(null);
                regMobile.setErrorEnabled(false);
                return true;
        }
    }

    private int valonDatabae()
    {
        phone = regMobile.getEditText().getText().toString();
        Node = FirebaseDatabase.getInstance().getReference();
        Node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(phone).exists())
                {
                    f=1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return f;
    }

    private Boolean validateAddress() {
        String val = regAddress.getText().toString();

        if (val.isEmpty()) {
            regAddress.setError("District field cannot be Empty");
            return false;
        } else {
            regAddress.setError(null);
            return true;
        }
    }

    private Boolean validateSex()
    {
        if(sexType.equals("Select"))
        {
            Toast.makeText(signup.this,"Select any Gender Option",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private Boolean validateWork()
    {
        if(workType.equals("Select"))
        {
            Toast.makeText(signup.this,"Select any WorkType Option",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public void registerUser(View view) {

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        if (!validateFName() || !validatePassword() || !validateConfirmPassword() ||
                !validatePasswordCheck() || !validateMobile() || !validateAddress() || !validateSex() || !validateWork()) {
            //Toast.makeText(signup.this, "CHECK YOUR DETAILS", Toast.LENGTH_SHORT).show();
            f=0;
            return;
        }else{
            //Getting values
                    String fName = regFirstName.getEditText().getText().toString();
                    String lName = regLastName.getEditText().getText().toString();
                    String sex = sexType;
                    String userStatus = workType;
                    String password = regPassword.getEditText().getText().toString();
                    String mobile = regMobile.getEditText().getText().toString();
                    String address = regAddress.getText().toString();


                    UserDetail userDetail = new UserDetail(fName, lName, password, mobile,
                            sex, userStatus, address);
                    Log.d("fb", userDetail.toString());
                    //reference.child(mobile).setValue(userDetail);
                    reference.child(mobile + "/firstName").setValue(fName);
                    reference.child(mobile + "/lastName").setValue(lName);
                    reference.child(mobile + "/mobile").setValue(mobile);
                    reference.child(mobile + "/password").setValue(password);
                    reference.child(mobile + "/sex").setValue(sex);
                    reference.child(mobile + "/District").setValue(address);
                    reference.child(mobile + "/userStatus").setValue(userStatus);
                    reference.child(mobile + "/image").setValue("https://firebasestorage.googleapis.com/v0/b/fueltechsystem.appspot.com/o/Profile%20pictures%2Fman.png?alt=media&token=45855202-5a17-40e2-b9df-84c40b6a4dff");
                    if (!userStatus.equals("Others")) {
                        //setDateAndTime();
                        ref = rootNode.getReference("Professionals");
                        ref.child(address).child(userStatus).child(mobile).child("FirstName").setValue(fName);
                        ref.child(address).child(userStatus).child(mobile).child("LastName").setValue(lName);
                        ref.child(address).child(userStatus).child(mobile).child("PhoneNo").setValue(mobile);
                        ref.child(address).child(userStatus).child(mobile).child("image").setValue("https://firebasestorage.googleapis.com/v0/b/fueltechsystem.appspot.com/o/Profile%20pictures%2Fman.png?alt=media&token=45855202-5a17-40e2-b9df-84c40b6a4dff");
                        //ref.child(key).setValue("2");
                    }

                    Intent intent = new Intent(signup.this, verifyNumber.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
            }
    }

    private void setDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MM dd, yyyy ");
        currentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss a");
        currentTime = time.format(calendar.getTime());

        key = currentDate+currentTime;
    }

}


