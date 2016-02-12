package util;


public enum LEVEL implements HasDisplayName {
    //0,1,2,3
    ERROR, WARN, INFO, DEBUG, TRACE;

    @Override
    public String getDisplayName() {
        return this.name();
    }
}
