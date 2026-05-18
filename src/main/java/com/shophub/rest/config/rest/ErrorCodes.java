package com.shophub.rest.config.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCodes {
    UNAWARE_ERR("Unaware exc thrown!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN("JWT is invalid!", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_USER("User accessing denied!", HttpStatus.FORBIDDEN),
    USER_NOTFOUND("User not found!", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("Email or password is incorrected", HttpStatus.BAD_REQUEST),
    INVALID_ID("Invalid id on entity ${}", HttpStatus.BAD_REQUEST),
    HIBERNATE_VALIDATION_ERR_LOG("Hibernate Validation failed for code=%, field=%, value=%", HttpStatus.BAD_REQUEST),
    DUPLICATE_UPSERT("Updating action is duplicated with another one, please try again!", HttpStatus.BAD_REQUEST),
    REMOVING_STRICT_DATA("Cannot remove strict data (used by another data)!", HttpStatus.BAD_REQUEST),
    INVALID_IDS("Some ids from collection are invalid", HttpStatus.BAD_REQUEST),
    NEGATIVE_QTY("Negative quantity appeared, please check again!", HttpStatus.BAD_REQUEST),
    BUSY_ORDERING_SVC("Order has been terminated cause of busy services, please try again!", HttpStatus.BAD_REQUEST),
    ADMIN_NOT_FOUND("Admin application not found", HttpStatus.BAD_REQUEST),
    ORDER_CANCELING_NOT_ORDERED_STS("Just the Order with ORDERED status can be Canceled", HttpStatus.BAD_REQUEST),
    ORDER_DELIVERY_NOT_PREPARING_STS("Just the Order with PREPARING status can be Delivered", HttpStatus.BAD_REQUEST),
    ORDER_CLOSE_NOT_DELIVERY_STS("Just the Order with IN_DELIVERY status can be Closed", HttpStatus.BAD_REQUEST),
    ;
    String msg;
    HttpStatus httpStatus;
}
