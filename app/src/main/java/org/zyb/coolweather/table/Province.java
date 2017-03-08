package org.zyb.coolweather.table;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/4.
 */

public class Province extends DataSupport {

    private int id;
    private int provinceId;
    private String provinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
