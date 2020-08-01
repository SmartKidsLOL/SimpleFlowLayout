package com.wjr.plugins

import com.google.gson.Gson
import com.wjr.plugins.models.FeishuObj
import groovyx.net.http.HttpBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree

// 飞书：https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482
class MyPlugin implements Plugin<Project> {
    String serverJenkinsSourcePath = "/Users/timedomain/.jenkins/workspace/"
    String serverJenkinsJobPath = "/Users/timedomain/.jenkins/jobs/"
    String defaultApkPath = "/app/build/outputs/apk/release/"
    String defaultArtifactsPath = "artifact${defaultApkPath}"

    @Override
    void apply(Project project) {
        project.task('pluginTest') {
            doLast {
                def result = HttpBuilder.configure {
                    request.uri = 'https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482'
                    FeishuObj obj = new FeishuObj()

                    def env = System.getenv()
                    def buildUrl = env['BUILD_URL']
                    def sourceApkPath = "${serverJenkinsSourcePath}${env['JOB_NAME']}/${defaultApkPath}"

                    def fileTree = project.fileTree(sourceApkPath).findAll {
                        it.name.endsWith(".apk")
                    }

                    File t = new File(sourceApkPath)
                    println('文件测试：' + t.getPath() + "\n" + t.listFiles())

                    String title = "项目：${env['JOB_NAME']} 版本：${env['BUILD_NUMBER']} 构建完成"
                    StringBuilder contentStr = new StringBuilder()
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
                    contentStr.append("\n")
                            .append("如果点击无效，请稍等几秒后打开，文件正在归档中")
                    obj.title = title + "构建成功"
                    obj.text = contentStr.toString()

                    request.body = new Gson().toJson(obj)
                    request.charset = 'UTF-8'
                    request.contentType = 'application/json'
                }.post()
                println result
            }
        }
    }
}