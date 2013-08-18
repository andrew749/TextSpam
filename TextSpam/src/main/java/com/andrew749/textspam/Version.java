package com.andrew749.textspam;

import java.util.ArrayList;

/**
 * Created by andrew on 18/08/13.
 */
public class Version {
    int versionNumber;
    ArrayList<String> changes = new ArrayList<String>();

    public Version(int number) {
        versionNumber = number;
    }

    public void addChange(String change) {
        changes.add(change);
    }
}
