package com.example.hipenjava.models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Courses.CourseDetailActivity;
import com.example.hipenjava.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }
// clickec on list
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        holder.courseSubtitle.setText(course.getDuration()+" giờ");

        Glide.with(context)
                .load(course.getImage())
                .placeholder(R.drawable.img_landscape)
                .into(holder.courseImage);

        // click on item
        holder.itemView.setOnClickListener(v -> {

            try {
                Intent intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("courseID", course.getId());

                intent.putExtra("name", course.getName());
                intent.putExtra("duration", course.getDuration());
                intent.putExtra("level", course.getLevel());
                intent.putExtra("lessonNum", course.getLessonNum());
                intent.putExtra("description", course.getDescription());
                intent.putExtra("image", course.getImage());
                context.startActivity(intent);

            } catch (Exception e) {
                Log.e("AdapterError", "Error opening course: " + e.getMessage());
                Toast.makeText(context, "Lỗi mở khóa học", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle, courseSubtitle;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseSubtitle = itemView.findViewById(R.id.courseSubtitle);
        }
    }
}
