package validationservice;

import exception.InvalidInputException;

import java.io.File;

public class FileValidator implements IValidator<File> {

    private static final String EMPTY_FILE = "";

    @Override
    public void validate(File input) throws InvalidInputException {
        if (input == null || !input.isFile() || input.isHidden() || !input.canRead()) {
            throw new InvalidInputException(
                    String.format(
                            "%s threw exception for input %s",
                            FileValidator.class.getName(),
                            input == null ? EMPTY_FILE : input.toString()));
        }
    }
}
