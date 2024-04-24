package com.example.loginservice.extractor;

import java.time.LocalDate;
import java.time.Period;

public class Genarator {
    public static String changeToGenaration(LocalDate birth) {
        int age = Period.between(birth,LocalDate.now()).getYears();
        if(age < 7) {
            return "어린이";
        }else if(age <13) {
            return "초등학생";
        }else if(age <19) {
            return "청소년";
        } else if(age <22) {
            return "20대 초반";
        }else if(age <29) {
            return "20대";
        }else if(age <39) {
            return "30대";
        }else if(age <49) {
            return "40대";
        }else if(age <59) {
            return "50대";
        }else if(age <69) {
            return "60대";
        }else if(age <79) {
            return "70대";
        }else if(age <89) {
            return "80대";
        }else if(age <99) {
            return "90대";
        }else {
            return "신";
        }
    }

    public static String changeToBornIn(LocalDate birth) {
        int born = birth.getYear() % 100;
        return String.format("%02d년생",born);
    }
}
