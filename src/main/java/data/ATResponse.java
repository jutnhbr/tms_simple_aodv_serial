package data;

public enum ATResponse {

    AT_OK("AT,OK"),
    AT_ERR("AT,CMDERR"),
    AT_VER("AT,VER=1.0.0"),
    AT_RSSI("AT,RSSI=-50"),
    AT_ADDR("AT,ADDR=FF02");

    private final String response;

    ATResponse(String response) {
        this.response = response;
    }

    public String getCommand() {
        return response;
    }

}
