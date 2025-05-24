package com.solanteq.solar.backoffice.exception

class ArgsException(override val message: String? = null, val errorCode: ErrorCode = ErrorCode.OK) : Exception(message)