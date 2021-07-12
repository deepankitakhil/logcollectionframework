package validationservice;

import exception.InvalidInputException;

public interface IValidator<T> {

    void validate(T input) throws InvalidInputException;
}
