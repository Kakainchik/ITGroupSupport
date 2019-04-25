package kz.itgroup.itgroupsupport;

import java.io.Serializable;

public class TokenFile implements Serializable {

    private TokenModel token;
    private String fullFileName;

    public TokenFile(TokenModel token, String fullName) {
        this.token = token;
        this.fullFileName = fullName;
    }

    public String getFullFileName() {
        return fullFileName;
    }

    public TokenModel getToken() {
        return token;
    }
}
