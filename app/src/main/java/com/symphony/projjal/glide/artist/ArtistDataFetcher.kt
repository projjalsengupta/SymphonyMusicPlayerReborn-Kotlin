package com.symphony.projjal.glide.artist

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.symphony.bitmaputils.BitmapUtils
import com.symphony.bitmaputils.BitmapUtils.getBytes
import com.symphony.mediastorequery.model.Artist
import com.symphony.projjal.AUDIOSCROBBLER_API_KEY
import com.symphony.projjal.FANART_API_KEY
import com.symphony.projjal.methodcalls.MethodCalls
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URLEncoder
import java.nio.ByteBuffer

class ArtistDataFetcher internal constructor(val model: Artist, val context: Context?) :
    DataFetcher<ByteBuffer> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        var data: ByteArray? = getArtistImageByteArrayFromName(model.name)
        if (data == null && model.songCount > 0) {
            data = getByteDataFromUri(model.songs[0].albumArtUri)
        }
        if (data != null) {
            callback.onDataReady(ByteBuffer.wrap(data))
        } else {
            callback.onDataReady(null)
        }
    }

    override fun cleanup() {}
    override fun cancel() {}
    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    private fun getByteDataFromUri(uri: Uri): ByteArray? {
        context?.let { context1 ->
            context1.contentResolver.openInputStream(uri).use { inputStream ->
                return inputStream?.let { inputStream1 -> getBytes(inputStream1) }
            }
        }
        return null
    }

    private fun getArtistImageByteArrayFromName(artistName: String): ByteArray? {
        if (artistName == "") {
            return null
        }
        var url: String? =
            "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + URLEncoder.encode(
                artistName,
                "UTF-8"
            ) + "&api_key=" + AUDIOSCROBBLER_API_KEY + "&format=json&autocorrect=1"
        var response: String? = MethodCalls.get(url!!)
        if (response != null) {
            Log.d("RESPONSE", response)
        }
        if (response != null) {
            val id = getArtistMBIDFrom(response)
            if (id != null) {
                url =
                    "https://webservice.fanart.tv/v3/music/" + id + "&?api_key=" + FANART_API_KEY + "&format=json"
                response = MethodCalls.get(url)
                if (response != null) {
                    Log.d("RESPONSE", response)
                }
                if (response != null) {
                    url = getArtistImageURL(response)
                    if (url != null && url != "") {
                        return BitmapUtils.decodeUrl(url)
                    }
                }
                url = "http://musicbrainz.org/ws/2/artist/" + URLEncoder.encode(
                    id,
                    "UTF-8"
                ) + "?inc=url-rels"
                response = MethodCalls.get(url)
                if (response != null) {
                    Log.d("RESPONSE", response)
                }
                if (response != null) {
                    url = getArtistImageURLFromXML(response)
                    if (url != null) {
                        return BitmapUtils.decodeUrl(url)
                    }
                }
            }
        }
        return BitmapUtils.decodeUrl(
            "https://tse2.mm.bing.net/th?q=" + URLEncoder.encode(
                artistName,
                "UTF-8"
            ) + "%20artist+spotify.com&w=500&h=500&c=7&rs=1&p=0&dpr=3&pid=1.7&mkt=en-IN&adlt=on%27"
        )
    }

    private fun getArtistImageURLFromXML(response: String?): String? {
        var responseString = response
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(StringReader(responseString))
            var eventType = xpp.eventType
            var url: String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xpp.name == "relation" && xpp.getAttributeValue(
                        null,
                        "type"
                    ) == "image"
                ) {
                    xpp.next()
                    url = xpp.nextText()
                    break
                }
                eventType = xpp.next()
            }
            if (url != null) {
                url =
                    "https://commons.wikimedia.org/w/api.php?action=query&amp;prop=imageinfo&amp;iiprop=url&amp;redirects&amp;format=json&amp;iiurlwidth=500&amp;titles=" + url.substring(
                        url.indexOf("File:")
                    )
                responseString = MethodCalls[url]
                if (responseString != null) {
                    try {
                        var jsonObject = JSONObject(responseString)
                        jsonObject = jsonObject.getJSONObject("query")
                        jsonObject = jsonObject.getJSONObject("pages")
                        val keys = jsonObject.keys()
                        val id = keys.next().toString()
                        val `object` = jsonObject.getJSONObject(id)
                        val jsonArray = `object`.getJSONArray("imageinfo")
                        val imageObject = jsonArray[0] as JSONObject
                        return imageObject.getString("thumburl")
                    } catch (ignored: Exception) {
                    }
                }
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    private fun getArtistMBIDFrom(json: String): String? {
        return try {
            var jsonObject = JSONObject(json)
            jsonObject = jsonObject.getJSONObject("artist")
            jsonObject.getString("mbid")
        } catch (ignored: Exception) {
            null
        }
    }

    private fun getArtistImageURL(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            val jsonArray = jsonObject.getJSONArray("artistthumb")
            (jsonArray[0] as JSONObject).getString("url")
        } catch (ignored: Exception) {
            null
        }
    }
}
