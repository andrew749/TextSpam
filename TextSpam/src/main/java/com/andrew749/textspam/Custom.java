package com.andrew749.textspam;

import java.io.Serializable;

/**
 * Created by andrew on 25/06/13.
 */
public class Custom implements Serializable {
    String phonenumber;

    public Custom(String number) {
        phonenumber = number;
    }

    public String getPhoneNumber() {
        return phonenumber;
    }

}
