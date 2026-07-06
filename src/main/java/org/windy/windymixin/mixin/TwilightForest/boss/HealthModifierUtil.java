package org.windy.windymixin.mixin.TwilightForest.boss;


public class HealthModifierUtil {
    // 通用翻倍方法，可扩展倍数
    public static double doubleHealth(double original) {
        return original * 2;
    }
}