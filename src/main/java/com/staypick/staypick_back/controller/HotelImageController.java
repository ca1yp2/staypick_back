package com.staypick.staypick_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staypick.staypick_back.service.HotelImageService;

@RestController
@RequestMapping("/api/hotels")
public class HotelImageController {

    @Autowired
    private HotelImageService hotelImageService;

    @GetMapping("/{hotelName}/images")
    public List<String> getHotelImages(@PathVariable String hotelName){
        return hotelImageService.getHotelImages(hotelName);
    }
}
