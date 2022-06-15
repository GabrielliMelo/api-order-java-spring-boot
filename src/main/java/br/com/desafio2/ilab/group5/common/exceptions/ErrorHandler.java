package br.com.desafio2.ilab.group5.common.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class ErrorHandler implements ErrorController {

    @Autowired
    ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public DefaultErrorResponse errorHandler(WebRequest req) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(req,
                ErrorAttributeOptions.of(Include.MESSAGE, Include.BINDING_ERRORS));

        int code = (int) attributes.get("status");
        String message = (String) attributes.get("message");   
        String path = (String) attributes.get("path");

        DefaultErrorResponse error = new DefaultErrorResponse(HttpStatus.valueOf(code), message, path);

        if (attributes.containsKey("errors")) {
            error.setMessage("Erro de validação.");

            @SuppressWarnings("unchecked")
            List<FieldError> fieldErrors = (List<FieldError>) attributes.get("errors");
            ArrayList<ValidationErrors> listErrors = new ArrayList<>();

            for (FieldError fieldError : fieldErrors) {
                listErrors.add(new ValidationErrors(fieldError.getDefaultMessage(), fieldError.getField()));
            }

            error.setValidationErrors(listErrors);
        }
        
        if (error.getMessage().contains("401")) { 
        	error.setStatus("UNAUTHORIZED");
        	error.setCode(401);
        	if (error.getMessage().contains("ausente")) { 
        		error.setMessage("Token ausente.");	
        	} else { 
        		error.setMessage("Token inválido.");
        	}
        }

        return error;
    }
}
