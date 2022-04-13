package mybatis.parser.model;

public enum ResolvingType {
    RegisteredParameterTypeFound(true),
    PropertyNameNotFound(false),
    CursorPropertyTypeFound(true),
    MapParameterTypeFound(false),
    GetterFound(true),
    GetterNotFound(false),
    Unresolved(false);

    boolean isResolved = false;

    ResolvingType(boolean isResolved) {
        this.isResolved = isResolved;
    }

    public boolean isResolved() {
        return isResolved;
    }
}
