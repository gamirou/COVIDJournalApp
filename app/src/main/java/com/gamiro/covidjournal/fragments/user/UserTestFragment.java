package com.gamiro.covidjournal.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.UserTest;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;

public class UserTestFragment extends Fragment {

    private static final String TAG = "UserTestFragment";

    // Layout elements
    private EditText editDateMade, editDateResult;
    private Spinner spinnerStatus;
    private Button btnDelete, btnSave;
    private ArrayAdapter spinnerAdapter;

    // View models
    private DateTimeViewModel dateViewModel;
    private HomeActivityViewModel viewModel;

    // Data received to edit the test
    private boolean isDateOrderedAdded = false;
    private UserTest test;
    private String testId;

    // Delete notifications if any
    private ArrayList<String> activeNotificationIds = new ArrayList<>();
    private boolean isNotificationActive = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UserTestFragmentArgs args = UserTestFragmentArgs.fromBundle(getArguments());
            test = args.getUserTest();
            testId = args.getUserTestId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout elements
        editDateMade = getView().findViewById(R.id.edit_user_test_date_made);
        editDateResult = getView().findViewById(R.id.edit_user_test_date_result);
        spinnerStatus = getView().findViewById(R.id.spinner_user_test_status);

        // Buttons
        btnSave = getView().findViewById(R.id.btn_user_test_save);
        btnDelete = getView().findViewById(R.id.btn_user_test_delete);

        editDateMade.setInputType(InputType.TYPE_NULL);
        editDateResult.setInputType(InputType.TYPE_NULL);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.user_test_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        dateViewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);

        dateViewModel.setSelectedDate(AppUtil.ADD_TEST_MADE);
        dateViewModel.setSelectedDate(AppUtil.ADD_TEST_RESULT);

        viewModel.getUserNotificationActions().observe(getViewLifecycleOwner(), notificationActionsMap -> {
            isNotificationActive = notificationActionsMap.containsKey(AppUtil.OPEN_USER_TEST) && testId.isEmpty();

            if (isNotificationActive) {
                activeNotificationIds = notificationActionsMap.get(AppUtil.OPEN_USER_TEST);
            }
        });

        // Add test observe dates
        editDateMade.setOnClickListener(view -> {
            if (test.getDateMade() == null) {
                if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.userTestFragment) {
                    UserTestFragmentDirections.ActionUserTestFragmentToDatePickerFragment2 action =
                            UserTestFragmentDirections.actionUserTestFragmentToDatePickerFragment2();
                    action.setDateKey(AppUtil.ADD_TEST_MADE);
                    action.setMinDate("01/01/2020");
                    action.setMaxDate("today");

                    Navigation.findNavController(requireView()).navigate(action);
                }
            } else {
                Toast.makeText(getContext(), "You cannot change the date you ordered the test. Delete the test and make a new one",
                        Toast.LENGTH_LONG).show();
            }
        });

        editDateResult.setOnClickListener(view -> {
            // If pending or date ordered selected
            if (!spinnerStatus.getSelectedItem().toString().equals("PENDING") && !editDateMade.getText().toString().isEmpty()) {
                if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.userTestFragment) {
                    UserTestFragmentDirections.ActionUserTestFragmentToDatePickerFragment2 action =
                            UserTestFragmentDirections.actionUserTestFragmentToDatePickerFragment2();
                    action.setDateKey(AppUtil.ADD_TEST_RESULT);
                    action.setMinDate(editDateMade.getText().toString());
                    action.setMaxDate("today");

                    Navigation.findNavController(requireView()).navigate(action);
                }
            } else {
                String message = "";
                if (editDateMade.getText().toString().isEmpty()) {
                    message = "Please enter the date when you ordered the test first";
                } else {
                    message = "Your test is pending, your result will come back in the future";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // If set to pending, set the date result to null
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("PENDING")) {
                    dateViewModel.setSelectedDate(AppUtil.ADD_TEST_RESULT);
                    if (test.getResult() != null) {
                        if (!testId.isEmpty() && !test.getResult().equals("PENDING")) {
                            Toast.makeText(getContext(), "You cannot change it back to pending! Did you result vanish?", Toast.LENGTH_SHORT).show();
                            spinnerStatus.setSelection(spinnerAdapter.getPosition(test.getResult()));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: true");
            }
        });

        // Set edit texts
        dateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), stringStringHashMap -> {
            if (stringStringHashMap.containsKey(AppUtil.ADD_TEST_MADE)) {
                editDateMade.setText(stringStringHashMap.get(AppUtil.ADD_TEST_MADE));
            }

            if (stringStringHashMap.containsKey(AppUtil.ADD_TEST_RESULT)) {
                editDateResult.setText(stringStringHashMap.get(AppUtil.ADD_TEST_RESULT));
            }
        });

        // Set the data from safe args
        if (test.getDateMade() != null) {
            if (!test.getDateMade().isEmpty()) {
                Log.d(TAG, "!test.getDateMade().isEmpty():success");
                dateViewModel.setSelectedDate(AppUtil.ADD_TEST_MADE, test.getDateMade());
                dateViewModel.setSelectedDate(AppUtil.ADD_TEST_RESULT, test.getDateResult());

                spinnerStatus.setSelection(spinnerAdapter.getPosition(test.getResult()));

                isDateOrderedAdded = true;
            }
        } else {
            btnSave.setText("Add");
            btnDelete.setVisibility(View.GONE);
        }

        // Add or save tests
        btnSave.setOnClickListener(view -> {
            // Add test
            String dateMade = editDateMade.getText().toString();
            String dateResult = editDateResult.getText().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            boolean might = !status.equals("NEGATIVE");
            boolean has = status.equals("POSITIVE");

            boolean testAdded = testId.isEmpty() || (!dateResult.equals(test.getDateResult()) || !status.equals(test.getResult()));

            if (testAdded) {
                UserTest newTest = new UserTest(dateMade, status, dateResult);
                viewModel.addUserTest(newTest, testId);
                viewModel.setUserCoronaState(might, has, newTest);
                getActivity().onBackPressed();

                for (String notificationId: activeNotificationIds) {
                    viewModel.deleteNotification(notificationId);
                }
            } else {
                Toast.makeText(getContext(), "No changes", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete test
        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Delete the test");
            builder.setMessage("Are you absolutely sure? This cannot be reversed!");
            builder.setPositiveButton("Yes", (dialog, id) -> {
                viewModel.deleteUserTest(testId, test.getResult());
                getActivity().onBackPressed();
            });

            builder.setNegativeButton("No", (dialog, id) -> {});
            builder.show();
        });
    }
}