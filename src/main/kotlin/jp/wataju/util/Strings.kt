package jp.wataju.util

import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

object Strings {

    fun get(
        tag: String,
        id: Int
    ): String {

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(Paths.get("src/main/resources/values/strings.xml").toFile())
        val languageList = document.documentElement
        val languages = languageList.getElementsByTagName(tag)

        return languages.item(id).textContent

    }

}
