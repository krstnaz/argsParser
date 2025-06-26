package org.kia.exception

import org.kia.exception.code.ErrorCode

class ArgsException(override val message: String? = null, val errorCode: ErrorCode = ErrorCode.OK) : Exception(message)