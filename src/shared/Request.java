package shared;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operation;
    private Object data;
    private String username;

    public Request() {
    }

    public Request(String operation, Object data) {
        this.operation = operation;
        this.data = data;
    }

    public Request(String operation, Object data, String username) {
        this.operation = operation;
        this.data = data;
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
