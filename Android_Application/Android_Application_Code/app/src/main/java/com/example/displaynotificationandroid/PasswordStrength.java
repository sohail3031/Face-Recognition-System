package com.example.displaynotificationandroid;
//
//import android.graphics.Color;
//
//public enum PasswordStrength {
//    WEAK(R.string.weak, Color.parseColor("#61ad85")),
//    MEDIUM(R.string.medium, Color.parseColor("#4d8a6a")),
//    STRONG(R.string.strong, Color.parseColor("#3a674f")),
//    VERY_STRONG(R.string.very_strong, Color.parseColor("#264535"));
//
//    public int msg;
//    public int color;
//    private static int MIN_LENGTH = 8;
//    private static int MAX_LENGTH = 15;
//
//    PasswordStrength(int msg, int color) {
//        this.msg = msg;
//        this.color = color;
//    }
//
//    public static PasswordStrength calculate(String password) {
//        int score = 0;
//        // boolean indicating if password has an upper case
//        boolean upper = false;
//        // boolean indicating if password has a lower case
//        boolean lower = false;
//        // boolean indicating if password has at least one digit
//        boolean digit = false;
//        // boolean indicating if password has a leat one special char
//        boolean specialChar = false;
//
//        for (int i = 0; i < password.length(); i++) {
//            char c = password.charAt(i);
//
//            if (!specialChar  &&  !Character.isLetterOrDigit(c)) {
//                score++;
//                specialChar = true;
//            } else {
//                if (!digit  &&  Character.isDigit(c)) {
//                    score++;
//                    digit = true;
//                } else {
//                    if (!upper || !lower) {
//                        if (Character.isUpperCase(c)) {
//                            upper = true;
//                        } else {
//                            lower = true;
//                        }
//
//                        if (upper && lower) {
//                            score++;
//                        }
//                    }
//                }
//            }
//        }
//
//        int length = password.length();
//
//        if (length > MAX_LENGTH) {
//            score++;
//        } else if (length < MIN_LENGTH) {
//            score = 0;
//        }
//
//        // return enum following the score
//        switch(score) {
//            case 0 : return WEAK;
//            case 1 : return MEDIUM;
//            case 2 : return STRONG;
//            case 3 : return VERY_STRONG;
//            default:
//        }
//
//        return VERY_STRONG;
//    }
//}

import android.graphics.Color;
//import com.ybs.passwordstrengthchecker.R;

public enum PasswordStrength {

    WEAK(R.string.password_strength_weak, Color.RED),
    MEDIUM(R.string.password_strength_medium, Color.argb(255, 220, 185, 0)),
    STRONG(R.string.password_strength_strong, Color.GREEN),
    VERY_STRONG(R.string.password_strength_very_strong, Color.BLUE);

    //--------REQUIREMENTS--------
    static int REQUIRED_LENGTH = 8;
    static int MAXIMUM_LENGTH = 15;
    static boolean REQUIRE_SPECIAL_CHARACTERS = true;
    static boolean REQUIRE_DIGITS = true;
    static boolean REQUIRE_LOWER_CASE = true;
    static boolean REQUIRE_UPPER_CASE = false;

    int resId;
    int color;

    PasswordStrength(int resId, int color) {
        this.resId = resId;
        this.color = color;
    }

    public CharSequence getText(android.content.Context ctx) {
        return ctx.getText(resId);
    }

    public int getColor() {
        return color;
    }

    public static PasswordStrength calculateStrength(String password) {
        int currentScore = 0;
        boolean sawUpper = false;
        boolean sawLower = false;
        boolean sawDigit = false;
        boolean sawSpecial = false;


        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                currentScore += 1;
                sawSpecial = true;
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1;
                    sawDigit = true;
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(c))
                            sawUpper = true;
                        else
                            sawLower = true;
                        if (sawUpper && sawLower)
                            currentScore += 1;
                    }
                }
            }

        }

        if (password.length() > REQUIRED_LENGTH) {
            if ((REQUIRE_SPECIAL_CHARACTERS && !sawSpecial)
                    || (REQUIRE_UPPER_CASE && !sawUpper)
                    || (REQUIRE_LOWER_CASE && !sawLower)
                    || (REQUIRE_DIGITS && !sawDigit)) {
                currentScore = 1;
            }else{
                currentScore = 2;
                if (password.length() > MAXIMUM_LENGTH) {
                    currentScore = 3;
                }
            }
        }else{
            currentScore = 0;
        }

        switch (currentScore) {
            case 0:
                return WEAK;
            case 1:
                return MEDIUM;
            case 2:
                return STRONG;
            case 3:
                return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }

}
