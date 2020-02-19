package com.takhaki.idolbloglistener.parser

import android.util.Xml
import com.takhaki.idolbloglistener.Model.Entry
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class BlogXmlParser(private val name: String) {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<*> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Entry> {
        val entries = mutableListOf<Entry>()

        parser.require(XmlPullParser.START_TAG, name, "feed")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "entry") {
                entries.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Entry {
        parser.require(XmlPullParser.START_TAG, name, "entry")
        var title: String? = null
        var summary: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readTitle(parser)
                "summary" -> summary = readSummary(parser)
                "link" -> link = readLink(parser)
                else -> skip(parser)
            }
        }
        return Entry(title, summary, link)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, name, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, name, "title")
        return title
    }

    // Processes link tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, name, "link")
        val tag = parser.name
        val relType = parser.getAttributeValue(null, "rel")
        if (tag == "link") {
            if (relType == "alternate") {
                link = parser.getAttributeValue(null, "href")
                parser.nextTag()
            }
        }
        parser.require(XmlPullParser.END_TAG, name, "link")
        return link
    }

    // Processes summary tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readSummary(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, name, "summary")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, name, "summary")
        return summary
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}