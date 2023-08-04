package com.example.temperature1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//초단기실황
@Controller
public class inputtemCon {

    private String top,outer,bottom;
    @GetMapping("/inputtem")

    @PostMapping("/inputtem")
    public String processTemperature(
            @RequestParam("temperatureValue") String temperature,
            @RequestParam("precipitationValue") String precipitation,
            Model model
    ) {

        // 사용자가 입력한 온도와 강수량 값을 받아서 처리
        int temperatureValue = Integer.parseInt(temperature);

        // 강수량 값에 대한 처리 (추가적인 로직이 필요할 수 있습니다.)
        // ...
        if (temperatureValue >= 28) {

            model.addAttribute("top", "반팔");
            model.addAttribute("outer", "-");
            model.addAttribute("bottom", "반바지, 얇은 나일론 소재의 긴바지");
        } else if (temperatureValue >= 23) {
            model.addAttribute("top", "반팔");
            model.addAttribute("outer", "가디건");
            model.addAttribute("bottom", "반바지, 얇은 나일론 소재의 긴바지");
        } else if (temperatureValue >= 20) {
            model.addAttribute("top", "긴팔 셔츠,반팔 위 긴 셔츠나, 반팔과 핸드워머");
            model.addAttribute("outer", "-");
            model.addAttribute("bottom", "긴바지");
        } else if (temperatureValue >= 17) {
            model.addAttribute("top", "긴팔");
            model.addAttribute("outer", "0온스 학잠, 반팔 위 겉옷 ");
            model.addAttribute("bottom", "긴바지");
        } else if (temperatureValue >= 12) {
            model.addAttribute("top", "니트,블레이저");
            model.addAttribute("outer", "2온스 학잠, 가디건, 후드집업");
            model.addAttribute("bottom", "-");
        } else if (temperatureValue >= 9) {
            model.addAttribute("top", "보온을 위한 목티, 히트텍 ,기모 후드티");
            model.addAttribute("outer", "기모 후드집업");
            model.addAttribute("bottom", "-");
        } else if (temperatureValue >= 5) {
            model.addAttribute("top", "보온을 위한 목티, 히트텍");
            model.addAttribute("outer", "코트, 가죽자켓, 플리스");
            model.addAttribute("bottom", "반바지, 얇은 나일론 소재의 긴바지");
        } else {
            model.addAttribute("top", "보온을 위한 목티, 히트텍");
            model.addAttribute("outer", "패딩, 코트, 목도리");
            model.addAttribute("bottom", "-");
        }
        model.addAttribute("tem", temperatureValue);


        System.out.println(top);
        return "inputtem";
    }
}