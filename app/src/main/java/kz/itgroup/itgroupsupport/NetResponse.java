package kz.itgroup.itgroupsupport;

public class NetResponse<ResponseType> {

    private ResponseType response;
    private int codeResponse;

    public NetResponse(ResponseType response, int code) {
        this.response = response;
        this.codeResponse = code;
    }

    public int getCodeResponse() {
        return codeResponse;
    }

    public ResponseType getResponse() {
        return response;
    }
}