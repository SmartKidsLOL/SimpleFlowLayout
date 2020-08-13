package com.wjr.plugins

import com.google.gson.Gson
import com.wjr.plugins.models.FeiShuObj
import groovyx.net.http.HttpBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

// 飞书：https://open.feishu.cn/open-apis/bot/hook/38ce74e6df4b423c89de4bd028050482
class MyPlugin implements Plugin<Project> {
    String serverJenkinsSourcePath = "/Users/timedomain/.jenkins/workspace/"
    String defaultApkPath = "/outputs/"
    String defaultArtifactsPath = "artifact${defaultApkPath}"
    String feiShuBotWebHook = "https://open.feishu.cn/open-apis/bot/hook/a523c0aa8eb24b88b72d8697b7c09de9"

    @Override
    void apply(Project project) {
        project.task('buildAndroidJenkins') {
            doLast {
                def result = HttpBuilder.configure {
                    request.uri = feiShuBotWebHook
                    FeiShuObj obj = new FeiShuObj()

                    def env = System.getenv()
                    def buildUrl = env['BUILD_URL']
                    def sourceApkPath = "${serverJenkinsSourcePath}${env['JOB_NAME']}/${defaultApkPath}"

                    def fileTree = project.fileTree(sourceApkPath).findAll {
                        it.name.endsWith(".apk")
                    }

                    def appGradleFile = new File("${serverJenkinsSourcePath}${env['JOB_NAME']}/build.gradle")
                    def versionCode = null
                    def versionName = null
                    appGradleFile.eachLine { line ->
                        if (line.contains("appVersionCode")) {
                            def res1 = line.split(":")[1]
                            versionCode = res1.contains(",") ? res1.substring(0, res1.length() - 1) : res1
                        }
                        if (line.contains("appVersionName")) {
                            def res2 = line.split(":")[1]
                            versionName = res2.contains(",") ? res2.substring(0, res2.length() - 1) : res2
                        }
                    }

                    String title = "项目：${env['JOB_NAME']} 第${env['BUILD_NUMBER']}次 构建完成"
                    StringBuilder contentStr = new StringBuilder()
                    contentStr.append("\n")
                            .append("分支为->")
                            .append("${env['GIT_BRANCH']}")
                            .append("\n")
                            .append("versionCode：")
                            .append(versionCode)
                            .append("\n")
                            .append("versionName：")
                            .append(versionName)
                            .append("\n")
                    fileTree.each { File file ->
                        def filePrefixName
                        if (file.name.contains(".")) {
                            filePrefixName = file.name.substring(0, file.name.lastIndexOf("."))
                        } else {
                            filePrefixName = file.name
                        }
                        def nameArr = file.name.split("_")
                        def finaName = filePrefixName
                        if (nameArr != null && nameArr.length > 2) {
                            if (filePrefixName.contains("release")) {
                                def releaseIndex = filePrefixName.indexOf("release")
                                def subResult = filePrefixName.substring(0, releaseIndex)
                                finaName = "${subResult.split("_")[subResult.length() - 1]}_release.apk"
                            } else if (filePrefixName.contains("debug")) {
                                def debugIndex = filePrefixName.indexOf("debug")
                                def subResult = filePrefixName.substring(0, debugIndex)
                                finaName = "${subResult.split("_")[subResult.length() - 1]}_debug.apk"
                            }
                        }
                        contentStr.append("\n")
                                .append("文件：")
                                .append(finaName)
                                .append(" 下载地址为：")
                                .append("\n")
                                .append(buildUrl)
                                .append(defaultArtifactsPath)
                                .append(file.name)
                    }
                    contentStr.append("\n\n")
                            .append("如果点击无效，请稍等几秒后打开，文件正在归档中")
                    obj.title = title
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