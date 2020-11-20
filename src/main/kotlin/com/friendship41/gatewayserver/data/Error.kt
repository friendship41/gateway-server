package com.friendship41.gatewayserver.data

data class ErrorResponse(
        var message: String?,
        var status: Int,
        var errorCode: String?,
        var errors: List<ErrorCause> = ArrayList()
) {
    data class ErrorCause(
            var field: String,
            var value: String,
            var reason: String
    )
}

enum class CommonErrorCode(val status: Int, val code: String, val message: String) {
    INVALID_INPUT_VALUE(400, "CE001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "CE002", "Not Allowed Request"),
    UNAUTHORIZED(401, "CE003", "Unauthorized, Access is Denied"),
    INTERNAL_SERVER_ERROR(500, "CE004", "Internal Server Error"),
    BAD_CREDENTIALS(401, "CE005", "Bad Credentials");

    fun toErrorResponse(): ErrorResponse = ErrorResponse(this.message, this.status, this.code)
}
