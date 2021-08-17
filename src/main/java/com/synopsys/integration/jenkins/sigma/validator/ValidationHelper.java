package com.synopsys.integration.jenkins.sigma.validator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ValidationHelper {
    public static Set<String> RESERVED_ARGUMENT_NAMES;

    // Please note that if you modify this list check the help HTML files to determine if the files need to be updated.
    static {
        RESERVED_ARGUMENT_NAMES = new HashSet<>();
        RESERVED_ARGUMENT_NAMES.add("config");
        RESERVED_ARGUMENT_NAMES.add("policy");
        RESERVED_ARGUMENT_NAMES.add("f");
        RESERVED_ARGUMENT_NAMES.add("format");
        RESERVED_ARGUMENT_NAMES.add("w");
        RESERVED_ARGUMENT_NAMES.add("working-dir");
    }

    private ValidationHelper() {
        // prevent construction
    }

    public static boolean isFormFieldEmpty(String value) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return false;
    }

    public static boolean isNameValid(String name) {
        String processedName = name.trim();
        if (processedName.startsWith("--")) {
            processedName = processedName.substring(2);
        } else if (processedName.startsWith("-")) {
            processedName = processedName.substring(1);
        }
        if (RESERVED_ARGUMENT_NAMES.contains(processedName.toLowerCase())) {
            return false;
        }

        return true;
    }
}
