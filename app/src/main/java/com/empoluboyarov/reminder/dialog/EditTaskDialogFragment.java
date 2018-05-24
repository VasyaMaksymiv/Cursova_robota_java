package com.empoluboyarov.reminder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.empoluboyarov.reminder.R;
import com.empoluboyarov.reminder.Utils;
import com.empoluboyarov.reminder.alarm.AlarmHelper;
import com.empoluboyarov.reminder.model.ModelTask;

import java.util.Calendar;

public class EditTaskDialogFragment extends DialogFragment {

    public static EditTaskDialogFragment newInstance(ModelTask task) {
        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", task.getTitle());
        args.putLong("date", task.getDate());
        args.putInt("priority", task.getPriority());
        args.putLong("time_stamp", task.getTimeStamp());

        editTaskDialogFragment.setArguments(args);
        return editTaskDialogFragment;
    }

    private EditTaskListener editTaskListener;

    public interface EditTaskListener {
        void onTaskEdited(ModelTask updateTask);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editTaskListener = (EditTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement EditTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString("title");
        long date = args.getLong("date", 0);
        int priority = args.getInt("priority", 0);
        long timeStamp = args.getLong("time_stamp", 0);

        final ModelTask task = new ModelTask(title, date, priority, 0, timeStamp);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_editing_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View container = inflater.inflate(R.layout.dialog_task, null);

        final TextInputLayout titTitle = (TextInputLayout) container.findViewById(R.id.dialogTaskTitle);
        TextInputLayout titDate = (TextInputLayout) container.findViewById(R.id.dialogTaskDate);
        TextInputLayout titTime = (TextInputLayout) container.findViewById(R.id.dialogTaskTime);

        final EditText etTitle = titTitle.getEditText();
        final EditText etDate = titDate.getEditText();
        final EditText etTime = titTime.getEditText();

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        etTitle.setText(task.getTitle());
        etTitle.setSelection(etTitle.length());
        if (task.getDate() != 0){
            etDate.setText(Utils.getData(task.getDate()));
            etTime.setText(Utils.getTime(task.getDate()));
        }

        titTitle.setHint(getResources().getString(R.string.task_title));
        titDate.setHint(getResources().getString(R.string.task_date));
        titTime.setHint(getResources().getString(R.string.task_time));

        builder.setView(container);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.priority_levels));

        spPriority.setAdapter(priorityAdapter);
        spPriority.setSelection(task.getPriority());

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        if (etDate.length() != 0 || etTime.length() != 0){
            calendar.setTimeInMillis(task.getDate());
        }


        assert etDate != null;
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0)
                    etDate.setText(" ");

                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getData(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        assert etTime != null;
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length() == 0)
                    etTime.setText(" ");
                DialogFragment timePickerFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                task.setTitle(etTitle.getText().toString());
                task.setStatus(ModelTask.STATUS_CURRENT);

                if (etDate.length() != 0 || etTime.length() != 0) {
                    task.setDate(calendar.getTimeInMillis());
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                editTaskListener.onTaskEdited(task);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etTitle.length() == 0) {
                    positiveButton.setEnabled(false);
                    titTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                }
                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                            titTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                        } else {
                            positiveButton.setEnabled(true);
                            titTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });

        return alertDialog;
    }
}

