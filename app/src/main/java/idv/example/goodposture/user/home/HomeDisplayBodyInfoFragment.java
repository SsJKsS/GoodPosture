package idv.example.goodposture.user.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.MyRecord;
import idv.example.goodposture.user.my.Myinfo;

public class HomeDisplayBodyInfoFragment extends Fragment {
    private Activity activity;
    private TextView tvWelcome, tvDisplayBodyStatus, tvDisplayBMI, tvDisplayBMR,
            tvEatRecord, tvExerciseRecord;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Bodyinfo bodyinfo;
    private Myinfo myinfo;
    private List<MyRecord> myExerciseRecordList, myEatRecordList;
    private Float[] totalExerciseCalorie = new Float[24];
    private Float[] totalEatCalorie = new Float[24];
    private float exerciseTotal, eatTotal = 0;
    private Button btGoSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myExerciseRecordList = new ArrayList<>();
        myEatRecordList = new ArrayList<>();
        bodyinfo = new Bodyinfo();
        myinfo = new Myinfo();
        for (int i = 0; i <= 23; i++) {
            totalExerciseCalorie[i] = 0f;
        }
        for (int i = 0; i <= 23; i++) {
            totalEatCalorie[i] = 0f;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_home_display_body_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleWelcomeInfo();
        handleBodyInfo();
        handleRecordInfo();
        handleButton();
    }

