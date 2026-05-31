package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.exception.InvalidCredentialException;
import com.example.gardenassistant.exception.UserNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof UserNotFoundException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.NOT_FOUND)
                    .build();
        }

        if (ex instanceof InvalidCredentialException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.UNAUTHORIZED)
                    .build();
        }

        if (ex instanceof AccessDeniedException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("Access denied")
                    .errorType(ErrorType.FORBIDDEN)
                    .build();
        }

        if (ex instanceof BindException bindException) {

            String message = bindException.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .findFirst()
                    .orElse("Validation error");

            return GraphqlErrorBuilder.newError(env)
                    .message(message)
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getClass().getSimpleName() + ": " + ex.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
