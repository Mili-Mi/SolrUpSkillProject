package tv.cadent.ctaas.dis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;
import tv.cadent.ctaas.dis.common.model.StatusResponse;


@ControllerAdvice
@Log4j2
public class GlobalExceptionHandlingController {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<StatusResponse> handleGlobalException(Exception e) {
		StatusResponse response = new StatusResponse();
		response.addErrorMessage(e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
