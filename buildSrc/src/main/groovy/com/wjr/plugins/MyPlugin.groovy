package com.wjr.plugins

import com.google.gson.Gson
import com.wjr.plugins.models.FeishuObj
import groovyx.net.http.HttpBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

// 飞书：https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482
class MyPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('pluginTest') {
            doLast {
                def result = HttpBuilder.configure {
                    request.uri = 'https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482'
                    FeishuObj obj = new FeishuObj()
                    obj.title = "测试标题"
                    obj.text = "测试内容"
                    request.body = new Gson().toJson(obj)
                    println(request.body)
                    request.charset = 'UTF-8'
                    request.contentType = 'application/json'
                }.post()
                println result
                def env = System.getenv()
                println(env)
            }
        }
    }
}