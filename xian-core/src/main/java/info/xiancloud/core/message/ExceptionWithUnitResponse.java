package info.xiancloud.core.message;

/**
 * An exception containing a unit response object
 *
 * @author happyyangyuan
 */
public class ExceptionWithUnitResponse extends Exception {
    private UnitResponse unitResponse;

    public UnitResponse getUnitResponse() {
        return unitResponse;
    }

    public ExceptionWithUnitResponse(UnitResponse unitResponse) {
        setUnitResponse(unitResponse);
    }


    public ExceptionWithUnitResponse setUnitResponse(UnitResponse unitResponse) {
        this.unitResponse = unitResponse;
        return this;
    }
}
