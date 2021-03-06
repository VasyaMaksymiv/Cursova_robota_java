package com.empoluboyarov.reminder.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.empoluboyarov.reminder.fragment.TaskFragment;
import com.empoluboyarov.reminder.model.Item;
import com.empoluboyarov.reminder.model.ModelSeparator;
import com.empoluboyarov.reminder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TASK = 0;
    public static final int TYPE_SEPARATOR = 1;

    List<Item> items = new ArrayList<>();
    TaskFragment taskFragment;

    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;

    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void updateTask(ModelTask newTask){
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()){
                ModelTask task  = (ModelTask) getItem(i);
                if (newTask.getTimeStamp() == task.getTimeStamp()){
                    remoteItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }

    public void remoteItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);
            if (location - 1 >= 0 && location <= getItemCount() - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    ModelSeparator separator = (ModelSeparator) getItem(location - 1);
                    checkSeparators(separator.getType());
                    items.remove(location - 1);
                    notifyItemRemoved(location - 1);
                }
            } else if (getItemCount() -1 >= 0 && !getItem(getItemCount()-1).isTask()){
                ModelSeparator separator = (ModelSeparator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());

                int locationTemp = getItemCount() -1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    public void checkSeparators(int type){
        switch (type){
            case ModelSeparator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case ModelSeparator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView date;
        protected CircleImageView priority;

        public TaskViewHolder(View itemView, TextView title, TextView date, CircleImageView priority) {
            super(itemView);
            this.title = title;
            this.date = date;
            this.priority = priority;
        }
    }

    public TaskFragment getTaskFragment() {
        return taskFragment;
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorToday = false;
            containsSeparatorOverdue = false;
            containsSeparatorTomorrow = false;
            containsSeparatorFuture = false;
        }
    }

    protected class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;

        public SeparatorViewHolder(View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else
            return TYPE_SEPARATOR;
    }
}
