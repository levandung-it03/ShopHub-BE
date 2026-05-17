package com.shophub.rest.util;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.ArrayList;

public class UString {

    public static String castNullableString(@Nullable Object object) {
        return object == null ? "null" : object.toString();
    }

    @Getter
    public static class SJoin {
        private final StringBuilder s;
        // Init
        public SJoin(String s) {
            this.s = new StringBuilder(s);
        }
        // Beginning
        public static SJoin on(String s) {
            return new SJoin(s);
        }
        // Next str
        public SJoin n(String s) {
            this.s.append(s);
            return this;
        }
        // Aggregate
        public String ok() {
            return this.s.toString();
        }
    }

    @Getter
    public static class SFormat {
        public static final Character SYMBOL = '%';

        private final Character symbol;
        private String pattern;
        private final ArrayList<String> params;
        // Init
        public SFormat(Character symbol) {
            this.symbol = symbol;
            this.params = new ArrayList<>();
        }
        public static SFormat by(Character symbol) {
            return new SFormat(symbol);
        }

        public SFormat pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public static SFormat defaultPattern(String pattern) {
            SFormat formatter = new SFormat(SYMBOL);
            formatter.pattern = pattern;
            return formatter;
        }

        public SFormat param(String param) {
            this.params.add(param);
            return this;
        }

        public String ok() {
            int pivot = 0, paramIdx = 0;
            while (pivot != -1) {
                pivot = this.pattern.indexOf(this.symbol, pivot + 1);
                if (pivot != -1) {
                    var left = this.pattern.substring(0, pivot);
                    var right = this.pattern.substring(pivot + 1);
                    this.pattern = left + this.params.get(paramIdx++) + right;
                }
            }
            return this.pattern;
        }
    }
}
