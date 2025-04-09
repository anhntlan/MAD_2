package com.example.hipenjava.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.Courses.LessonDetailActivity;
import com.example.hipenjava.R;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    private List<Lesson> lessons;
    private Context context;

    public LessonAdapter(Context context, List<Lesson> lessons) {
        this.context = context;
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.name.setText(lesson.getName());
        holder.duration.setText(lesson.getDuration() + " giờ");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonDetailActivity.class);
            intent.putExtra("lessonID", lesson.getId());
            intent.putExtra("lessonName", lesson.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView name, duration;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.lesson_name);
            duration = itemView.findViewById(R.id.lesson_duration);
        }
    }
}
