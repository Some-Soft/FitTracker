package com.fittracker.fittracker.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorResponseMapperTest {

    @InjectMocks
    private ErrorResponseMapper errorResponseMapper;

    private final LocalDate TEST_DATE = LocalDate.of(2023,11,10);


    BindingResult bindingResult;
    FieldError fieldError;
    MethodParameter methodParameter;
    HttpMessageNotReadableException httpMessageNotReadableException;

    @BeforeEach
    void setUp() {
        bindingResult = mock(BindingResult.class);
        fieldError = mock(FieldError.class);
        methodParameter = mock(MethodParameter.class);
        httpMessageNotReadableException = mock(HttpMessageNotReadableException.class);
    }

    @Test
    void givenWeightNotFoundException_shouldReturnErrorResponse() {
        throwAndHandleException(WeightNotFoundException.class, TEST_DATE, LocalDate.class, e -> {
            var expectedErrorResponse = new ErrorResponse(null, "Weight not found for date: 2023-11-10");
            var errorResponse = errorResponseMapper.map(e);
            assertThat(errorResponse).isEqualTo(expectedErrorResponse);
        });
    }

    @Test
    void givenWeightAlreadyExistsException_shouldReturnErrorResponse() {
        throwAndHandleException(WeightAlreadyExistsException.class, TEST_DATE, LocalDate.class, e -> {
            var expectedErrorResponse = new ErrorResponse(null, "Weight already exists for date: 2023-11-10");
            var errorResponse = errorResponseMapper.map(e);
            assertThat(errorResponse).isEqualTo(expectedErrorResponse);
        });
    }

    private <E extends RuntimeException, T> void throwAndHandleException(Class<E> exceptionType, Object parameter, Class<T> parameterType, Consumer<E> exceptionHandling) {
        try {
            throw exceptionType.getDeclaredConstructor(parameterType).newInstance(parameter);
        } catch (Exception e) {
            exceptionHandling.accept(exceptionType.cast(e));
        }
    }

    @Nested
    class MethodArgumentNotValid{
        @Test
        void givenMethodArgumentNotValidExceptionWithNonEmptyBidingResults_shouldReturnErrorResponseWithCustomMessage() {
            when(bindingResult.getFieldError()).thenReturn(fieldError);
            when(fieldError.getField()).thenReturn("testField");
            when(fieldError.getDefaultMessage()).thenReturn("default message for test field");

            try {
                throw new MethodArgumentNotValidException(methodParameter, bindingResult);
            } catch (MethodArgumentNotValidException e) {
                var expectedResponse = new ErrorResponse("testField", "default message for test field");
                var errorResponse = errorResponseMapper.map(e);
                assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
            }
        }

        @Test
        void givenMethodArgumentNotValidExceptionWithEmptyBidingResults_shouldReturnErrorResponseWithDefaultMessage() {
            when(bindingResult.getFieldError()).thenReturn(null);

            try {
                throw new MethodArgumentNotValidException(methodParameter, bindingResult);
            } catch (MethodArgumentNotValidException e) {
                var expectedResponse = new ErrorResponse(null, "Invalid request");
                var errorResponse = errorResponseMapper.map(e);
                assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
            }
        }
    }


    @Test
    void givenMethodArgumentTypeMismatchException_shouldReturnErrorResponseWithDefaultMessage() {
        Throwable throwable = mock(Throwable.class);
        String fieldName = "test field name";
        try {
            throw new MethodArgumentTypeMismatchException(null, null, fieldName, methodParameter, throwable);
        } catch (MethodArgumentTypeMismatchException e) {
            var expectedResponse = new ErrorResponse("test field name", "Invalid data type");
            var errorResponse = errorResponseMapper.map(e);
            assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
        }
    }

    @Nested
    class HttpMessageNotReadable{
        @ParameterizedTest
        @MethodSource("rootCausesAndExpectedResponses")
        void givenHttpMessageNotReadableExceptionWithNonEmptyRootCause_shouldReturnErrorResponseWithCustomMessage(Map.Entry<Throwable,ErrorResponse> entry) {
            when(httpMessageNotReadableException.getRootCause()).thenReturn(entry.getKey());

            try {
                throw httpMessageNotReadableException;
            } catch (HttpMessageNotReadableException e) {
                var errorResponse = errorResponseMapper.map(e);
                assertThat(errorResponse).usingRecursiveComparison().isEqualTo(entry.getValue());
            }
        }
        @Test
        void givenHttpMessageNotReadableExceptionWithEmptyRootCause_shouldReturnErrorResponseWithDefaultMessage() {
            when(httpMessageNotReadableException.getRootCause()).thenReturn(null);

            try {
                throw httpMessageNotReadableException;
            } catch (HttpMessageNotReadableException e) {
                var expectedResponse = new ErrorResponse(null,"Invalid request");
                var errorResponse = errorResponseMapper.map(e);
                assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
            }
        }


        static Stream<Map.Entry<Throwable, ErrorResponse>> rootCausesAndExpectedResponses() {
            DateTimeParseException dateTimeParseException = mock(DateTimeParseException.class);
            InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);
            List<JsonMappingException.Reference> path = mock(List.class);
            JsonMappingException.Reference first = mock(JsonMappingException.Reference.class);

            when(invalidFormatException.getPath()).thenReturn(path);
            when(path.getFirst()).thenReturn(first);
            when(first.getFieldName()).thenReturn("test field name");

            Map<Throwable,ErrorResponse> map = new HashMap<>();

            map.put(dateTimeParseException,new ErrorResponse("date","Date must be in format: YYYY-MM-DD"));
            map.put(invalidFormatException,new ErrorResponse("test field name","Invalid data type"));

            return map.entrySet().stream();
        }
    }


    @Test
    void givenMissingServletRequestParameterException_shouldReturnErrorResponse() {
        MissingServletRequestParameterException missingServletRequestParameterException = mock(MissingServletRequestParameterException.class);
        when(missingServletRequestParameterException.getParameterName()).thenReturn("test parameter name");

        try {
            throw missingServletRequestParameterException;
        } catch (MissingServletRequestParameterException e) {
            var expectedResponse = new ErrorResponse("test parameter name", "Parameter must not be null");
            var errorResponse = errorResponseMapper.map(e);
            assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
        }
    }

    @Test
    void givenException_shouldReturnErrorResponse() {
        Exception exception = mock(Exception.class);
        try {
            throw exception;
        } catch (Exception e) {
            var expectedResponse = new ErrorResponse(null,"Unknown error");
            var errorResponse = errorResponseMapper.map(e);
            assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
        }
    }



}