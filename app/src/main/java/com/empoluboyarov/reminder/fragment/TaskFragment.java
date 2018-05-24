package com.empoluboyarov.reminder.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.empoluboyarov.reminder.MainActivity;
import com.empoluboyarov.reminder.R;
import com.empoluboyarov.reminder.adapter.TaskAdapter;
import com.empoluboyarov.reminder.alarm.AlarmHelper;
import com.empoluboyarov.reminder.dialog.EditTaskDialogFragment;
import com.empoluboyarov.reminder.model.Item;
import com.empoluboyarov.reminder.model.ModelTask;

public abstract class TaskFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;

    public MainActivity activity;
    public AlarmHelper alarmHelper;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null)
            activity = (MainActivity) getActivity();
        alarmHelper = AlarmHelper.getInstance();
        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);


    public void updateTask(ModelTask task) {
        adapter.updateTask(task);
    }

    public void removeTaskDialog(final int location) {
        Item item = adapter.getItem(location);
        final long timeStamp;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(R.string.dialog_removing_message);
        if (item.isTask()) {
            ModelTask removingTask = (ModelTask) item;
            timeStamp = ((ModelTask) item).getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    adapter.remoteItem(location);
                    isRemoved[0] = true;

                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });

                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if (isRemoved[0]) {
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removeTask(timeStamp);
                            }
                        }
                    });
                    snackbar.show();

                    dialog.dismiss();
                }
            });

            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        dialogBuilder.show();
    }

    public void showTaskEditDialog(ModelTask task) {
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivity().getFragmentManager(), "EditTaskDialogFragment");
    }

    public abstract void moveTask(ModelTask task);


    public abstract void addTaskFromDB();

    public abstract void findTask(String title);


}
