package idv.example.goodposture.user.my;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import idv.example.goodposture.R;

public class MyAddRecordFragment extends Fragment {
    private ArrayAdapter adapter;
    private RadioGroup radioGroup;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_add_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleRadioGroup();
    }

    private void findViews(View view) {
        radioGroup = view.findViewById(R.id.rg_record);
        spinner = view.findViewById(R.id.sp_record_item);
    }

    private void handleRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_exerciseRecord) {
                adapter = ArrayAdapter
                        .createFromResource(getActivity(),
                                R.array.exercise_record_type,
                                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinner.setAdapter(adapter);
            }
            else if (checkedId == R.id.rb_eatRecord) {
                adapter = ArrayAdapter
                        .createFromResource(getActivity(),
                                R.array.eat_record_type,
                                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinner.setAdapter(adapter);
            }
        });

    }

}