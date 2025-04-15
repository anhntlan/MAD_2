package com.example.hipenjava.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Courses.CourseLearningActivity;
import com.example.hipenjava.R;

import java.util.List;

public class EnrolledCourseAdapter extends RecyclerView.Adapter<EnrolledCourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    public EnrolledCourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseTitle.setText(course.getName());
        holder.courseDescription.setText(course.getDescription());

        if (course.getImage() != null && !course.getImage().isEmpty()) {
            Glide.with(context)
                    .load(course.getImage())
                    .placeholder(R.drawable.img_landscape)
                    .into(holder.courseImage);
        } else {
            holder.courseImage.setImageResource(R.drawable.img_landscape);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseLearningActivity.class);
            intent.putExtra("courseID", course.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle, courseDescription;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseSubtitle);
        }
    }
}