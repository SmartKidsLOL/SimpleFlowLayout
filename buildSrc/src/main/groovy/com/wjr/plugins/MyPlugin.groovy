package com.wjr.plugins

import com.google.gson.Gson
import com.wjr.plugins.models.FeishuObj
import groovyx.net.http.HttpBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree

// 飞书：https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482
class MyPlugin implements Plugin<Project> {
    String serverJenkinsJobPath = "/Users/timedomain/.jenkins/jobs/"
    String defaultArtifactsPath = "artifact/app/build/outputs/apk/release/"

    @Override
    void apply(Project project) {
        project.task('pluginTest') {
            doLast {
                def result = HttpBuilder.configure {
                    request.uri = 'https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482'
                    FeishuObj obj = new FeishuObj()
                    def env = System.getenv()
                    def buildUrl = env['BUILD_URL']
                    String title = "项目：${env['JOB_NAME']} 版本：${env['BUILD_NUMBER']} "
                    StringBuilder contentStr = new StringBuilder()
                    ConfigurableFileTree fileTree = project.fileTree(
                            "${serverJenkinsJobPath}${env['JOB_NAME']}/${env['BUILD_NUMBER']}/${defaultArtifactsPath}")
                    println('文件测试：' +
                            new File("/Users/timedomain/.jenkins/jobs/").listFiles())
                    if (fileTree == null || fileTree.size() == 0) {
                        obj.title = title + "构建失败"
                        obj.text = "详情请查看Log：${env['BUILD_URL']}console"
                    } else {
                        fileTree.each { File file ->
                            def filePrefixName
                            if (file.name.contains(".")) {
                                filePrefixName = file.name.substring(0, file.name.lastIndexOf("."))
                            } else {
                                filePrefixName = name
                            }
                            contentStr.append("\n")
                                    .append("文件：")
                                    .append(filePrefixName)
                                    .append(" 下载地址为：")
                                    .append(buildUrl)
                                    .append(defaultArtifactsPath)
                                    .append(file.name)
                        }
                        obj.title = title
                        obj.text = contentStr.toString()
                    }

                    request.body = new Gson().toJson(obj)
                    request.charset = 'UTF-8'
                    request.contentType = 'application/json'
                }.post()
                println result
            }
        }
    }
}