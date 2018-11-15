package com.wjr.simple_flow_layout;

/**
 * 简易标签类
 */
public class SimpleTag {
    private String content;
    private int num;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SimpleTag{" +
                "content='" + content + '\'' +
                ", num=" + num +
                '}';
    }
}
