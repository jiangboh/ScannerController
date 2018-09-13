package com.bravo.xml;

import java.util.HashMap;

/**
 * Created by admin on 2018-9-12.
 */

public class Name_DIC_Struct {
    public String name;
    public HashMap<String, Object> dic;

    public Name_DIC_Struct(String name)
    {
        this.name = name;
        dic = new HashMap<String, Object>();
    }

    public Name_DIC_Struct()
    {
        this("");
    }
}
