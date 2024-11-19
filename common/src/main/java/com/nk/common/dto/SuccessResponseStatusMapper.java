package com.nk.common.dto;

import com.nk.base.dto.ResponseStatus;
import org.springframework.http.HttpStatus;

public class SuccessResponseStatusMapper {

    // Method to map only success HTTP statuses to ErrorResponse
    public static ResponseStatus mapToSuccessResponse(HttpStatus httpStatus) {
        switch (httpStatus) {
            case OK:
                return new ResponseStatus("SUCCESS", "Request processed successfully");

            case CREATED:
                return new ResponseStatus("SUCCESS", "Resource created successfully");

            case ACCEPTED:
                return new ResponseStatus("SUCCESS", "Request accepted");

            case NO_CONTENT:
                return new ResponseStatus("SUCCESS", "Request processed with no content");

            default:
                return new ResponseStatus("SUCCESS", "Operation successful");
        }
    }
}
