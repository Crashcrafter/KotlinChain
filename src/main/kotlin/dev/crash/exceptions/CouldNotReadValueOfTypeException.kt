package dev.crash.exceptions

class CouldNotReadValueOfTypeException(type: String) : Exception() {
    override val message: String = "Could not read Value of type $type from packet!"
}
