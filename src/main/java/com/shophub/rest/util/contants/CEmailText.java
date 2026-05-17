package com.shophub.rest.util.contants;

public interface CEmailText {

    interface Order {
        static String CREATION_TITLE(String creatorEmail) {
            return "Order has been created by " + creatorEmail;
        }

        static String CREATION_HTML() {
            return String.format("""

            """);
        }
    }
}