    private void findViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvDisplayBodyStatus = view.findViewById(R.id.tv_displayBodyStatus);
        tvDisplayBMI = view.findViewById(R.id.tv_displayBMI);
        tvDisplayBMR = view.findViewById(R.id.tv_displayBMR);
        tvEatRecord = view.findViewById(R.id.tv_eatRecord);
        tvExerciseRecord = view.findViewById(R.id.tv_exerciseRecord);
        btGoSetting = view.findViewById(R.id.bt_goSetting);
    }

    private void handleWelcomeInfo() {
        // ???????????? (????????????????????? uid ??? db ??????????????????)
        DocumentReference queRef = db.collection("my_info")
                .document(auth.getCurrentUser().getUid());
        // ???????????????????????? & ???????????????
        queRef.get().addOnCompleteListener(queryTask -> {
            // ??????????????????
            if (queryTask.isSuccessful()) {
                // ?????? getResult() ?????????????????????????????????
                DocumentSnapshot document = queryTask.getResult();
                // ???????????????????????????????????????????????????
                assert document != null;
                if (!document.exists()) {
                    String now = new SimpleDateFormat("HH")
                            .format(Calendar.getInstance().getTime());
                    if (6 <= Integer.parseInt(now) && Integer.parseInt(now) <= 11) {
                        tvWelcome.append("?????????");
                    } else if (12 <= Integer.parseInt(now) && Integer.parseInt(now) <= 17) {
                        tvWelcome.append("?????????");
                    } else {
                        tvWelcome.append("?????????");
                    }
                } else {
                    showWelcomeInfo();
                }
            }
        });
    }

    private void showWelcomeInfo() {
        // ??????????????????
        db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // ????????????
                .get()
                // ???????????????????????????
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // ???????????????????????????????????????
                        DocumentSnapshot documentSnapshot = task.getResult();
                        myinfo = documentSnapshot.toObject(Myinfo.class);
                        String now = new SimpleDateFormat("HH")
                                .format(Calendar.getInstance().getTime());
                        if (6 <= Integer.parseInt(now) && Integer.parseInt(now) <= 11) {
                            tvWelcome.append(myinfo.getNickname() + "????????????");
                        } else if (12 <= Integer.parseInt(now) && Integer.parseInt(now) <= 17) {
                            tvWelcome.append(myinfo.getNickname() + "????????????");
                        } else {
                            tvWelcome.append(myinfo.getNickname() + "????????????");
                        }
                    } else {
                        String message = task.getException() == null ?
                                "????????????" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleBodyInfo() {
        /**
         ?????????????????????????????? (EX : uid)
         .whereEqualTo("uid", auth.getCurrentUser().getUid())
         ?????????????????? (???????????????????????? ID ????????????????????? UID)
         .document(auth.getCurrentUser().getUid())
         ?????????????????? (?????????????????????????????????????????????????????? ID)
         .document()
         */
        // ??????????????????
        db.collection("body_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // ????????????
                .get()
                // ???????????????????????????
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // ???????????????????????????????????????
                        // for (DocumentSnapshot documentSnapshot : task.getResult())
                        DocumentSnapshot documentSnapshot = task.getResult();
                        bodyinfo = documentSnapshot.toObject(Bodyinfo.class);
                        showBodyInfo();
                    } else {
                        String message = task.getException() == null ?
                                "????????????" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBodyInfo() {
        if (bodyinfo.getHeight() == null) {
            tvDisplayBodyStatus.append(" ?????????");
            tvDisplayBMI.append(" ?????????");
            tvDisplayBMR.append(" ?????????");
        } else {
            Double height = Double.parseDouble(bodyinfo.getHeight());
            Double weight = Double.parseDouble(bodyinfo.getWeight());
            double bmi = weight / ((height / 100) * (height / 100));
            DecimalFormat df = new DecimalFormat("#.#");  // ????????????????????????
            // ?????? bmi
            if (bmi < 18.5) {
                tvDisplayBodyStatus.append(" ?????? ( " + bodyinfo.getAge() + " ??? )");
            } else if (18.5 <= bmi && bmi < 24) {
                tvDisplayBodyStatus.append(" ?????? ( " + bodyinfo.getAge() + " ??? )");
            } else if (24 <= bmi && bmi < 27) {
                tvDisplayBodyStatus.append(" ?????? ( " + bodyinfo.getAge() + " ??? )");
            } else {
                tvDisplayBodyStatus.append(" ?????? ( " + bodyinfo.getAge() + " ??? )");
            }
            tvDisplayBMI.append(" " + Double.parseDouble(df.format(bmi)));
            // ?????? bmr
            // ?????? = 66 + (13.7 ?? ??????) + (5.0 ?? ??????) ??? (6.8 ?? ??????)
            // ?????? = 655 + (9.6 ?? ??????) + (1.8 ?? ??????) ??? (4.7 ?? ??????)
            Double age = Double.parseDouble(bodyinfo.getAge());
            if (bodyinfo.getGender().equals("male")) {
                double bmr = 66 + (13.7 * weight) + (5.0 * height) - (6.8 * age);
                String bmrStr = Math.round(bmr) + " (???)";
                tvDisplayBMR.append(" " + bmrStr);
            } else {
                double bmr = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
                String bmrStr = Math.round(bmr) + " (???)";
                tvDisplayBMR.append(" " + bmrStr);
            }
        }
    }

    private void handleRecordInfo() {
        String uid = auth.getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy/MM/dd")
                .format(Calendar.getInstance().getTime());
        // ????????????
        db.collection("record")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .whereEqualTo("calorieType", "exercise")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    myExerciseRecordList.add(document.toObject(MyRecord.class));
                }
                for (int i = 0; i < myExerciseRecordList.size(); i++) {
                    switch (myExerciseRecordList.get(i).getTime()) {
                        case "0:00":
                            totalExerciseCalorie[0] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "1:00":
                            totalExerciseCalorie[1] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "2:00":
                            totalExerciseCalorie[2] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "3:00":
                            totalExerciseCalorie[3] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "4:00":
                            totalExerciseCalorie[4] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "5:00":
                            totalExerciseCalorie[5] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "6:00":
                            totalExerciseCalorie[6] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "7:00":
                            totalExerciseCalorie[7] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "8:00":
                            totalExerciseCalorie[8] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "9:00":
                            totalExerciseCalorie[9] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "10:00":
                            totalExerciseCalorie[10] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "11:00":
                            totalExerciseCalorie[11] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "12:00":
                            totalExerciseCalorie[12] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "13:00":
                            totalExerciseCalorie[13] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "14:00":
                            totalExerciseCalorie[14] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "15:00":
                            totalExerciseCalorie[15] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "16:00":
                            totalExerciseCalorie[16] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "17:00":
                            totalExerciseCalorie[17] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "18:00":
                            totalExerciseCalorie[18] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "19:00":
                            totalExerciseCalorie[19] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "20:00":
                            totalExerciseCalorie[20] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "21:00":
                            totalExerciseCalorie[21] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "22:00":
                            totalExerciseCalorie[22] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        case "23:00":
                            totalExerciseCalorie[23] += myExerciseRecordList.get(i).getCalorie();
                            break;
                        default:
                            break;
                    }
                }
                for (int sizeIndex = 0; sizeIndex < myExerciseRecordList.size(); sizeIndex++) {
                    exerciseTotal += myExerciseRecordList.get(sizeIndex).getCalorie();
                }
                tvExerciseRecord.append(" " + (int) exerciseTotal + " ??????");
            } else {
                String message = task.getException() == null ?
                        "Not Found" :
                        task.getException().getMessage();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });

        // ????????????
        db.collection("record")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .whereEqualTo("calorieType", "eat")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    myEatRecordList.add(document.toObject(MyRecord.class));
                }
                for (int i = 0; i < myEatRecordList.size(); i++) {
                    switch (myEatRecordList.get(i).getTime()) {
                        case "0:00":
                            totalEatCalorie[0] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "1:00":
                            totalEatCalorie[1] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "2:00":
                            totalEatCalorie[2] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "3:00":
                            totalEatCalorie[3] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "4:00":
                            totalEatCalorie[4] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "5:00":
                            totalEatCalorie[5] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "6:00":
                            totalEatCalorie[6] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "7:00":
                            totalEatCalorie[7] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "8:00":
                            totalEatCalorie[8] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "9:00":
                            totalEatCalorie[9] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "10:00":
                            totalEatCalorie[10] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "11:00":
                            totalEatCalorie[11] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "12:00":
                            totalEatCalorie[12] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "13:00":
                            totalEatCalorie[13] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "14:00":
                            totalEatCalorie[14] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "15:00":
                            totalEatCalorie[15] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "16:00":
                            totalEatCalorie[16] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "17:00":
                            totalEatCalorie[17] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "18:00":
                            totalEatCalorie[18] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "19:00":
                            totalEatCalorie[19] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "20:00":
                            totalEatCalorie[20] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "21:00":
                            totalEatCalorie[21] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "22:00":
                            totalEatCalorie[22] += myEatRecordList.get(i).getCalorie();
                            break;
                        case "23:00":
                            totalEatCalorie[23] += myEatRecordList.get(i).getCalorie();
                            break;
                        default:
                            break;
                    }
            }
            for (int sizeIndex = 0; sizeIndex < myEatRecordList.size(); sizeIndex++) {
                eatTotal += myEatRecordList.get(sizeIndex).getCalorie();
            }
            tvEatRecord.append(" " + (int) eatTotal + " ??????");
        } else {
                String message = task.getException() == null ?
                        "Not Found" :
                        task.getException().getMessage();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleButton() {
        btGoSetting.setOnClickListener(v -> {
            // ??????NavController??????
            NavController navController = Navigation.findNavController(btGoSetting);
            // ????????????
            navController.navigate(R.id.action_homeDisplayBodyInfoFragment_to_fragmentMy);
        });
    }
}