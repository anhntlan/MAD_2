package com.example.hipenjava.Activities.Challenge;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.R;

import java.util.ArrayList;
import java.util.List;

public class ChallengeListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private List<Challenge> challengeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_list);

        recyclerView = findViewById(R.id.recyclerViewChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        challengeList = new ArrayList<>();
        challengeList.add(new Challenge("Vẽ bầu trời", "Còn 3 ngày", R.drawable.img_sky));
        challengeList.add(new Challenge("Chủ đề động vật", "Đã kết thúc", R.drawable.img_animals));

        adapter = new ChallengeAdapter(challengeList, this);
        recyclerView.setAdapter(adapter);
    }
}
