/*
package com.example.hipenjava.Utils;

import com.example.hipenjava.Models.ArtCategory;
import com.example.hipenjava.Models.ArtExample;
import com.example.hipenjava.Models.ArtItem;
import com.example.hipenjava.Models.CategoryType;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Initialize Firebase with sample data
    public static void initializeFirebaseData() {
        // Initialize category types
        initializeCategoryTypes();
        
        // Initialize art schools
        initializeArtSchools();
        
        // Initialize art periods
        initializeArtPeriods();
        
        // Initialize art types
        initializeArtTypes();
        
        // Initialize art examples
        initializeArtExamples();
    }

    private static void initializeCategoryTypes() {
        databaseReference.child("categoryTypes").child("schools")
                .setValue(new CategoryType("schools", "Trường phái nghệ thuật", "Art Schools"));
        
        databaseReference.child("categoryTypes").child("periods")
                .setValue(new CategoryType("periods", "Thời kỳ nghệ thuật", "Art Periods"));
        
        databaseReference.child("categoryTypes").child("types")
                .setValue(new CategoryType("types", "Loại hình nghệ thuật", "Art Types"));
    }

    private static void initializeArtSchools() {
        // Realism
        List<ArtItem> realismItems = new ArrayList<>();
        realismItems.add(new ArtItem(
                "realism1",
                "Hiện thực",
                "Realism",
                "Chủ nghĩa Hiện thực là trào lưu nghệ thuật và văn học tập trung miêu tả cuộc sống một cách khách quan, trung thực, không lý tưởng hóa hay cường điệu.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/realism.jpg?alt=media",
                "schools_realism",
                "schools"
        ));
        
        // Abstract
        List<ArtItem> abstractItems = new ArrayList<>();
        abstractItems.add(new ArtItem(
                "abstract1",
                "Ảo tưởng",
                "Abstract",
                "Nghệ thuật Ảo tưởng là phong cách nghệ thuật không cố gắng tái hiện thực tế một cách chính xác mà sử dụng hình dạng, màu sắc, hình thức và đường nét để tạo ra tác phẩm có thể tồn tại độc lập với tham chiếu trực quan đến thế giới.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/abstract.jpg?alt=media",
                "schools_abstract",
                "schools"
        ));
        
        // Surrealism
        List<ArtItem> surrealismItems = new ArrayList<>();
        surrealismItems.add(new ArtItem(
                "surrealism1",
                "Siêu thực",
                "Surrealism",
                "Chủ nghĩa Siêu thực là phong trào văn hóa bắt đầu vào những năm 1920, nổi tiếng với các tác phẩm nghệ thuật và văn học mang tính huyền ảo, bất ngờ và phi lý trí, thường kết hợp các yếu tố từ thế giới mơ và thực tại.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/surrealism.jpg?alt=media",
                "schools_surrealism",
                "schools"
        ));
        
        // Add to Firebase
        databaseReference.child("artSchools").child("realism")
                .setValue(new ArtCategory(
                        "schools_realism",
                        "Hiện thực",
                        "Realism",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/realism_thumb.jpg?alt=media",
                        realismItems
                ));
        
        databaseReference.child("artSchools").child("abstract")
                .setValue(new ArtCategory(
                        "schools_abstract",
                        "Ảo tưởng",
                        "Abstract",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/abstract_thumb.jpg?alt=media",
                        abstractItems
                ));
        
        databaseReference.child("artSchools").child("surrealism")
                .setValue(new ArtCategory(
                        "schools_surrealism",
                        "Siêu thực",
                        "Surrealism",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/surrealism_thumb.jpg?alt=media",
                        surrealismItems
                ));
    }

    private static void initializeArtPeriods() {
        // Classical Art
        List<ArtItem> classicalItems = new ArrayList<>();
        classicalItems.add(new ArtItem(
                "classical1",
                "Cổ điển",
                "Classical Art",
                "Nghệ thuật Cổ điển đề cập đến nghệ thuật của các nền văn minh cổ đại, đặc biệt là Hy Lạp và La Mã, và sau đó là các phong trào nghệ thuật lấy cảm hứng từ chúng.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/classical.jpg?alt=media",
                "periods_classical",
                "periods"
        ));
        
        // Medieval Art
        List<ArtItem> medievalItems = new ArrayList<>();
        medievalItems.add(new ArtItem(
                "medieval1",
                "Trung đại",
                "Medieval Art",
                "Nghệ thuật Trung đại bao gồm nhiều phong cách nghệ thuật từ thời kỳ sau sự sụp đổ của Đế chế La Mã cho đến thời kỳ Phục hưng, được đặc trưng bởi các chủ đề tôn giáo và biểu tượng.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/medieval.jpg?alt=media",
                "periods_medieval",
                "periods"
        ));
        
        // Renaissance Art
        List<ArtItem> renaissanceItems = new ArrayList<>();
        renaissanceItems.add(new ArtItem(
                "renaissance1",
                "Phục Hưng",
                "Renaissance Art",
                "Nghệ thuật Phục hưng là một phong trào văn hóa bắt đầu ở Ý vào thế kỷ 14 và kéo dài đến thế kỷ 17, đánh dấu sự chuyển đổi từ Trung cổ sang Thời kỳ hiện đại. Nó được đặc trưng bởi sự quan tâm đến nghệ thuật cổ điển và chủ nghĩa nhân văn.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/renaissance.jpg?alt=media",
                "periods_renaissance",
                "periods"
        ));
        
        // Add to Firebase
        databaseReference.child("artPeriods").child("classical")
                .setValue(new ArtCategory(
                        "periods_classical",
                        "Cổ điển",
                        "Classical Art",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/classical_thumb.jpg?alt=media",
                        classicalItems
                ));
        
        databaseReference.child("artPeriods").child("medieval")
                .setValue(new ArtCategory(
                        "periods_medieval",
                        "Trung đại",
                        "Medieval Art",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/medieval_thumb.jpg?alt=media",
                        medievalItems
                ));
        
        databaseReference.child("artPeriods").child("renaissance")
                .setValue(new ArtCategory(
                        "periods_renaissance",
                        "Phục Hưng",
                        "Renaissance Art",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/renaissance_thumb.jpg?alt=media",
                        renaissanceItems
                ));
    }

    private static void initializeArtTypes() {
        // Painting
        List<ArtItem> paintingItems = new ArrayList<>();
        paintingItems.add(new ArtItem(
                "painting1",
                "Hội họa",
                "Painting",
                "Hội họa là nghệ thuật tạo ra hình ảnh bằng cách sử dụng màu sắc, sắc tố, hoặc các chất khác lên một bề mặt như giấy, vải, hoặc tường.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/painting.jpg?alt=media",
                "types_painting",
                "types"
        ));
        
        // Sculpture
        List<ArtItem> sculptureItems = new ArrayList<>();
        sculptureItems.add(new ArtItem(
                "sculpture1",
                "Điêu khắc",
                "Sculpture",
                "Điêu khắc là nghệ thuật tạo ra các tác phẩm ba chiều bằng cách tạo hình, đúc, đẽo, hoặc kết hợp các vật liệu.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/sculpture.jpg?alt=media",
                "types_sculpture",
                "types"
        ));
        
        // Calligraphy
        List<ArtItem> calligraphyItems = new ArrayList<>();
        calligraphyItems.add(new ArtItem(
                "calligraphy1",
                "Tranh thư pháp",
                "Calligraphy",
                "Thư pháp là nghệ thuật viết chữ đẹp, thường được thực hiện bằng bút lông hoặc bút mực đặc biệt, tạo ra các tác phẩm nghệ thuật từ chữ viết.",
                "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/calligraphy.jpg?alt=media",
                "types_calligraphy",
                "types"
        ));
        
        // Add to Firebase
        databaseReference.child("artTypes").child("painting")
                .setValue(new ArtCategory(
                        "types_painting",
                        "Hội họa",
                        "Painting",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/painting_thumb.jpg?alt=media",
                        paintingItems
                ));
        
        databaseReference.child("artTypes").child("sculpture")
                .setValue(new ArtCategory(
                        "types_sculpture",
                        "Điêu khắc",
                        "Sculpture",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/sculpture_thumb.jpg?alt=media",
                        sculptureItems
                ));
        
        databaseReference.child("artTypes").child("calligraphy")
                .setValue(new ArtCategory(
                        "types_calligraphy",
                        "Tranh thư pháp",
                        "Calligraphy",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/calligraphy_thumb.jpg?alt=media",
                        calligraphyItems
                ));
    }

    private static void initializeArtExamples() {
        // Realism examples
        databaseReference.child("artExamples").child("realism1")
                .setValue(new ArtExample(
                        "realism_ex1",
                        "Cụ già chơi đàn guitar",
                        "Picasso",
                        "1903-1904",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/realism_example1.jpg?alt=media",
                        "schools_realism"
                ));
        
        databaseReference.child("artExamples").child("realism2")
                .setValue(new ArtExample(
                        "realism_ex2",
                        "Những cô gái hái lúa",
                        "Jean-François Millet",
                        "1857",
                        "https://firebasestorage.googleapis.com/v0/b/art-gallery-app.appspot.com/o/realism_example2.jpg?alt=media",
                        "schools_realism"
                ));
        
        // Add more examples for other categories as needed
    }
}
*/
