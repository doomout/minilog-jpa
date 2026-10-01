package com.asdf.minilog.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class MinilogGraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {
    private static final Logger logger =
            LoggerFactory.getLogger(MinilogGraphQLExceptionResolver.class);

    @Override
    protected GraphQLError resolveToSingleError(Throwable exception, DataFetchingEnvironment env) {
        // 발생한 예외 정보를 로그에 기록
        logger.error(
                "GraphQL Exception at {}: {}",
                env.getExecutionStepInfo().getPath(),
                exception.getMessage(),
                exception);

        GraphQLError error;
        var handlerParameterPath = env.getExecutionStepInfo().getPath();

        if (exception instanceof ArticleNotFoundException || exception instanceof UserNotFoundException) {
            error =
                    GraphqlErrorBuilder.newError()
                            .message(exception.getMessage())
                            .errorType(ErrorType.NOT_FOUND)
                            .path(handlerParameterPath)
                            .build();
        } else if (exception instanceof NotAuthorizedException) {
            error =
                    GraphqlErrorBuilder.newError()
                            .message(exception.getMessage())
                            .errorType(ErrorType.UNAUTHORIZED)
                            .path(handlerParameterPath)
                            .build();
        } else if (exception instanceof IllegalArgumentException) {
            error =
                    GraphqlErrorBuilder.newError()
                            .message(exception.getMessage())
                            .errorType(ErrorType.BAD_REQUEST)
                            .path(handlerParameterPath)
                            .build();
        } else {
            error =
                    GraphqlErrorBuilder.newError()
                            .message("Internal Server Error")
                            .errorType(ErrorType.INTERNAL_ERROR)
                            .path(handlerParameterPath)
                            .build();
        }

        return error;
    }
}
