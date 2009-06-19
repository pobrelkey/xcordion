package xcordion.api;

public enum IgnoreState {
    // in an authoritative portion of the test
    NORMATIVE,

    // in a portion of the test which is executed, but whose results don't count towards the success/failure of the test
    IGNORED,

    // in a portion of the test which is not executed
    OMITTED
}
