package pl.bsotniczuk.hygrosense.model;

public class ResponseBody {

    private String response;

    public ResponseBody() {
    }

    public ResponseBody(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
