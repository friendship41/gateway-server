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

