package com.example.myplugin;

/**
 * 宿主使用插件中的类
 */
public class TestPluginClass {
    private String Name;
    private Integer Version;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getVersion() {
        return Version;
    }

    public void setVersion(Integer version) {
        this.Version = version;
    }
}
