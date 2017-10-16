package com.gt.union.common.util;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16 0016.
 */
public class MockUtilTestBean {
    private static final long serialVersionUID = 1L;

    private String name;
    private int i;
    private MockUtilTestEnum mockEnum;
    private List<String> lists;

    private MockUtilTestBean(String name, MockUtilTestEnum mockEnum) {
        this.name = name;
        this.mockEnum = mockEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public MockUtilTestEnum getMockEnum() {
        return mockEnum;
    }

    public void setMockEnum(MockUtilTestEnum mockEnum) {
        this.mockEnum = mockEnum;
    }

    public List<String> getLists() {
        return lists;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }
}

enum MockUtilTestEnum {
    ONE(1), TWO(2);

    private int n;

    MockUtilTestEnum(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return String.valueOf(n);
    }
}
