package com.example.hipenjava.Activities.Challenge;

public class Challenge {
    private String id;
    private String name;
    private String description;
    private String timeStart;
    private String timeEnd;
    private String imageUrl;

    public Challenge(String id, String name, String description, String timeStart, String timeEnd, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getStatus() {
        long currentTime = System.currentTimeMillis();
        long start = Long.parseLong(timeStart);
        long end = Long.parseLong(timeEnd);

        if (currentTime < start) {
            long timeUntilStart = start - currentTime;
            long days = timeUntilStart / (1000 * 60 * 60 * 24);
            long hours = (timeUntilStart / (1000 * 60 * 60)) % 24;

            if (days > 0) {
                return days + " ngày nữa";
            } else if (hours > 0) {
                return hours + " giờ nữa";
            } else {
                return "Sẽ mở trong 1 giờ nữa";
            }

        } else if (currentTime > end) {
            return "Đã kết thúc";

        } else {
            long timeLeftMillis = end - currentTime;
            long days = timeLeftMillis / (1000 * 60 * 60 * 24);
            long hours = (timeLeftMillis / (1000 * 60 * 60)) % 24;

            if (days > 0) {
                return "Còn " + days + " ngày";
            } else if (hours > 0) {
                return "Còn " + hours + " giờ";
            } else {
                return "Còn dưới 1 tiếng";
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public long getTimeEndInLong(){
        return Long.parseLong(timeEnd);
    }
}
