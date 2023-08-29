package by.bashlikovv.messenger.data

sealed class AppException : RuntimeException {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

class EmptyFieldException : AppException()

class PasswordMismatchException : AppException()

class AccountAlreadyExistsException : AppException()

class AuthException : AppException()

class StorageException: AppException()

class ParseBackendResponseException(
    cause: Throwable
) : AppException(cause = cause)