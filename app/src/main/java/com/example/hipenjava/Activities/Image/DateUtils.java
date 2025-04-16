package com.example.hipenjava.Activities.Image;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
public class DateUtils {


    // Phương thức lấy thời gian tương đối từ Timestamp
    public static String getRelativeTime(Timestamp timestamp) {
        // Chuyển Timestamp thành Date
        Date date = timestamp.toDate();

        // Lấy thời gian hiện tại
        Date now = Calendar.getInstance().getTime();

        // Tính sự khác biệt thời gian
        long diff = now.getTime() - date.getTime();

        // Tính số ngày chênh lệch
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        // Trả về các kết quả tương ứng
        if (days == 0) {
            return "Hôm nay"; // Nếu là hôm nay
        }
        if (days == 1) {
            return "1 ngày trước"; // Nếu là 1 ngày trước
        }
        return days + " ngày trước"; // Nếu là nhiều ngày trước
    }



}
