package com.staypick.staypick_back.service;

import com.staypick.staypick_back.entity.Accommodation;
import com.staypick.staypick_back.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class HotelImageService {
    private static final String UPLOAD_DIR = "src/main/resources/static/upload/hotels/"; // 실제 경로에 맞게 수정

    @Autowired
    private AccommodationRepository accommodationRepository;

    public List<String> getHotelImages(String hotelName) {
        Optional<Accommodation> accommodation = accommodationRepository.findByName(hotelName);
        if (accommodation.isEmpty()) {
            return new ArrayList<>(); // 호텔이 없으면 빈 목록 반환
        }

        File directory = new File(UPLOAD_DIR);
        File[] files = directory.listFiles();
        List<String> imageFiles = new ArrayList<>();

        if (files != null) {
            String regex = "^" + Pattern.quote(hotelName) + "_.*";
            for (File file : files) {
                if (file.isFile() && file.getName().matches(regex)) {
                    imageFiles.add("/upload/hotels/" + file.getName()); // 프론트에서 접근 가능한 경로
                }
            }
        }
        return imageFiles;
    }
}
