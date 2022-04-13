package org.javacs.example;

import com.google.gson.*;

public class UsingGson<Param> {
    public void test() {
        Gson gson = new Gson();
        gson.toJson(1);
    }

    void testGotoPackagePrivate() {
        var x = GotoPackagePrivate.PUBLIC_FIELD;
    }
}